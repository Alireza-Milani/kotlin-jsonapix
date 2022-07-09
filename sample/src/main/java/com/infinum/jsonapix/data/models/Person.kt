package com.infinum.jsonapix.data.models

import com.infinum.jsonapix.annotations.HasMany
import com.infinum.jsonapix.annotations.HasOne
import com.infinum.jsonapix.annotations.JsonApiX
import com.infinum.jsonapix.annotations.JsonApiXMeta
import com.infinum.jsonapix.core.JsonApiModel
import com.infinum.jsonapix.core.resources.Meta
import kotlinx.serialization.Serializable

@Serializable
@JsonApiX("person")
data class Person(
    val name: String?,
    val surname: String,
    val age: Int,
    @HasOne("dog")
    val myFavoriteDog: Dog? = null,
    @HasOne("dog")
    val myOldDog: Dog,
    @HasMany("dog")
    val allMyDogs: List<Dog>?,
    @HasMany("dog")
    val allOldMyDogs: List<Dog>?
) : JsonApiModel()

@Serializable
@JsonApiXMeta("person")
data class PersonMeta(val owner: String) : Meta
