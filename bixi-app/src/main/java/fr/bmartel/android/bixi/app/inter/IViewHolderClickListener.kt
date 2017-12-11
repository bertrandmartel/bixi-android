package fr.bmartel.android.bixi.app.inter

import android.view.View

/**
 * click listener for recyclerview item.
 *
 * @author Bertrand Martel
 */
interface IViewHolderClickListener {

    /**
     * triggered when user click on packet in recycler view
     *
     * @param view
     */
    fun onClick(view: View)
}