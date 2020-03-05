package com.earthdefensesystem.retrorv.glide

import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

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
    }
}
)