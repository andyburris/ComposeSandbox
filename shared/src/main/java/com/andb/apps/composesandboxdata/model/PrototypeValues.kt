package com.andb.apps.composesandboxdata.model

import kotlinx.serialization.Serializable


@Serializable
sealed class PrototypeValue<T> {
    @Serializable data class Fixed<T>(val value: T) : PrototypeValue<T>()
    @Serializable class Inherit<T> : PrototypeValue<T>()
}