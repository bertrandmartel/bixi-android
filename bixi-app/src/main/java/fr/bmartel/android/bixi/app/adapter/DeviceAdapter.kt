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
package fr.bmartel.android.bixi.app.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import fr.bmartel.android.bixi.app.R
import fr.bmartel.android.bixi.app.inter.IViewHolderClickListener
import fr.bmartel.android.bixi.model.BtDevice

/**
 * Adapter for scanned devices.
 *
 * @author Bertrand Martel
 */
class DeviceAdapter(list: List<BtDevice>,
                    private val mListener: IViewHolderClickListener) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    private var mScanList: List<BtDevice> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.scan_item, parent, false)
        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (deviceAddress, deviceName) = mScanList[position]
        holder.deviceName.text = deviceName + " (" + deviceAddress + ")"
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mScanList.size
    }

    fun setData(deviceList: List<BtDevice>) {
        mScanList = deviceList
    }

    operator fun get(index: Int): BtDevice {
        return mScanList[index]
    }

    /**
     * ViewHolder for Contact item
     */
    inner class ViewHolder
    /**
     * ViewHolder for Contact item
     *
     * @param v
     */
    (v: View, var mListener: IViewHolderClickListener) : RecyclerView.ViewHolder(v), View.OnClickListener {

        var layout: LinearLayout
        var deviceName: TextView

        init {
            deviceName = v.findViewById(R.id.group_name)
            layout = v.findViewById(R.id.group_layout)
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onClick(v)
        }
    }
}