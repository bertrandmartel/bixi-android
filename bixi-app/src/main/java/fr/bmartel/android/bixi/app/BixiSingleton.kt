/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Bertrand Martel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.android.bixi.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import fr.bmartel.android.bixi.app.listener.ICommonListener
import fr.bmartel.android.bixi.bluetooth.BixiDevice
import fr.bmartel.android.bixi.client.BixiClient
import fr.bmartel.android.bixi.inter.IBixiListener
import fr.bmartel.android.bixi.model.BtDevice
import java.util.*

/**
 * Singleton object interacting with BixiClient, wrapper to Bixi service.
 *
 * @author Bertrand Martel
 */
class BixiSingleton
/**
 * Build the singleton.
 *
 * @param context
 */
(context: Context) : IBixiListener {

    private val mBixiClient: BixiClient = BixiClient(context = context, bixiListener = this)

    /**
     * list of listeners added by activities.
     */
    private val mListeners = ArrayList<ICommonListener>()

    val deviceList: List<BtDevice>
        get() = mBixiClient.deviceList

    fun connect(activity: Activity) {
        mBixiClient.init(activity = activity)
    }

    fun disconnect() {
        mBixiClient.disconnect()
    }

    fun addListener(listener: ICommonListener) {
        mListeners.add(listener)
    }

    /**
     * remove a singleton listener.
     *
     * @param listener
     */
    fun removeListener(listener: ICommonListener) {
        mListeners.remove(listener)
    }

    override fun onServiceConnected() {
        for (listener in mListeners) {
            listener.onServiceConnected()
        }
    }

    override fun onStartScan() {
        Log.v(TAG, "scan started")
        for (listener in mListeners) {
            listener.onStartScan()
        }
    }

    override fun onEndScan() {
        Log.v(TAG, "scan ended")
        for (listener in mListeners) {
            listener.onEndScan()
        }
    }

    override fun onDeviceDiscovered(device: BtDevice) {
        Log.v(TAG, "device discovered : " + device.deviceName)
        for (listener in mListeners) {
            listener.onDeviceDiscovered(device = device)
        }
    }

    override fun onDeviceDisconnected(device: BtDevice) {
        Log.v(TAG, "device disconnected : " + device.deviceName)
        for (listener in mListeners) {
            listener.onDeviceDisconnected(device = device)
        }
    }

    override fun onDeviceConnected(device: BtDevice) {
        Log.v(TAG, "device connected : " + device.deviceName)
        for (listener in mListeners) {
            listener.onDeviceConnected(device = device)
        }
    }

    override fun onBluetoothOff() {
        for (listener in mListeners) {
            listener.onBluetoothOff()
        }
    }

    override fun onPermissionDenied() {
        for (listener in mListeners) {
            listener.onPermissionDenied()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mBixiClient.onActivityResult(requestCode = requestCode)
    }

    fun startScan(): Boolean {
        return mBixiClient.startScan()
    }

    fun toggleScan() {
        if (mBixiClient.isScanning) {
            mBixiClient.stopScan()
        } else {
            mBixiClient.clearScanningList()
            mBixiClient.startScan()
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mBixiClient.onRequestPermissionsResult(requestCode = requestCode, grantResults = grantResults)
    }

    fun clearScanningList() {
        mBixiClient.clearScanningList()
    }

    fun stopScan() {
        mBixiClient.stopScan()
    }

    fun connectDevice(deviceAddress: String) {
        mBixiClient.connectDevice(deviceAddress = deviceAddress)
    }

    fun getDevice(device: BtDevice): BixiDevice? {
        return mBixiClient.getDevice(device = device)
    }

    fun disconnectAll() {
        mBixiClient.disconnectAll()
    }

    fun isConnected(device: BtDevice): Boolean {
        return mBixiClient.isConnected(device = device)
    }

    companion object {

        private val TAG = BixiSingleton::class.java.simpleName

        /**
         * Singleton static instance.
         */
        private var mInstance: BixiSingleton? = null

        /**
         * Get the static singleton instance.
         *
         * @param context Android application context.
         * @return singleton instance
         */
        fun getInstance(context: Context): BixiSingleton? {
            if (mInstance == null) {
                mInstance = BixiSingleton(context = context)
            }
            return mInstance
        }
    }
}
