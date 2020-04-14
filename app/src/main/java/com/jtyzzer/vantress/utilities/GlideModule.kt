package com.jtyzzer.vantress.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

@GlideModule
class GlideApp : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.apply {
            val requests = RequestOptions()
                .fitCenter().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .dontTransform()

            setDefaultRequestOptions(requests)

            setDefaultTransitionOptions(
                Drawable::class.java,
                DrawableTransitionOptions.withCrossFade(
                    DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                )
            )
        }
    }
}