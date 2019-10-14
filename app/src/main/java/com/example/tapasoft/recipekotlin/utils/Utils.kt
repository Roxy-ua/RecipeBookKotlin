package com.example.tapasoft.recipekotlin.utils

import android.content.Context
import android.util.TypedValue


object Utils {

    fun convertDipToPixels(context: Context, dip: Int): Int {
        //return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), context.getResources().getDisplayMetrics()).toInt()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), context.resources.displayMetrics).toInt()
    }

}