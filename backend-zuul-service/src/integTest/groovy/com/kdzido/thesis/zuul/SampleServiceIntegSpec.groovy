package com.kdzido.thesis.zuul

import groovyx.net.http.RESTClient
import org.springframework.http.MediaType
import spock.lang.Specification
import spock.lang.Stepwise

import java.util.concurrent.TimeUnit

import static org.awaitility.Awaitility.await

/**
 * @author krzysztof.dzido@gmail.com
 */
@Stepwise
class SampleServiceIntegSpec extends Specification {

    final static ZUULSERVICE_URI = System.getenv("ZUULSERVICE_URI")

    def zuulServiceClient = new RESTClient("$ZUULSERVICE_URI").with {
        setHeaders(Accept: MediaType.APPLICATION_JSON_VALUE)
        it
    }

    def "that zuul discovered (from eureka) route to sampleservice"() {
        expect:
        await().atMost(4, TimeUnit.MINUTES).until({
            try {
                def resp = zuulServiceClient.get(path: "/routes")

                return resp.status == 200 &&
                        resp.headers.'Content-Type'.contains(MediaType.APPLICATION_JSON_VALUE) &&
                        resp.data."/sampleservice/**" == "sampleservice"
            } catch (e) {
                return false
            }
        })
    }

    def "that sampleservice is called through zuul"() {
        expect:
        await().atMost(4, TimeUnit.MINUTES).until({
            try {
                def resp = zuulServiceClient.get(path: "/sampleservice/v1/config")

                return resp.status == 200 &&
                        resp.headers.'Content-Type'.contains(MediaType.APPLICATION_JSON_VALUE) &&
                        resp.data.plain == "This is a Git-backed test property for the sampleservice (default)" &&
                        resp.data.cipher == "password"
            } catch (e) {
                return false
            }
        })
    }

    // TODO test load balancing
    // TODO test circuit breaker
    // TODO test timeouts

    // TODO test correlation ID
    // TODO test auth

}
