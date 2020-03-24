package com.earthdefensesystem.retrorv.utilities

import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R

class DragListener : View.OnDragListener {
    var isDropped: Boolean = false
    var listener: DragListener = DragListener()

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        when (event?.action) {
            DragEvent.ACTION_DROP -> {
                isDropped = true

                var viewSource : View = event.localState as View
                var viewId : Int = v?.id!!
                val item = R.id.card_list_item
                val deckIV = R.id.deck_background_iv
                val listRV = R.id.deck_rv

                var source = viewSource.parent as RecyclerView
                var adapterSource = source.adapter as ListAdapter<*, *>
                var positionSource = viewSource.tag as Int
                var sourceId = source.id
                var target = v.parent as ImageView
                var positionTarget = v.tag as Int
                var cardList = adapterSource
            }
        }
        return true

      }
}