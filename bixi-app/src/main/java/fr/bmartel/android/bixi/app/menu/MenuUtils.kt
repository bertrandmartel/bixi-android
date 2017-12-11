/**
 * The MIT License (MIT)
 *
 *
 * Copyright (c) 2017 Bertrand Martel
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.android.bixi.app.menu

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.widget.DrawerLayout
import android.view.MenuItem

import fr.bmartel.android.bixi.app.R
import fr.bmartel.android.bixi.app.dialog.AboutDialog
import fr.bmartel.android.bixi.app.dialog.OpenSourceItemsDialog
import fr.bmartel.android.bixi.app.inter.IBixi

/**
 * Some functions used to manage Menu.
 *
 * @author Bertrand Martel
 */
object MenuUtils {

    private val TAG = MenuUtils::class.java.simpleName

    /**
     * Execute actions according to selected menu item
     *
     * @param menuItem MenuItem object
     * @param drawer  navigation drawer
     * @param context  android context
     */
    fun selectDrawerItem(menuItem: MenuItem, drawer: DrawerLayout, context: Context, activity: IBixi) {

        when (menuItem.itemId) {
            R.id.open_source_components -> {
                val dialog = OpenSourceItemsDialog(context = context)
                activity.setCurrentDialog(dialog = dialog)
                dialog.show()
            }
            R.id.rate_app -> {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.applicationContext.packageName)))
            }
            R.id.about_app -> {
                val dialog = AboutDialog(context = context)
                activity.setCurrentDialog(dialog = dialog)
                dialog.show()
            }
            R.id.report_bugs -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", context.resources.getString(R.string.developper_mail), null))
                intent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.issue_object))
                intent.putExtra(Intent.EXTRA_TEXT, context.resources.getString(R.string.issue_message))
                context.startActivity(Intent.createChooser(intent, context.resources.getString(R.string.issue_title)))
            }
        }
        drawer.closeDrawers()
    }
}