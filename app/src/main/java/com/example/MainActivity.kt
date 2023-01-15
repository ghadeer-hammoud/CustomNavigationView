package com.example

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.customnavigationrailview.R
import com.ghadeerh.customnavigationview.CustomNavigationView
import com.ghadeerh.customnavigationview.OnMenuItemClickListener

class MainActivity : AppCompatActivity() {

    private lateinit var customNavigationView: CustomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customNavigationView = findViewById(R.id.customNavView)

        customNavigationView.setOnItemClickListener (object : OnMenuItemClickListener {
            override fun onItemClicked(itemId: Int) {
                Toast.makeText(this@MainActivity, itemId.toString(), Toast.LENGTH_SHORT).show()
                if(customNavigationView.isExpanded())
                    customNavigationView.toggleMenu()
            }
        })

        //customNavigationView.setMenuBackgroundColor(ContextCompat.getColor(this, R.color.blue_dark))
        val header = layoutInflater.inflate(R.layout.layout_header, null, false)
        customNavigationView.setHeader(header)
        customNavigationView.setExternalToggleView(findViewById<Button>(R.id.toggleView))
        customNavigationView.setCollapseWhenClickOutside(true)

        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            customNavigationView.setGridSpanCount(2)
        else
            customNavigationView.setGridSpanCount(3)
        customNavigationView.setSelectedIconTintColor(ContextCompat.getColor(this, R.color.white))
        customNavigationView.setSelectedIconBackgroundTintColor(ContextCompat.getColor(this, R.color.orange))

        //customNavigationView.setIconTintColor(ContextCompat.getColor(this, R.color.black))
        customNavigationView.setIconBackgroundTintColor(ContextCompat.getColor(this, R.color.grey_medium))
        customNavigationView.setToggleButtonTintColor(ContextCompat.getColor(this, R.color.white))
        customNavigationView.setToggleButtonBackgroundTintColor(ContextCompat.getColor(this, R.color.blue_dark))
        //customNavigationView.setMenu(R.menu.menu)
    }

    override fun onBackPressed() {

        if(customNavigationView.isExpanded())
            customNavigationView.toggleMenu()
        else
            super.onBackPressed()
    }
}