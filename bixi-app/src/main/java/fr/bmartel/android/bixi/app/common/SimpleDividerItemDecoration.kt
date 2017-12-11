package fr.bmartel.android.bixi.app.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View

import fr.bmartel.android.bixi.app.R

/**
 * draw line separation for all item of recyclerview.
 *
 * @author Bertrand Martel
 */
class SimpleDividerItemDecoration(context: Context?) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable? = context?.resources?.getDrawable(R.drawable.line_divider)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        if (mDivider != null) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight

            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider.intrinsicHeight

                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }
}