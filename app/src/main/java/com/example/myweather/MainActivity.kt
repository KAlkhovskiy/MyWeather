package com.example.myweather
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myweather.fragment.MainFragment



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, MainFragment.newInstance())
            .commit()

    }
}
