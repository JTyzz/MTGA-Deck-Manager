package com.jtyzzer.vantress.utilities

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import com.jtyzzer.vantress.R


class ImageSpanConverter {
    companion object {
        //inserts mana symbols into card mana costs in deck recyclerview
        fun getSpannedImage(context: Context, s: String): Spannable {
            val manaMap = HashMap<String, Int>()
            manaMap["{0}"] = R.drawable.ic_0
            manaMap["{1}"] = R.drawable.ic_1
            manaMap["{2}"] = R.drawable.ic_2
            manaMap["{3}"] = R.drawable.ic_3
            manaMap["{4}"] = R.drawable.ic_4
            manaMap["{5}"] = R.drawable.ic_5
            manaMap["{6}"] = R.drawable.ic_6
            manaMap["{7}"] = R.drawable.ic_7
            manaMap["{8}"] = R.drawable.ic_8
            manaMap["{9}"] = R.drawable.ic_9
            manaMap["{10}"] = R.drawable.ic_10
            manaMap["{2B}"] = R.drawable.ic_2b
            manaMap["{2G}"] = R.drawable.ic_2g
            manaMap["{B}"] = R.drawable.ic_b
            manaMap["{B/G}"] = R.drawable.ic_bg
            manaMap["{B/R}"] = R.drawable.ic_br
            manaMap["{BP}"] = R.drawable.ic_bp
            manaMap["{C}"] = R.drawable.ic_c
            manaMap["{G}"] = R.drawable.ic_g
            manaMap["{GP}"] = R.drawable.ic_gp
            manaMap["{G/U}"] = R.drawable.ic_gu
            manaMap["{G/W}"] = R.drawable.ic_gw
            manaMap["{R}"] = R.drawable.ic_r
            manaMap["{R/G}"] = R.drawable.ic_rg
            manaMap["{RP}"] = R.drawable.ic_rp
            manaMap["{R/W}"] = R.drawable.ic_rw
            manaMap["{S}"] = R.drawable.ic_s
            manaMap["{U}"] = R.drawable.ic_u
            manaMap["{U/B}"] = R.drawable.ic_ub
            manaMap["{UP}"] = R.drawable.ic_up
            manaMap["{U/R}"] = R.drawable.ic_ur
            manaMap["{W}"] = R.drawable.ic_w
            manaMap["{WB}"] = R.drawable.ic_wb
            manaMap["{WP}"] = R.drawable.ic_wp
            manaMap["{W/U}"] = R.drawable.ic_wu
            manaMap["{X}"] = R.drawable.ic_x
            manaMap["{Y}"] = R.drawable.ic_y

            val builder = SpannableStringBuilder()
            builder.append(s)

            for (i in 0..builder.length) {
                for (j in i..builder.length) {
                    for (entry in manaMap.entries) {
                        if (builder.subSequence(i, j)
                                .toString() == entry.key) {
                            builder.setSpan(ImageSpan(context, entry.value, DynamicDrawableSpan.ALIGN_BOTTOM),
                                i, j, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            }
            return builder
        }

        //inserts mana symbols into deck in list fragment
        fun getSpannedManaImage(context: Context, s: String): Spannable {
            val manaSymbolMap = HashMap<String, Int>()
            manaSymbolMap["G"] = R.drawable.ic_g
            manaSymbolMap["W"] = R.drawable.ic_w
            manaSymbolMap["U"] = R.drawable.ic_u
            manaSymbolMap["B"] = R.drawable.ic_b
            manaSymbolMap["R"] = R.drawable.ic_r

            val builder = SpannableStringBuilder()
            builder.append(s)

            for (i in 0..builder.length) {
                for (j in i..builder.length) {
                    for (entry in manaSymbolMap.entries) {
                        if (builder.subSequence(i, j)
                                .toString() == entry.key
                        ) {
                            builder.setSpan(
                                ImageSpan(context, entry.value, DynamicDrawableSpan.ALIGN_BOTTOM),
                                i, j, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                }
            }
            return builder
        }


    }
}