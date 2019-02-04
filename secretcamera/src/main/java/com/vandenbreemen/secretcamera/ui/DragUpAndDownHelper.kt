package com.vandenbreemen.secretcamera.ui

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.DOWN
import android.support.v7.widget.helper.ItemTouchHelper.UP

interface DragListener {

    fun onViewMoved(oldPosition: Int, newPosition: Int)

}

/**
 *
 * @author kevin
 */
class DragUpAndDownHelper(val listener: DragListener) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {

        return makeMovementFlags((UP or DOWN), 0)

    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        listener.onViewMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {

    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }
}