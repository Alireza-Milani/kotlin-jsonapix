package com.infinum.jsonapix.core.shared

import com.infinum.jsonapix.core.common.JsonApiXMissingArgumentException

fun <T : Any> requireNotNull(value: T?, missingArgument: String): T {
    if (value == null) {
        throw JsonApiXMissingArgumentException(missingArgument)
    } else {
        return value
    }
}
