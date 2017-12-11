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
package fr.bmartel.android.bixi.client

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log

import java.util.ArrayList

import fr.bmartel.android.bixi.bluetooth.BixiDevice
import fr.bmartel.android.bixi.bluetooth.BluetoothConst
import fr.bmartel.android.bixi.inter.IBixiListener
import fr.bmartel.android.bixi.model.BtDevice
import fr.bmartel.android.bixi.service.BixiService
import fr.bmartel.android.bixi.utils.Utils

/**
 * Bixi client wrapper to be used to bind service & call service API.
 *
 * @author Bertrand Martel
 */
class BixiClient {

    /**
     * android context.
     */
    private var mContext: Context
    /**
     * listener used for catching Bluetooth events.
     */
    private lateinit var mBixiListener: IBixiListener

    /**
     * Bixi service instance.
     */
    private var bixiService: BixiService? = null

    /**
     * define if Bixi service is bounded.
     */
    private var mBound: Boolean = false

    /**
     * intent used to launch bixi service.
     */
    private var bixiServiceIntent: Intent? = null

    private var mActivity: Activity? = null

    /**
     * broadcast receiver used to catch user click on service notification.
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val btDevice: BtDevice?
            when (action) {
                BluetoothConst.BT_EVENT_SCAN_START -> mBixiListener.onStartScan()
                BluetoothConst.BT_EVENT_SCAN_END -> mBixiListener.onEndScan()
                BluetoothConst.BT_EVENT_DEVICE_DISCOVERED -> {
                    btDevice = Utils.parseArrayList(intent)
                    if (btDevice != null) {
                        mBixiListener.onDeviceDiscovered(device = btDevice)
                    }
                }
                BluetoothConst.BT_EVENT_DEVICE_DISCONNECTED -> {
                    btDevice = Utils.parseArrayList(intent)
                    if (btDevice != null) {
                        mBixiListener.onDeviceDisconnected(device = btDevice)
                    }
                }
                BluetoothConst.BT_EVENT_DEVICE_CONNECTED -> {
                    btDevice = Utils.parseArrayList(intent)
                    if (btDevice != null) {
                        mBixiListener.onDeviceConnected(device = btDevice)
                    }
                }
            }
        }
    }

    /**
     * Build Bixi client.
     *
     * @param context      Android context.
     * @param bixiListener bluetooth event listener.
     */
    constructor(context: Context, bixiListener: IBixiListener) {
        mContext = context
        mBixiListener = bixiListener
    }

    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            bixiService = (service as BixiService.LocalBinder).service
            mBixiListener.onServiceConnected()
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    val deviceList: List<BtDevice>
        get() = bixiService?.scanningList ?: ArrayList()

    val isScanning: Boolean
        get() = bixiService?.isScanning ?: false

    /**
     * bind service and register receiver.
     */
    fun connect() {
        bindService()
        registerReceiver()
    }

    /**
     * disconnect from service (that will not close server if service is not destroyed eg service not in persistent mode).
     */
    fun disconnect() {
        if (mBound == true) {
            mContext.unbindService(mServiceConnection)
            mContext.unregisterReceiver(receiver)
            mBound = false
        }
    }

    /**
     * register receiver.
     */
    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(BluetoothConst.BT_EVENT_SCAN_START)
        filter.addAction(BluetoothConst.BT_EVENT_SCAN_END)
        filter.addAction(BluetoothConst.BT_EVENT_DEVICE_DISCOVERED)
        filter.addAction(BluetoothConst.BT_EVENT_DEVICE_CONNECTED)
        filter.addAction(BluetoothConst.BT_EVENT_DEVICE_DISCONNECTED)
        mContext.registerReceiver(receiver, filter)
    }

    /**
     * bind to Bixi service.
     */
    private fun bindService() {
        bixiServiceIntent = Intent()
        bixiServiceIntent?.setClassName(mContext, SERVICE_NAME)

        mContext.startService(bixiServiceIntent)

        mBound = mContext.bindService(bixiServiceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE)

        if (mBound == true) {
            Log.v(TAG, "service started")
        } else {
            Log.e(TAG, "service not started")
        }
    }

    fun requestLocationPermission(activity: Activity?): Boolean {
        if (Build.VERSION.SDK_INT >= 23 && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity?.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_COARSE_LOCATION)
            return false
        }
        return true
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_COARSE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init(mActivity)
                } else {
                    mBixiListener.onPermissionDenied()
                }
            }
        }
    }

    fun enableBt(activity: Activity?): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (!adapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity?.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        return adapter.isEnabled
    }

    fun onActivityResult(requestCode: Int) {
        if (requestCode == REQUEST_ENABLE_BT) {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter.isEnabled) {
                init(mActivity)
            } else {
                mBixiListener.onBluetoothOff()
            }
        }
    }

    fun startScan(): Boolean {
        return bixiService?.startScan() ?: false
    }

    fun stopScan() {
        bixiService?.stopScan()
    }

    fun init(activity: Activity?) {
        mActivity = activity
        if (enableBt(activity) && requestLocationPermission(activity)) {
            connect()
        }
    }

    fun clearScanningList() {
        bixiService?.clearScanningList()
    }

    fun connectDevice(deviceAddress: String) {
        bixiService?.connect(deviceAddress)
    }

    fun getDevice(device: BtDevice): BixiDevice? {
        return bixiService?.connectionList?.get(device.deviceAddress)?.device ?: null
    }

    fun disconnectAll() {
        bixiService?.disconnectall()
    }

    fun isConnected(device: BtDevice): Boolean {
        return bixiService?.connectionList?.get(device.deviceAddress)?.isConnected ?: false
    }

    companion object {

        private val TAG = BixiClient::class.java.simpleName

        /**
         * service intent name.
         */
        val SERVICE_NAME = "fr.bmartel.android.bixi.service.BixiService"

        /**
         * define if bluetooth is enabled on device
         */
        private val REQUEST_ENABLE_BT = 1

        private val REQUEST_PERMISSION_COARSE_LOCATION = 2
    }
}