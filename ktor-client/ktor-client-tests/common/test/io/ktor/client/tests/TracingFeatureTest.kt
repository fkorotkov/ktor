/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.tests

import io.ktor.client.features.tracing.*
import io.ktor.client.request.*
import io.ktor.client.tests.utils.*
import kotlin.test.*

class TracingFeatureTest : ClientLoader() {
    @Test
    fun testTrace() = clientTests {
        val testTracer = TestTracer()

        config {
            install(TracingFeature) {
                tracer = testTracer
            }
        }

        test { client ->
            val res: String = client.get("$TEST_SERVER/logging")
            assertEquals("home page", res)
            assertEquals(0, testTracer.httpExchangeFailedCount)
            assertEquals(1, testTracer.interpretResponseCount)
            assertEquals(1, testTracer.requestWillBeSentCount)
            assertEquals(1, testTracer.responseHeadersReceivedCount)
            assertEquals(1, testTracer.responseReadFinishedCount)
        }
    }
}

private class TestTracer : Tracer {

    var httpExchangeFailedCount = 0
    var interpretResponseCount = 0
    var requestWillBeSentCount = 0
    var responseHeadersReceivedCount = 0
    var responseReadFinishedCount = 0

    override fun httpExchangeFailed(requestId: String, message: String) {
        httpExchangeFailedCount++
    }

    override fun interpretResponse(
        requestId: String,
        contentType: String?,
        contentEncoding: String?,
        body: Any?
    ): Any? {
        interpretResponseCount++
        return body
    }

    override fun requestWillBeSent(requestId: String, requestData: HttpRequestData) {
        requestWillBeSentCount++
    }

    override fun responseHeadersReceived(
        requestId: String,
        requestData: HttpRequestData,
        responseData: HttpResponseData
    ) {
        responseHeadersReceivedCount++
    }

    override fun responseReadFinished(requestId: String) {
        responseReadFinishedCount++
    }
}
