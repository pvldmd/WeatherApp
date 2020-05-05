package com.pvld.weatherapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pvld.weatherapp.network.entities.NetworkService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private val APP_PREFERENCES = "settings"
    private val PREF_CITY = "city"
    private val PREF_POSITION = "position"

    private val service = NetworkService.createNetworkService()
    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        spinner_main_cities.setSelection(settings.getInt(PREF_POSITION, 0))
        spinner_main_cities.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                if (parent != null) {
                    val city = parent.selectedItem.toString()
                    settings.edit().putString(PREF_CITY, city).apply()
                    settings.edit().putInt(PREF_POSITION, selectedItemPosition).apply()
                    loadCurrentWeather(applicationContext, city)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        settings.getString(PREF_CITY, "Moscow")?.let { loadCurrentWeather(this, it) }
    }

    fun loadCurrentWeather (context: Context, city: String) {
        group_weather_info.visibility = View.INVISIBLE
        progress_bar.visibility = ProgressBar.VISIBLE

        CoroutineScope(Dispatchers.IO).launch{
            val response = service.loadWeatherData("${city},RU")
            withContext(Dispatchers.Main){
                try {
                    if (response.isSuccessful) {
                        textView_main_temp.text = "${response.body()?.main?.temp} °C"
                        textView_main_desc.text = response.body()?.weather?.get(0)?.description
                        response.body()?.weather?.get(0)?.id?.let { setWeatherImage(it) }
                    } else {
                        Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Exception ${e.message}", Toast.LENGTH_LONG).show()
                }
                group_weather_info.visibility = View.VISIBLE
                progress_bar.visibility = ProgressBar.GONE
            }

        }
    }

    private fun setWeatherImage(weatherCode: Int) {
        val imageResource: Int = when (weatherCode) {
            200 - 232 -> R.drawable.ic_11d
            300 - 321 -> R.drawable.ic_09d
            500 - 504 -> R.drawable.ic_10d
            511 -> R.drawable.ic_13d
            520 - 531 -> R.drawable.ic_09d
            600 - 622 -> R.drawable.ic_13d
            700 - 781 -> R.drawable.ic_50d
            800 -> R.drawable.ic_01d
            801 -> R.drawable.ic_02d
            802 -> R.drawable.ic_03d
            803 - 804 -> R.drawable.ic_04d
            else -> R.drawable.ic_03d
        }
        imageView_main_icon.setImageResource(imageResource)
    }

    fun onClickRefreshButton() {
        settings.getString(PREF_CITY, "Moscow")?.let { loadCurrentWeather(this, it) }
    }

}
