package com.example.myweather.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweather.MainViewModel
import com.example.myweather.R
import com.example.myweather.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private lateinit var launcher:ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val dataModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container,false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        dataModel.dayData.observe(activity as AppCompatActivity) {
            binding.img.setImageResource(it.img)
            binding.temp.text = "${it.temp}°C"
            binding.city.setText(it.city)
            binding.feelsLike.text = "Ощущается: ${it.feelsLike}°C"
            binding.windSpeed.text = "${it.windSpeed} м/с"
            binding.description.text = it.description
            binding.MaxMin.text = "${it.tempMin}°C/${it.tempMax}°C"
            binding.deg.text = "Направление: ${it.deg}°"
            binding.dayTime.text = it.dayTime

        }
        dataModel.getLocation(requireActivity())
        binding.autoButton.setOnClickListener {
            dataModel.getLocation(requireActivity())
        }
        binding.handButton.setOnClickListener {
            if (binding.city.text.toString().trim() == "")
                Toast.makeText(activity as AppCompatActivity, R.string.no_user_input, Toast.LENGTH_SHORT).show()
            else
                dataModel.getAPI(binding.city.text.toString(), requireActivity())
        }
        init()
    }

    private fun init(){
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(activity as AppCompatActivity)
            rcView.adapter = dataModel.adapter
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


}