package fr.bmartel.android.bixi.app.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.ListView

import fr.bmartel.android.bixi.app.R
import fr.bmartel.android.bixi.app.adapter.OpenSourceItemAdapter

/**
 * open source components dialog
 *
 * @author Bertrand Martel
 */
class OpenSourceItemsDialog(context: Context) : AlertDialog(context) {

    init {

        val inflater = LayoutInflater.from(context)
        val listview = inflater.inflate(R.layout.open_source_list, null) as ListView
        listview.adapter = OpenSourceItemAdapter(context)

        setView(listview)
        setTitle(R.string.open_source_items)
        setButton(DialogInterface.BUTTON_POSITIVE, context.resources.getString(R.string.dialog_ok),
                null as DialogInterface.OnClickListener?)
    }
}