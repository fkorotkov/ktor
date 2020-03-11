/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.features.tracing

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*

/**
 * Client tracing feature. Allows to trace HTTP request processing using specified [Tracer]. This feature requires
 * tracer to be specified via configuration.
 */
class TracingFeature internal constructor(val tracer: Tracer) {
    /**
     * Tracing feature configuration.
     */
    class Config {
        /**
         * Tracer responsible for saving and presenting request processing events.
         */
        var tracer: Tracer? = null
    }

    companion object Feature : HttpClientFeature<Config, TracingFeature>, HttpClientEngineCapability<Tracer> {
        override val key: AttributeKey<TracingFeature> = AttributeKey("Tracing")

        override fun install(feature: TracingFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                var engineWrapper = context.attributes.getOrNull(HttpClientEngineWrapper.key)

                if (engineWrapper == null) {
                    engineWrapper = HttpClientEngineWrapper()
                    context.attributes.put(HttpClientEngineWrapper.key, engineWrapper)
                }

                var sequence = 0

                engineWrapper.wrap { requestData ->
                    val requestId = "${sequence++}"
                    feature.tracer.requestWillBeSent(requestId, requestData)
                    try {
                        val result = this(requestData)
                        feature.tracer.responseHeadersReceived(requestId, requestData, result)

                        context.executionContext.invokeOnCompletion {
                            feature.tracer.responseReadFinished(requestId)
                        }

                        with(result) {
                            HttpResponseData(
                                statusCode,
                                requestTime,
                                headers,
                                version,
                                feature.tracer.interpretResponse(
                                    requestId,
                                    headers[HttpHeaders.ContentType],
                                    headers[HttpHeaders.ContentEncoding],
                                    result.body
                                )!!,
                                callContext
                            )
                        }
                    } catch (cause: Throwable) {
                        feature.tracer.httpExchangeFailed(requestId, cause.message!!)
                        throw cause
                    }
                }
            }
        }

        override fun prepare(block: Config.() -> Unit): TracingFeature {
            val tracer = Config().apply(block).tracer
            if (tracer == null) {
                error("Tracing feature configuration requires tracer to be specified")
            } else {
                return TracingFeature(tracer)
            }
        }
    }
}
