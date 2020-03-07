package com.earthdefensesystem.retrorv.utilities

import android.content.Context
import android.graphics.PorterDuff
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.earthdefensesystem.retrorv.R

inline fun <T : Any> makeLoadingRequestListener(
    crossinline failedAction: (String) -> Unit,
    crossinline readyAction: (String) -> Unit):
        RequestListener<T> = object : RequestListener<T> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<T>?,
        isFirstResource: Boolean
    ): Boolean {
        e?.printStackTrace()
        e?.logRootCauses("onLoadFailed using CircularProgressDrawable")
        failedAction.invoke("${e?.message}")
        return false
    }

    override fun onResourceReady(
        resource: T,
        model: Any?,
        target: Target<T>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        readyAction.invoke("Success: $dataSource")
        return false
    }
}

//fun Context.getCircularProgressDrawable(): CircularProgressDrawable = CircularProgressDrawable(this).apply {
//    setColorFilter(resources.getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP)
//    strokeWidth = 7f
//    centerRadius = 20f
//}.also {
//    it.start()
//}