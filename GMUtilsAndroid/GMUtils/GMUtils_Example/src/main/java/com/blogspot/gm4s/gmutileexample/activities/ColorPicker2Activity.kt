package com.blogspot.gm4s.gmutileexample.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.gm4s.gmutileexample.R
import gmutils.ui.adapters.BaseRecyclerAdapter
import gmutils.ui.adapters.BaseRecyclerAdapterViewHolder
import gmutils.ui.customViews.ColorPicker

class ColorPicker2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker2)

        val rv = findViewById<RecyclerView>(R.id.colorPickerRv)
        val text = findViewById<TextView>(R.id.text)

        val adapter = Adapter(rv, generateColorList())
        adapter.setOnItemClickListener { _, item, position ->
            text.text = item.toString() + "\n" + item.color
            Log.e("****", item.toString() + " ---- " + item.color)
        }
        Log.e("****", "items count: ${adapter.itemCount}")
    }

    fun generateColorList(): List<ColorX> {
        val list = mutableListOf<ColorX>()

        list.addAll(generateRedColorList())
        list.addAll(generateGreenColorList())
        list.addAll(generateBlueColorList())

        return list
    }

    fun generateRedColorList(): List<ColorX> {
        val list = mutableListOf<ColorX>()

        //3, 5, 15, 17, 51, 85, 255

        val a1 = arrayOf(64, 128, 192, 255)
        val a2 = arrayOf(40, 80, 120, 160)

        for (a in a1)
            for (b in a2)
                for (c in a2) {
                    if (a >= b && a >= c)
                        list.add(ColorX(r = a, g = c, b = b))

                    /*if (c == a2[a2.size - 1]) {
                        list.add(ColorX(r = 255, g = 255, b = 255))
                    }*/
                }

        return list
    }

    fun generateGreenColorList(): List<ColorX> {
        val list = mutableListOf<ColorX>()

        //3, 5, 15, 17, 51, 85, 255

        val a1 = arrayOf(64, 128, 192, 255)
        val a2 = arrayOf(40, 80, 120, 160)

        for (a in a1)
            for (b in a2)
                for (c in a2) {
                    if (a >= b && a >= c)
                        //list.add(ColorX(r = b, g = a, b = c))
                        list.add(ColorX(r = c, g = a, b = b))


                    /*if (c == a2[a2.size - 1]) {
                        list.add(ColorX(r = 255, g = 255, b = 255))
                    }*/
                }

        return list
    }

    fun generateBlueColorList(): List<ColorX> {
        val list = mutableListOf<ColorX>()

        //3, 5, 15, 17, 51, 85, 255

        val a1 = arrayOf(64, 128, 192, 255)
        val a2 = arrayOf(40, 80, 120, 160)

        for (a in a1)
            for (b in a2)
                for (c in a2) {
                    if (a >= b && a >= c)
                        list.add(ColorX(r = c, g = b, b = a))


                    /*if (c == a2[a2.size - 1]) {
                        list.add(ColorX(r = 255, g = 255, b = 255))
                    }*/
                }

        return list
    }


    data class ColorX(val r: Int, val g: Int, val b: Int) {
        val color get() = Color.rgb(r, g, b)
    }

    class Adapter(recyclerView: RecyclerView?, list: List<ColorX>) :
        BaseRecyclerAdapter<ColorX>(recyclerView, list, 5, true) {

        override fun setupWithRecyclerViewAsGrid(
            recyclerView: RecyclerView,
            spanCount: Int,
            horizontal: Boolean
        ) {
            val itemWidth = recyclerView.resources.getDimensionPixelSize(R.dimen.size_40)
            val count = recyclerView.width / itemWidth
            super.setupWithRecyclerViewAsGrid(recyclerView, count, horizontal)
        }

        override fun onDispose() {
        }

        override fun getViewHolder(
            viewType: Int,
            inflater: LayoutInflater,
            container: ViewGroup?
        ): BaseRecyclerAdapterViewHolder<ColorX> {
            return VH(R.layout.adapter_color_picker_item, inflater, container)
        }

        inner class VH(resId: Int, inflater: LayoutInflater, container: ViewGroup?) :
            BaseRecyclerAdapterViewHolder<ColorX>(resId, inflater, container) {

            val view = findViewById<View>(R.id.view)

            override fun setValues(item: ColorX) {
                view.setBackgroundColor(item.color)
            }

            override fun onDispose() {
            }
        }

    }
}
