package fr.bmartel.android.bixi.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import fr.bmartel.android.bixi.app.R

/**
 * Adapter for open source projects
 *
 * @author Bertrand Martel
 */
class OpenSourceItemAdapter(context: Context) : BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return COMPONENTS.size
    }

    override fun getItem(position: Int): Any {
        return COMPONENTS[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.open_source_items, parent, false)
        }

        val title = convertView?.findViewById<TextView>(R.id.title)
        val url = convertView?.findViewById<TextView>(R.id.url)

        title?.text = COMPONENTS[position][0]
        url?.text = COMPONENTS[position][1]

        return convertView
    }

    companion object {

        private val COMPONENTS = arrayOf(arrayOf("Icon : Hexagon by Ayse Muskara from the Noun Project", "https://thenounproject.com/term/hexagon/318525/"))
    }
}