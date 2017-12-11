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
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout

import fr.bmartel.android.bixi.app.R
import fr.bmartel.android.bixi.app.adapter.DeviceAdapter
import fr.bmartel.android.bixi.app.common.SimpleDividerItemDecoration
import fr.bmartel.android.bixi.app.inter.IBixi
import fr.bmartel.android.bixi.app.inter.IViewHolderClickListener
import fr.bmartel.android.bixi.model.BtDevice

/**
 * Scan Fragment : list bluetooth devices scanned.
 *
 * @author Bertrand Martel
 */
open class ScanFragment : CommonFragment() {

    private lateinit var mFragment: DeviceFragment
    private lateinit var mEmptyFrame: FrameLayout
    private lateinit var mDisplayFrame: RelativeLayout
    private lateinit var mScanListView: RecyclerView
    private lateinit var mScanAdapter: DeviceAdapter
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mActivity: FragmentActivity

    private val rootActivity: IBixi?
        get() = activity as IBixi?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.scan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEmptyFrame = view.findViewById(R.id.waiting_frame)
        mDisplayFrame = view.findViewById(R.id.display_frame)

        val deviceList: List<BtDevice>? = rootActivity?.deviceList

        if (deviceList != null && deviceList.isNotEmpty()) {
            mEmptyFrame.visibility = View.GONE
            mDisplayFrame.visibility = View.VISIBLE
        }

        mScanListView = view.findViewById(R.id.device_list)

        val mScanList = rootActivity?.deviceList

        if (mScanList != null) {
            mScanAdapter = DeviceAdapter(
                    list = mScanList,
                    mListener = object : IViewHolderClickListener {
                        override fun onClick(view: View) {
                            val index = mScanListView.getChildAdapterPosition(view)
                            if (index != null) {
                                rootActivity?.stopScan()
                                showProgress()
                                val addr = mScanAdapter.get(index).deviceAddress
                                if (addr != null) {
                                    rootActivity?.connectDevice(addr)
                                }
                            }
                        }
                    })
        }

        //set layout manager
        mScanListView.layoutManager = GridLayoutManager(activity, 1, LinearLayoutManager.VERTICAL, false)

        //set line decoration
        mScanListView.addItemDecoration(SimpleDividerItemDecoration(
                activity?.applicationContext
        ))

        mScanListView.adapter = mScanAdapter

        //setup swipe refresh
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh)
        mSwipeRefreshLayout.setOnRefreshListener {
            mScanAdapter.setData((activity as IBixi).deviceList)
            mScanAdapter.notifyDataSetChanged()
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        rootActivity?.hideMenuButton()
        rootActivity?.toolbar?.menu?.findItem(R.id.scanning_button)?.isVisible = true

        rootActivity?.disconnectAll()

        val deviceList: List<BtDevice>? = rootActivity?.deviceList

        if (deviceList != null && deviceList.isNotEmpty()) {
            mEmptyFrame.visibility = View.GONE
            mDisplayFrame.visibility = View.VISIBLE
        } else {
            mEmptyFrame.visibility = View.VISIBLE
            mDisplayFrame.visibility = View.GONE
        }
        if (deviceList != null) {
            mScanAdapter.setData(deviceList)
        }
        mScanAdapter.notifyDataSetChanged()
        updateToolbarTitle()
    }

    private fun updateToolbarTitle() {
        rootActivity?.setToolbarTitle(resources.getString(R.string.title_scan) + " (" + rootActivity?.deviceList?.size + ")")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context as FragmentActivity
        }
    }

    fun onDeviceDiscovered() {
        updateToolbarTitle()
        activity?.runOnUiThread {
            val deviceList: List<BtDevice>? = rootActivity?.deviceList

            if (deviceList != null && deviceList.isNotEmpty()) {
                mEmptyFrame.visibility = View.GONE
                mDisplayFrame.visibility = View.VISIBLE
            }
            if (deviceList != null) {
                mScanAdapter.setData(deviceList)
            }
            mScanAdapter.notifyDataSetChanged()
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    fun onDeviceConnected(device: BtDevice) {
        hideProgress()
        activity?.runOnUiThread {
            mFragment = DeviceFragment()
            val args = Bundle()
            args.putParcelable(context?.resources?.getString(R.string.device_field_to_fragment), device)
            mFragment.arguments = args

            val ft = mActivity.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.fragment_frame, mFragment, context?.resources?.getString(R.string.device_fragment_name))
            ft?.addToBackStack(null)
            ft?.commit()
        }
    }
}