package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.customnavigationrailview.R
import com.example.customnavigationview.CustomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var customNavigationView: CustomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customNavigationView = findViewById(R.id.customNavView)

        customNavigationView.setOnItemClickListener (object : OnMenuItemClickListener{
            override fun onItemClicked(itemId: Int) {
                Toast.makeText(this@MainActivity, itemId.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}