package com.jtyzzer.vantress.utilities

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.jtyzzer.vantress.R


//changes frame colors for cards in deck recyclerview
class CardBackgroundConverter {
    companion object {

        fun setCardBGColor(view: View, list: List<String>) {
            val colorMap = HashMap<String, Int>()

            colorMap["B"] = Color.parseColor("#50514F")
            colorMap["G"] = Color.parseColor("#449A73")
            colorMap["W"] = Color.parseColor("#FFF8D9")
            colorMap["R"] = Color.parseColor("#C74E4C")
            colorMap["U"] = Color.parseColor("#348AA7")
            colorMap[" B"] = Color.parseColor("#50514F")
            colorMap[" G"] = Color.parseColor("#449A73")
            colorMap[" W"] = Color.parseColor("#FFF8D9")
            colorMap[" R"] = Color.parseColor("#C74E4C")
            colorMap[" U"] = Color.parseColor("#348AA7")

            val monoManaMap = HashMap<String, Int>()
            monoManaMap["B"] = Color.parseColor("#6A6F6A")
            monoManaMap["G"] = Color.parseColor("#579A7C")
            monoManaMap["W"] = Color.parseColor("#FFFAE4")
            monoManaMap["R"] = Color.parseColor("#C77574")
            monoManaMap["U"] = Color.parseColor("#5B94A7")


            val bgArray = mutableListOf<Int>()
            for (item in list) {
                bgArray.add(colorMap.getValue(item))
            }


            val gd = GradientDrawable()
            val fg = GradientDrawable()
            gd.orientation = GradientDrawable.Orientation.LEFT_RIGHT
            when (bgArray.size) {
                1 -> {
                    gd.setColor(bgArray[0])
                    fg.setColor(monoManaMap.getValue(list[0]))
                }
                in 2..3 -> {
                    gd.colors = bgArray.toIntArray()
                    fg.setColor(Color.parseColor("#CBBC53"))

                }
                else -> {
                    gd.setColor(Color.parseColor("#CBB93F"))
                    fg.setColor(Color.parseColor("#CBBC53"))
                }
            }
            gd.cornerRadius = 40f
            fg.cornerRadius = 40f
            fg.setStroke(1, Color.parseColor("#2C2B2A"))
            view.findViewById<ConstraintLayout>(R.id.card_list_layout_bg).background = gd.mutate()
            view.findViewById<ConstraintLayout>(R.id.card_list_layout).background = fg.mutate()
        }
    }
}