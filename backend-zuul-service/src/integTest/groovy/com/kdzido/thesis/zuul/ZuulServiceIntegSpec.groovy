package com.kdzido.thesis.zuul

import groovyx.net.http.RESTClient
import org.springframework.http.MediaType
import spock.lang.Specification
import spock.lang.Stepwise

import java.util.concurrent.TimeUnit

import static groovyx.net.http.ContentType.URLENC
import static org.awaitility.Awaitility.await

/**
 * @author krzysztof.dzido@gmail.com
 */
@Stepwise
class ZuulServiceIntegSpec extends Specification {

    final static AUTHSERVICE_URI = System.getenv("AUTHSERVICE_URI")
    final static ZUULSERVICE_URI = System.getenv("ZUULSERVICE_URI")

    def authServiceClient = new RESTClient("$AUTHSERVICE_URI").with {
        setHeaders(Accept: MediaType.APPLICATION_JSON_VALUE)
        it
    }
    def zuulServiceClient = new RESTClient("$ZUULSERVICE_URI").with {
        setHeaders(Accept: MediaType.APPLICATION_JSON_VALUE)
        it
    }

    def "that zuul discovered (from eureka) route to sampleservice"() {
        expect:
        await().atMost(6, TimeUnit.MINUTES).pollInterval(1, TimeUnit.SECONDS).until({
            try {
                def resp = zuulServiceClient.get(path: "/routes")

                return resp.status == 200 &&
                        resp.headers.'Content-Type'.contains(MediaType.APPLICATION_JSON_VALUE) &&
                        resp.data."/api/sampleservice/**" == "sampleservice"
            } catch (e) {
                return false
            }
        })
    }

    def "that sampleservice is called through zuul with access token"() {
        setup:
        authServiceClient.auth.basic("newsapp", "newsappsecret")
        final passwordGrant = [
                grant_type: "password",
                scope: "mobileclient",
                username: "reader",
                password: "readerpassword"]

        expect:
        await().atMost(6, TimeUnit.MINUTES).pollInterval(1, TimeUnit.SECONDS).until({
            try {
                def authServerResp = authServiceClient.post(
                        path: "/auth/oauth/token",
                        body: passwordGrant,
                        requestContentType : URLENC)    // TODO multipart/form-data

                // token extracted
                final String accessToken = authServerResp.data.'access_token'
                assert accessToken.isEmpty() == false

                def zuulServiceClient = new RESTClient("$ZUULSERVICE_URI").with {
                    setHeaders(Accept: MediaType.APPLICATION_JSON_VALUE,
                            Authorization: "Bearer $accessToken")
                    it
                }
                def resp = zuulServiceClient.get(path: "/api/sampleservice/v1/config")

                return resp.status == 200 &&
                        resp.headers.'Content-Type'.contains(MediaType.APPLICATION_JSON_VALUE) &&
                        resp.data.plain == "This is a Git-backed test property for the sampleservice (default)" &&
                        resp.data.cipher == "password"
            } catch (e) {
                return false
            }
        })
    }

    def "that zuul rejects invalid access token to protected service"() {
        setup:
        authServiceClient.auth.basic("newsapp", "newsappsecret")

        expect:
        await().atMost(6, TimeUnit.MINUTES).pollInterval(1, TimeUnit.SECONDS).until({
            try {
                def zuulServiceClient = new RESTClient("$ZUULSERVICE_URI").with {
                    setHeaders(Accept: MediaType.APPLICATION_JSON_VALUE,
                            Authorization: "Bearer INVALID_TOKEN")
                    it
                }
                zuulServiceClient.get(path: "/api/sampleservice/v1/config")
                return false
            } catch (e) {
                assert e.response.status == 401
                return true
            }
        })
    }


    // TODO test load balancing
    // TODO test circuit breaker
    // TODO test timeouts

    // TODO test correlation ID

}
