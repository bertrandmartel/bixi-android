package fr.bmartel.android.bixi.app.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

import fr.bmartel.android.bixi.app.BuildConfig
import fr.bmartel.android.bixi.app.R

/**
 * About dialog
 *
 * @author Bertrand Martel
 */
class AboutDialog(context: Context) : AlertDialog(context) {

    init {

        val inflater = layoutInflater
        val dialoglayout = inflater.inflate(R.layout.about_dialog, null)
        setView(dialoglayout)

        val name = dialoglayout.findViewById<TextView>(R.id.name)
        val copyright = dialoglayout.findViewById<TextView>(R.id.copyright)
        val github_link = dialoglayout.findViewById<TextView>(R.id.github_link)

        name.text = context.resources.getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME
        copyright.setText(R.string.copyright)
        github_link.setText(R.string.github_link)

        setTitle(R.string.about)
        setButton(DialogInterface.BUTTON_POSITIVE, context.resources.getString(R.string.dialog_ok),
                null as DialogInterface.OnClickListener?)
    }
}