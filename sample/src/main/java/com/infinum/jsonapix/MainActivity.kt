package com.infinum.jsonapix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.infinum.jsonapix.annotations.JsonApiSerializable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val json =  Json.encodeToString(JsonApiSerializable_Person(Person("Stef", "Banek")))
        text.text = json

//        textDecoded.text = Json.decodeFromString<DummyDataJsonApiWrapper>(json).data.name
    }
}

@Serializable
@JsonApiSerializable("dummy_type")
data class DummyData(
    @SerialName("name") val name: String
)

@Serializable
@JsonApiSerializable("person")
data class Person(val name: String, val surname: String)


