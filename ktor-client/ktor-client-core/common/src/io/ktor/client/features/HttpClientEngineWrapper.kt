/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.features

import io.ktor.client.request.*
import io.ktor.client.engine.*
import io.ktor.util.*

/**
 * Engine wrapper that wraps execution of [HttpClientEngine.execute].
 */
internal class HttpClientEngineWrapper {
    /**
     * The actual wrappers that accepts initial [HttpClientEngineExecutor] as a receiver and returns another or the same
     * [HttpClientEngineExecutor] as a result.
     */
    internal var executorWrapper: HttpClientEngineExecutor.() -> HttpClientEngineExecutor = { this }

    /**
     * Wraps the engine. If the engine is already wrapped the result will be the combination of wrappers already applied
     * and the new wrapper.
     */
    internal fun wrap(block: suspend HttpClientEngineExecutor.(HttpRequestData) -> HttpResponseData) {
        executorWrapper = { { data -> block(data) } }
    }

    companion object {
        /**
         * Key to be used to access wrapper in a collection of [Attributes].
         */
        val key: AttributeKey<HttpClientEngineWrapper> = AttributeKey("wrapper")
    }
}

/**
 * Executor alias is a function that accepts [HttpRequestData] and returns [HttpResponseData].
 */
internal typealias HttpClientEngineExecutor = suspend (HttpRequestData) -> HttpResponseData
