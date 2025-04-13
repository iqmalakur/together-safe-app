package com.togethersafe.app.utils

typealias ApiSuccessCallback<T> = (data: T) -> Unit
typealias ApiErrorCallback = (status: Int, messages: List<String>) -> Unit
