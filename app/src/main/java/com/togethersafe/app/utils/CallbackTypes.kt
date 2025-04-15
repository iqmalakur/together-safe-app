package com.togethersafe.app.utils

typealias ApiSuccessCallback<T> = (data: T) -> Unit
typealias ApiErrorCallback = suspend (status: Int, messages: List<String>) -> Unit
