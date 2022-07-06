package com.example.myweather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.databinding.ListItemBinding

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.Holder>() {
    var dayList = ArrayList<DayInfo>()

    class Holder(view: View): RecyclerView.ViewHolder(view){

        val binding = ListItemBinding.bind(view)
        fun bind(elem: DayInfo) = with(binding){
            dayTime.text = elem.dayTime
            MaxMinTemp.text = "${elem.tempMin}°C/${elem.tempMax}°C"
            descriptionDay.text = elem.description
            imageView2.setImageResource(elem.img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dayList[position])
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    fun addDay(day: DayInfo){
        dayList.add(day)
        notifyDataSetChanged()
    }
    fun cleardayList(){
        dayList = ArrayList<DayInfo>()
    }
}