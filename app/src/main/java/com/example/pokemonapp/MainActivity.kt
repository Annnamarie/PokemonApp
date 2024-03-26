package com.example.pokemonapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler

import okhttp3.Headers
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val client = AsyncHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val randomButton: Button = findViewById(R.id.button)
        val searchButton: Button = findViewById(R.id.searchButton)
        val pokeImg: ImageView = findViewById(R.id.pokeImg)
        val pokemonName: TextView = findViewById(R.id.pokemonName)
        val pokeNameInput: EditText = findViewById(R.id.pokeNameInput)
        val strengthTextView: TextView = findViewById(R.id.strength)

        randomButton.setOnClickListener {
            fetchRandomPokemon(pokeImg, pokemonName, strengthTextView)
        }

        searchButton.setOnClickListener {
            val pokemon = pokeNameInput.text.toString().toLowerCase()
            if (pokemon.isNotEmpty()) {
                fetchPokemon(pokemon, pokeImg, pokemonName, strengthTextView)
            }
        }
    }

    private fun fetchRandomPokemon(imageView: ImageView, textView: TextView, strengthTextView: TextView) {
        val randomId = (1..898).random() // There are 898 Pokemon in the API
        val url = "https://pokeapi.co/api/v2/pokemon/$randomId"
        fetchPokemonData(url, imageView, textView, strengthTextView)
    }

    private fun fetchPokemon(name: String, imageView: ImageView, textView: TextView, strengthTextView: TextView) {
        val url = "https://pokeapi.co/api/v2/pokemon/$name"
        fetchPokemonData(url, imageView, textView, strengthTextView)
    }

    private fun fetchPokemonData(url: String, imageView: ImageView, textView: TextView, strengthTextView: TextView) {
        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers?, response: JSON?) {
                val pokemonName = response?.jsonObject?.getString("name")
                val imageUrl = response?.jsonObject?.getJSONObject("sprites")?.getString("front_default")
                val stats = response?.jsonObject?.getJSONArray("stats")

                pokemonName?.let {
                    textView.text = it.capitalize(Locale.getDefault())
                }

                imageUrl?.let {
                    Glide.with(this@MainActivity)
                        .load(it)
                        .into(imageView)
                }

                stats?.let {
                    var bestStat = 0
                    var bestStatName = ""
                    for (i in 0 until it.length()) {
                        val stat = it.getJSONObject(i)
                        val baseStat = stat.getInt("base_stat")
                        val statName = stat.getJSONObject("stat").getString("name")
                        if (baseStat > bestStat) {
                            bestStat = baseStat
                            bestStatName = statName
                        }
                    }
                    strengthTextView.text = "Strength: $bestStatName"
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String?,
                throwable: Throwable?
            ) {
                Log.e("Pokemon API", "Failed to fetch Pokemon: $errorResponse")
            }
        })
    }

}

