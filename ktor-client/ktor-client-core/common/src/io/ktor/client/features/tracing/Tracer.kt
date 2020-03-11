/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.features.tracing

import io.ktor.client.request.*

/**
 * Tracer interface invoked at crucial points of the request processing to handle important events such as a start of
 * the request processing, a receiving of response headers, a receiving of data and so on. Implementations of this
 * interface are responsible for saving and presenting these events.
 */
interface Tracer {
    /**
     * Indicates that the request processing has been start and request will be sent soon.
     */
    fun requestWillBeSent(requestId: String, requestData: HttpRequestData)

    /**
     * Indicates that the response processing has been started and headers were read.
     */
    fun responseHeadersReceived(requestId: String, requestData: HttpRequestData, responseData: HttpResponseData)

    /**
     * Wraps input channel to pass it to underlying implementation.
     */
    fun interpretResponse(requestId: String, contentType: String?, contentEncoding: String?, body: Any?) : Any?

    /**
     * Indicates that communication with the server has failed.
     */
    fun httpExchangeFailed(requestId: String, message: String)

    /**
     * Indicates that communication with the server has finished.
     */
    fun responseReadFinished(requestId: String)
}
