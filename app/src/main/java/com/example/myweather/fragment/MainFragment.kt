package com.example.myweather.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myweather.DayInfo
import com.example.myweather.R
import com.example.myweather.WeatherAdapter
import com.example.myweather.databinding.FragmentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class MainFragment : Fragment(), LocationListener {
    private lateinit var launcher:ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val adapter = WeatherAdapter()
    private val imageIdList = listOf(
        R.drawable.w1,
        R.drawable.w2,
        R.drawable.w3,
        R.drawable.w4,
        R.drawable.w5,
        R.drawable.w6,
        R.drawable.w7,
        R.drawable.w8,
        R.drawable.w9,
    )
    private val weatherDescribe = listOf(
        "ясно",
        "облачн",
        "пасмурно",
        "туман",
        "дождь",
        "снег",
        "град",
        "гроза"
    )
    private val key = "c58c6b30fac929947abde8df899ea688"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        init()
        getLocation()
        getAPI()
    }
    private fun getAPI() = with(binding){
        handButton.setOnClickListener {
            if (city.text.toString().trim() == "")
                Toast.makeText(activity as AppCompatActivity, R.string.no_user_input, Toast.LENGTH_SHORT).show()
            else {
                val url =
                    "https://api.openweathermap.org/data/2.5/forecast?q=${city.text}" +
                            "&appid=$key&lang=ru&units=metric"
                CoroutineScope(Dispatchers.IO).launch {
                    getURLData(url)
                }
            }
        }

        autoButton.setOnClickListener {
            getLocation()
        }

    }

    private fun init(){
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(activity as AppCompatActivity)
            rcView.adapter = adapter
        }
    }

    private fun checkPermissions(){
        if(ContextCompat.checkSelfPermission(
                activity as AppCompatActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            launcher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()){
                Toast.makeText(activity, "Location: $it", Toast.LENGTH_SHORT).show()
            }
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
    }

    @SuppressLint("SetTextI18n")
    private fun getURLData(url: String) {
        val queue = Volley.newRequestQueue(activity as AppCompatActivity)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response->
                val obj = JSONObject(response)
                binding.city.setText(obj.getJSONObject("city")
                    .getString("name"))
                val day = obj.getJSONArray("list").getJSONObject(0)
                binding.temp.text = day.getJSONObject("main").getInt("temp")
                    .toString() + "°C"
                binding.feelsLike.text = "Ощущается: " + day.getJSONObject("main")
                    .getInt("feels_like").toString() + "°C"
                binding.windSpeed.text = day.getJSONObject("wind")
                    .getDouble("speed").toString() + " м/с"
                binding.description.text = day.getJSONArray("weather").getJSONObject(0)
                    .getString("description")
                binding.MaxMin.text = day.getJSONObject("main").getInt("temp_min")
                    .toString() + "°C/" + day.getJSONObject("main").getInt("temp_max")
                    .toString() + "°C"
                binding.deg.text = "Направление: " + day.getJSONObject("wind")
                    .getDouble("deg").toString() + "°"
                binding.dayTime.text = day.getString("dt_txt")
                binding.img.setImageResource(getImg(day.getJSONArray("weather").getJSONObject(0)
                    .getString("description")))

                adapter.clearDayList()
                for (elem in 1..obj.getInt("cnt") step 4){
                    val oneDay = obj.getJSONArray("list").getJSONObject(elem)
                    val dayInfo = DayInfo(oneDay.getString("dt_txt"),
                        oneDay.getJSONArray("weather").getJSONObject(0)
                            .getString("description"),
                        oneDay.getJSONObject("main").getInt("temp_min")
                            .toString(),
                        oneDay.getJSONObject("main").getInt("temp_max")
                            .toString(),
                        getImg(oneDay.getJSONArray("weather").getJSONObject(0)
                            .getString("description"))
                    )
                    adapter.addDay(dayInfo)
                }

            },
            {
                Toast.makeText(activity, "$it", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequest)

    }
    private fun getImg(s: String): Int {
        var out = imageIdList[8]
        for (elem in weatherDescribe){
            if (elem in s){
                out = imageIdList[weatherDescribe.indexOf(elem)]
            }
        }
        return out
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {
            val locationManager: LocationManager =
                requireActivity().applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000,
                500.toFloat(), this
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val url =
            "https://api.openweathermap.org/data/2.5/forecast?lat=$latitude" +
                    "&lon=$longitude&appid=$key&lang=ru&units=metric"

        CoroutineScope(Dispatchers.IO).launch {
            getURLData(url)
        }

    }
}