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
package fr.bmartel.android.bixi.app.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import fr.bmartel.android.bixi.app.R
import fr.bmartel.android.bixi.app.adapter.EventsAdapter
import fr.bmartel.android.bixi.app.common.SimpleDividerItemDecoration
import fr.bmartel.android.bixi.app.inter.IBixi
import fr.bmartel.android.bixi.app.inter.IViewHolderClickListener
import fr.bmartel.android.bixi.inter.IGestureListener
import fr.bmartel.android.bixi.model.BixiEvent
import fr.bmartel.android.bixi.model.BtDevice
import java.util.*

/**
 * Device fragment used to list Bixi events (gesture).
 *
 * @author Bertrand Martel
 */
open class DeviceFragment : CommonFragment() {

    private lateinit var mEmptyFrame: FrameLayout
    private lateinit var mDisplayFrame: RelativeLayout
    private lateinit var mEventListView: RecyclerView
    private lateinit var mEventAdapter: EventsAdapter

    private lateinit var eventList: MutableList<BixiEvent>
    private lateinit var mDevice: BtDevice

    private lateinit var mActivity: FragmentActivity

    private val rootActivity: IBixi
        get() = activity as IBixi

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.device_fragment, container, false)
        val bundle = this.arguments
        if (bundle != null) {
            mDevice = bundle.getParcelable(context?.resources?.getString(R.string.device_field_to_fragment))
        }

        mEmptyFrame = view.findViewById(R.id.waiting_frame)
        mDisplayFrame = view.findViewById(R.id.display_frame)

        mEmptyFrame.visibility = View.GONE
        mDisplayFrame.visibility = View.VISIBLE

        mEventListView = view.findViewById(R.id.event_list)

        eventList = ArrayList()

        mEventAdapter = EventsAdapter(
                list = eventList,
                listener = object : IViewHolderClickListener {
                    override fun onClick(view: View) {

                    }
                })
        //set layout manager
        mEventListView.layoutManager = GridLayoutManager(activity, 1, LinearLayoutManager.VERTICAL, false)

        //set line decoration
        mEventListView.addItemDecoration(SimpleDividerItemDecoration(
                activity?.applicationContext
        ))

        mEventListView.adapter = mEventAdapter

        val device = rootActivity.getDevice(mDevice)

        device?.setBixiGestureListener(gestureListener = object : IGestureListener {
            override fun onGestureChange(event: BixiEvent) {
                activity?.runOnUiThread {
                    eventList.add(0, event)
                    mEventAdapter.setData(eventList)
                    mEventAdapter.notifyDataSetChanged()
                }
            }
        }) ?: Log.e(TAG, "device not found")

        return view
    }

    private fun updateToolbarTitle(device: BtDevice) {
        rootActivity.setToolbarTitle(device.deviceName + " (" + device.deviceAddress + ")")
    }

    override fun onResume() {
        super.onResume()
        rootActivity.hideMenuButton()
        updateToolbarTitle(mDevice)
        if (!rootActivity.isConnected(mDevice)) {
            showProgress()
            rootActivity.connectDevice(mDevice.deviceAddress)
        }
    }

    fun onDeviceConnected(device: BtDevice) {
        hideProgress()
        mDevice = device
        val bixiDevice = rootActivity.getDevice(mDevice)

        bixiDevice?.setBixiGestureListener(gestureListener = object : IGestureListener {
            override fun onGestureChange(event: BixiEvent) {
                activity?.runOnUiThread {
                    eventList.add(0, event)
                    mEventAdapter.setData(eventList)
                    mEventAdapter.notifyDataSetChanged()
                }
            }
        }) ?: Log.e(TAG, "device not found")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context as FragmentActivity
        }
    }

    companion object {
        private val TAG = DeviceFragment::class.java.simpleName
    }
}
