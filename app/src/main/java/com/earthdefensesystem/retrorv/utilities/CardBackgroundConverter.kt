package com.earthdefensesystem.retrorv.utilities

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.CardCount

class CardBackgroundConverter {
    companion object {
        //getApplication<Application>().resources.getColor

        fun setCardBGColor(constraintLayout: ConstraintLayout, list: List<String>){
            val colorMap = HashMap<String, Int>()

            colorMap["B"] = Color.parseColor("#50514F")
            colorMap["G"] = Color.parseColor("#449A73")
            colorMap["W"] = Color.parseColor("#FFFAE4")
            colorMap["R"] = Color.parseColor("#C74E4C")
            colorMap["U"] = Color.parseColor("#348AA7")
            colorMap[" B"] = Color.parseColor("#50514F")
            colorMap[" G"] = Color.parseColor("#449A73")
            colorMap[" W"] = Color.parseColor("#FFFAE4")
            colorMap[" R"] = Color.parseColor("#C74E4C")
            colorMap[" U"] = Color.parseColor("#348AA7")

            Log.d("colors", list.joinToString(","))

            val bgArray = mutableListOf<Int>()
            for (item in list){
                        bgArray.add(colorMap.getValue(item))
                }


            val gd = GradientDrawable()
            gd.orientation = GradientDrawable.Orientation.LEFT_RIGHT
            if(bgArray.size == 1){
                gd.setColor(bgArray[0])
            }else if(bgArray.size in 2..3) {
                gd.colors = bgArray.toIntArray()
            } else{
                gd.setColor(Color.parseColor("#C7B74C"))
            }
            gd.cornerRadius = 30f
            constraintLayout.background = gd.mutate()
        }
    }
}