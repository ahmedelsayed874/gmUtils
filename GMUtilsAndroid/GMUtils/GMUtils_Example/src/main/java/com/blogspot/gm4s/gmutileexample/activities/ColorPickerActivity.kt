package com.blogspot.gm4s.gmutileexample.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.blogspot.gm4s.gmutileexample.R
import gmutils.inputFilters.DecimalRangeFilter
import gmutils.ui.customViews.ColorPicker

class ColorPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        val colorPicker = findViewById<ColorPicker>(R.id.colorPicker)
        val number = findViewById<EditText>(R.id.number)
        findViewById<View>(R.id.okBtn).setOnClickListener {
            val num = number.text.toString().toIntOrNull() ?: return@setOnClickListener

            //colorPicker.setupColorsRecyclerView(num)
        }

        number.filters = arrayOf(
            DecimalRangeFilter(
                0.0,
                1.0
            )
        )
    }
}