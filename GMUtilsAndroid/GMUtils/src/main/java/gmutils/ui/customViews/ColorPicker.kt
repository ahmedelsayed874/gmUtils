package gmutils.ui.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import gmutils.R
import gmutils.inputFilters.IntegerRangeFilter
import gmutils.listeners.TextChangedListener
import gmutils.storage.GeneralStorage
import gmutils.ui.adapters.BaseRecyclerAdapter
import okhttp3.internal.toHexString
import org.json.JSONArray
import kotlin.math.max

class ColorPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    val pickerName: String = "",
) : FrameLayout(context, attrs, defStyleAttr) {

    //region private members
    private val redValueSeekBar: SeekBar
    private val redValueEt: EditText

    private val greenValueSeekBar: SeekBar
    private val greenValueEt: EditText

    private val blueValueSeekBar: SeekBar
    private val blueValueEt: EditText

    private val alphaValueSeekBar: SeekBar
    private val alphaValueEt: EditText

    private val hexValueEt: EditText
    private val colorPreviewCard: CardView

    private val colorsRv: RecyclerView
    private val recentColorsRv: RecyclerView

    private var disableSeekbarListener = false
    private var disableEditTextListener = false
    private var disableHexEditTextListener = false
    private var applyChangeOnHexInput = true
    //endregion

    //----------------------------------------------------------------------------------------------

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.color_picker, this, true)

        redValueSeekBar = view.findViewById(R.id.redValueSeekBar)
        redValueEt = view.findViewById(R.id.redValueEt)
        setupSeekBarWithEditText(redValueSeekBar, redValueEt, 1)

        greenValueSeekBar = view.findViewById(R.id.greenValueSeekBar)
        greenValueEt = view.findViewById(R.id.greenValueEt)
        setupSeekBarWithEditText(greenValueSeekBar, greenValueEt, 2)

        blueValueSeekBar = view.findViewById(R.id.blueValueSeekBar)
        blueValueEt = view.findViewById(R.id.blueValueEt)
        setupSeekBarWithEditText(blueValueSeekBar, blueValueEt, 3)

        alphaValueSeekBar = view.findViewById(R.id.alphaValueSeekBar)
        alphaValueEt = view.findViewById(R.id.alphaValueEt)
        setupSeekBarWithEditText(alphaValueSeekBar, alphaValueEt, 0)

        hexValueEt = view.findViewById(R.id.hexValueEt)
        colorPreviewCard = view.findViewById(R.id.colorPreviewCard)
        colorPreviewCard.setCardBackgroundColor(Color.parseColor("#ff7f7f7f"))
        colorPreviewCard.cardElevation = 0f
        colorPreviewCard.radius =
            view.context.resources.getDimensionPixelSize(R.dimen.size_10).toFloat()
        setupResultedHexValue()

        val colorPreviewCardContainer = view.findViewById<CardView>(R.id.colorPreviewCardContainer)
        colorPreviewCardContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.gray3
            )
        )
        colorPreviewCardContainer.cardElevation = 0f
        colorPreviewCardContainer.radius =
            view.context.resources.getDimensionPixelSize(R.dimen.size_10).toFloat()

        colorsRv = view.findViewById(R.id.colorsRv)
        setupColorsRecyclerView()

        recentColorsRv = view.findViewById(R.id.recentColorsRv)
        setupRecentColorsRecyclerView()

    }

    //----------------------------------------------------------------------------------------------

    //region SeekBar & EditText
    private class SeekBarProgressChangeListener(
        onChange: (SeekBar, Int) -> Unit
    ) : SeekBar.OnSeekBarChangeListener {
        var onChange: ((SeekBar, Int) -> Unit)? = onChange

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            onChange?.invoke(seekBar, progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }

    private fun setupSeekBarWithEditText(seekBar: SeekBar, editText: EditText, order: Int) {
        seekBar.setOnSeekBarChangeListener(SeekBarProgressChangeListener { seekBar, progress ->
            //if (disableSeekbarListener) return@SeekBarProgressChangeListener

            if (!disableSeekbarListener) {
                disableEditTextListener = true
                editText.setText(progress.toString())
                disableEditTextListener = false
            }

            val colorCode = convertNumbersToColorCode(
                alphaValueSeekBar.progress,
                redValueSeekBar.progress,
                greenValueSeekBar.progress,
                blueValueSeekBar.progress
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val colorState = ColorStateList.valueOf(
                    Color.rgb(
                        if (order == 1) progress else 0,
                        if (order == 2) progress else 0,
                        if (order == 3) progress else 0
                    )
                )

                seekBar.progressTintList = colorState
                seekBar.thumbTintList = colorState
            }

            if (applyChangeOnHexInput) {
                disableHexEditTextListener = true
                hexValueEt.setText(colorCode.toHexString())
                disableHexEditTextListener = false
            }

            colorPreviewCard.setCardBackgroundColor(colorCode)

            onColorChanged?.invoke(this, colorCode)
        })

        editText.filters = arrayOf(IntegerRangeFilter(0, 255))

        editText.addTextChangedListener(TextChangedListener.create {
            if (disableEditTextListener) return@create

            it.toIntOrNull()?.let {
                disableSeekbarListener = true
                seekBar.progress = it
                disableSeekbarListener = false
            }
        })
    }

    private fun setupResultedHexValue() {
        hexValueEt.addTextChangedListener(TextChangedListener.create {
            if (disableHexEditTextListener) return@create
            applyChangeOnHexInput = false

            var colorHex = it.replace("#", "")

            colorHex = if (colorHex.length < 6) {
                (colorHex + "000000").substring(0, 6)

            } else {
                (colorHex + "00000000").substring(0, 8)
            }

            val colorCode = try {
                Color.parseColor("#$colorHex")
            } catch (e: Exception) {
                Color.BLACK
            }

            redValueSeekBar.progress = Color.red(colorCode)
            greenValueSeekBar.progress = Color.green(colorCode)
            blueValueSeekBar.progress = Color.blue(colorCode)
            alphaValueSeekBar.progress = Color.alpha(colorCode)

            applyChangeOnHexInput = true
        })

        hexValueEt.setOnEditorActionListener { v, actionId, event ->
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                try {
                    val colorCode = Color.parseColor(hexValueEt.text.toString())
                    addToRecentColors(colorCode)
                } catch (e: Exception) {
                }

                return@setOnEditorActionListener true
            }

            false
        }

        colorPreviewCard.setOnClickListener {
            val colorCode = colorPreviewCard.cardBackgroundColor.defaultColor
            addToRecentColors(colorCode)
        }
    }
    //endregion

    //----------------------------------------------------------------------------------------------

    //region Colors Recycler View
    private data class Colors(
        val one: Int,
        val two: Int,
        val three: Int
    )

    private class ColorsAdapter(
        recyclerView: RecyclerView?,
        list: MutableList<Colors>,
        var whenColorSelect: ((Int) -> Unit)?
    ) : BaseRecyclerAdapter<Colors>(
        recyclerView, list, false
    ) {
        override fun onDispose() {
            whenColorSelect = null
        }

        override fun getViewHolder(
            viewType: Int,
            inflater: LayoutInflater,
            container: ViewGroup?
        ) = VH(R.layout.adapter_colors, inflater, container)

        inner class VH(resId: Int, inflater: LayoutInflater, container: ViewGroup?) :
            ViewHolder(resId, inflater, container) {

            val card1 = findViewById<CardView>(R.id.card1)
            val card2 = findViewById<CardView>(R.id.card2)
            val card3 = findViewById<CardView>(R.id.card3)

            init {
                itemView.setOnClickListener(null)
                card1.setOnClickListener(this)
                card2.setOnClickListener(this)
                card3.setOnClickListener(this)

                val elevation =
                    card1.context.resources.getDimensionPixelSize(R.dimen.size_1).toFloat()
                val radius = card1.context.resources.getDimensionPixelSize(R.dimen.size_5).toFloat()

                listOf<CardView>(card1, card2, card3).forEach {
                    it.cardElevation = elevation
                    it.radius = radius
                }
            }

            override fun setValues(item: Colors) {
                card1.setCardBackgroundColor(item.one)
                card2.setCardBackgroundColor(item.two)
                card3.setCardBackgroundColor(item.three)
            }

            override fun dispose() {
            }

            override fun onClick(v: View?) {
                //super.onClick(v)
                if (v == card1) {
                    whenColorSelect?.invoke(item.one)
                } else if (v == card2) {
                    whenColorSelect?.invoke(item.two)
                } else if (v == card3) {
                    whenColorSelect?.invoke(item.three)
                }
            }
        }
    }

    private fun setupColorsRecyclerView() {
        val list = mutableListOf<Colors>()

        val reds = generateRedColorList()
        val greens = generateGreenColorList()
        val blues = generateBlueColorList()

        val count = max(reds.size, max(greens.size, blues.size))

        for (i in 0 until count) {
            val c1 = if (i < reds.size) reds[i] else Color.WHITE
            val c2 = if (i < greens.size) greens[i] else Color.WHITE
            val c3 = if (i < blues.size) blues[i] else Color.WHITE
            list.add(Colors(c1, c2, c3))
        }

        Log.e("****", "color count = ${list.size}")

        ColorsAdapter(colorsRv, list) { selectedColor ->
            displayColorValues(selectedColor)
        }
    }

    private fun generateRedColorList(): List<Int> {
        val list = mutableListOf<Int>()

        //3, 5, 15, 17, 51, 85, 255

        val a1 = arrayOf(64, 128, 192, 255)
        val a2 = arrayOf(40, 80, 120, 160)

        for (a in a1)
            for (b in a2)
                for (c in a2) {
                    if (a >= b && a >= c)
                        list.add(Color.rgb(a, c, b))
                }

        return list
    }

    private fun generateGreenColorList(): List<Int> {
        val list = mutableListOf<Int>()

        //3, 5, 15, 17, 51, 85, 255

        val a1 = arrayOf(64, 128, 192, 255)
        val a2 = arrayOf(40, 80, 120, 160)

        for (a in a1)
            for (b in a2)
                for (c in a2) {
                    if (a >= b && a >= c)
                        list.add(Color.rgb(c, a, b))
                }

        return list
    }

    private fun generateBlueColorList(): List<Int> {
        val list = mutableListOf<Int>()

        //3, 5, 15, 17, 51, 85, 255

        val a1 = arrayOf(64, 128, 192, 255)
        val a2 = arrayOf(40, 80, 120, 160)

        for (a in a1)
            for (b in a2)
                for (c in a2) {
                    if (a >= b && a >= c)
                        list.add(Color.rgb(c, b, a))
                }

        return list
    }

    private fun displayColorValues(colorCode: Int) {
        redValueSeekBar.progress = Color.red(colorCode)
        greenValueSeekBar.progress = Color.green(colorCode)
        blueValueSeekBar.progress = Color.blue(colorCode)
        alphaValueSeekBar.progress = Color.alpha(colorCode)
    }
    //endregion

    //----------------------------------------------------------------------------------------------

    //region recent color recycler view
    private class RecentColorsAdapter(
        recyclerView: RecyclerView?,
        colors: MutableList<Int>,
        var onSelect: ((Int) -> Unit)?,
        var onRemove: ((RecentColorsAdapter, View, Int) -> Unit)?
    ) : BaseRecyclerAdapter<Int>(
        recyclerView, colors, false
    ) {
        override fun onDispose() {
            onSelect = null
            onRemove = null
        }

        override fun getViewHolder(
            viewType: Int,
            inflater: LayoutInflater,
            container: ViewGroup?
        ) = VH(R.layout.adapter_single_color, inflater, container)

        inner class VH(resId: Int, inflater: LayoutInflater, container: ViewGroup?) :
            ViewHolder(resId, inflater, container) {

            val card = findViewById<CardView>(R.id.card)

            init {
                itemView.setOnClickListener(null)
                card.setOnClickListener(this)
                card.setOnLongClickListener(this)

                card.cardElevation =
                    card.context.resources.getDimensionPixelSize(R.dimen.size_1).toFloat()
                card.radius = card.context.resources.getDimensionPixelSize(R.dimen.size_5).toFloat()

            }

            override fun setValues(item: Int) {
                card.setCardBackgroundColor(item)
            }

            override fun dispose() {
            }

            override fun onClick(v: View?) {
                //super.onClick(v)
                if (v == card) {
                    onSelect?.invoke(item)
                }
            }

            override fun onLongClick(v: View): Boolean {
                //return super.onLongClick(v)
                if (v == card) {
                    onRemove?.invoke(this@RecentColorsAdapter, v, adapterPosition)
                    return true
                }

                return false
            }
        }
    }

    private fun setupRecentColorsRecyclerView() {
        val colors = loadRecentSavedColors()

        RecentColorsAdapter(
            recyclerView = recentColorsRv,

            colors = colors,

            onSelect = { color ->
                displayColorValues(color)
            },

            onRemove = { adapter, view, position ->
                val popupMenu = PopupMenu(view.context, view, Gravity.BOTTOM)
                popupMenu.menu.add(R.string.remove).setOnMenuItemClickListener {
                    adapter.removeAt(position, true)
                    updateSavedRecentColors()
                    true
                }
                popupMenu.show()
            }
        )
    }

    private fun addToRecentColors(color: Int) {
        val list = (recentColorsRv.adapter as? RecentColorsAdapter)?.list
        if (list?.contains(color) == false) {
            (recentColorsRv.adapter as? RecentColorsAdapter)?.addOnTop(color, true)
            updateSavedRecentColors()
        }
    }

    //--------------------------------------------------------------------

    private val storageKey = "CLRS"

    private fun loadRecentSavedColors(): MutableList<Int> {
        val storage = GeneralStorage.getInstance(javaClass.simpleName + "-" + pickerName)
        val json = storage.retrieve(storageKey, "[]")

        return try {
            val jsonArray = JSONArray(json)
            val list = mutableListOf<Int>()

            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getInt(i))
            }

            list
        } catch (e: Exception) {
            emptyList<Int>().toMutableList()
        }
    }

    private fun updateSavedRecentColors() {
        val storage = GeneralStorage.getInstance(javaClass.simpleName)

        val jsonArray = JSONArray()

        (recentColorsRv.adapter as? RecentColorsAdapter)?.list?.forEach {
            try {
                jsonArray.put(it)
            } catch (e: Exception) {
            }
        }

        storage.save(storageKey, jsonArray.toString())
    }
    //endregion

    //----------------------------------------------------------------------------------------------

    //region helper methods
    private fun convertNumbersToColorCode(a: Int, r: Int, g: Int, b: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat()).toArgb()
        } else {
            val colorHex = convertNumbersToColorHex(a, r, g, b)
            Color.parseColor(colorHex)
        }
    }

    private fun convertNumbersToColorHex(a: Int, r: Int, g: Int, b: Int): String {
        var finalHex = ""

        listOf(a, r, g, b).forEach {
            var x = it.toHexString()
            if (x.length < 2) x = "0$x"
            else if (x.length > 2) x = x.substring(0, 2)

            finalHex += x
        }
        val colorHex = "#$finalHex"
        return colorHex
    }
    //endregion

    //----------------------------------------------------------------------------------------------

    var onColorChanged: ((ColorPicker, Int) -> Unit)? = null

    val selectedColor: Int
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val r = redValueSeekBar.progress.toFloat()
                val g = greenValueSeekBar.progress.toFloat()
                val b = blueValueSeekBar.progress.toFloat()
                val a = alphaValueSeekBar.progress.toFloat()

                Color.valueOf(r, g, b, a).toArgb()
            } else {
                val r = redValueSeekBar.progress
                val g = greenValueSeekBar.progress
                val b = blueValueSeekBar.progress
                val a = alphaValueSeekBar.progress

                convertNumbersToColorCode(a, r, g, b)
            }
        }

    val selectedColorHex get() = "#${selectedColor.toHexString()}"

    val recentSelectedColors : List<Int> get() {
        return loadRecentSavedColors()
    }

    fun addColorToRecentList(color: Int) {
        addToRecentColors(color)
    }
}
