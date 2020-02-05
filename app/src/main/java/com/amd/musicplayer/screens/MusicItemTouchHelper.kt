package com.amd.musicplayer.screens

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class MusicItemTouchHelper (var adapter : MusicAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
    var upOrDown = ItemTouchHelper.UP or ItemTouchHelper.DOWN
    var leftOrRight = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
     return makeMovementFlags(upOrDown , leftOrRight)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.getListener().onSwip(target , viewHolder)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.getListener().onDelet(viewHolder.adapterPosition , direction)
        }
}