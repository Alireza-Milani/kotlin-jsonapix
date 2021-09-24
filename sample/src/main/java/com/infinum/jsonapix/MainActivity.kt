package com.infinum.jsonapix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.infinum.jsonapix.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dog =
            Person(
                name = "Stef",
                surname = "Banek",
                age = 27,
                allMyDogs = listOf(Dog("Bella", 2), Dog("Bongo", 7)),
                myFavoriteDog = Dog("Bella", 2)
            )
        binding.text.text = dog.toJsonApiXString()

        binding.textDecoded.text = dog.toJsonApiXString().decodeJsonApiXString<Person>()?.name
    }
}
