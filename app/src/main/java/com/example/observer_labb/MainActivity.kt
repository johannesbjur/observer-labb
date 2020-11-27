package com.example.observer_labb

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    val url = URL("https://quote-garden.herokuapp.com/api/v2/quotes/random")

    lateinit var quoteText: TextView

    var quote by Delegates.observable<String>("") { property, oldValue, newValue ->

        println("$newValue")

        runOnUiThread {

            quoteText.text = newValue
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(quote)

        val newButton = findViewById<Button>(R.id.new_button)
        quoteText = findViewById<TextView>(R.id.quote_text)


        newButton.setOnClickListener {
            this.GetQuoteFromAPI()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun GetQuoteFromAPI() {

        val thread = Thread(Runnable {
            try {
                with(url.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"
                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->

                            val jsonObject = JSONObject(line)

                            if (jsonObject["statusCode"] == 200) {

                                var quoteJson = jsonObject["quote"] as JSONObject
                                val quoteText = quoteJson["quoteText"] as String
                                println(quoteText)

                                quote = quoteText
                            }
                            else {
                                quote = "Error retrieving data from api"
                            }

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        thread.start()
    }
}