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
package fr.bmartel.android.bixi.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.util.Log
import fr.bmartel.android.bixi.bluetooth.listener.IPushListener
import fr.bmartel.android.bixi.bluetooth.model.GattTask
import fr.bmartel.android.bixi.model.BtDevice
import fr.bmartel.android.bixi.utils.GattUtils
import fr.bmartel.android.bixi.utils.ManualResetEvent
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.Map
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Bluetooth android API processing : contains all android bluetooth api
 *
 *
 * alternative to this is using an Android Service that you can bind to your main activity
 *
 * @author Bertrand Martel
 */
class BluetoothManager {

    private val gattWorkingQueue: LinkedBlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()

    /*
     * Creates a new pool of Thread objects for the download work queue
     */
    private val gattThreadPool = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME.toLong(),
            KEEP_ALIVE_TIME_UNIT,
            gattWorkingQueue)

    /**
     * list of bluetooth connection by address
     */
    val connectionList = HashMap<String, BluetoothConnnection>()

    private val scanningList = HashMap<String, BluetoothDevice>()

    private val scanList = ArrayList<BtDevice>()

    /**
     * event manager used to block / release process
     */
    val eventManager = ManualResetEvent(false)

    /**
     * Bluetooth adapter
     */
    private var mBluetoothAdapter: BluetoothAdapter

    /**
     * message handler
     */
    private var mHandler: Handler

    /**
     * set bluetooth scan
     */
    @Volatile
    var isScanning = false

    private var context: Context

    private val mReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (BluetoothDevice.ACTION_FOUND == intent.action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device.address != null
                        && device.name != null
                        && !scanningList.containsKey(device.address) &&
                        device.name.startsWith("BIXI_")) {

                    scanningList.put(device.address, device)
                    scanList.add(BtDevice(device.address, device.name))
                    try {
                        val obj = JSONObject()
                        obj.put("address", device.address)
                        obj.put("deviceName", device.name)

                        val deviceInfo = ArrayList<String>()
                        deviceInfo.add(obj.toString())
                        broadcastUpdateStringList(BluetoothConst.BT_EVENT_DEVICE_DISCOVERED, deviceInfo)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    constructor(context: Context) {
        this.context = context

        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager

        mBluetoothAdapter = bluetoothManager.adapter

        //init message handler
        mHandler = Handler()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(mReceiver, filter)
    }

    /**
     * clear list adapter (usually before rescanning)
     */
    fun clearScanningList() {
        scanList.clear()
        scanningList.clear()
    }

    /**
     * Scan new Bluetooth device
     */
    fun startScan(): Boolean {
        if (!isScanning) {
            broadcastUpdate(BluetoothConst.BT_EVENT_SCAN_START)
            mHandler?.postDelayed(
                    {
                        if (isScanning) {
                            broadcastUpdate(BluetoothConst.BT_EVENT_SCAN_END)
                            isScanning = false
                            mBluetoothAdapter?.cancelDiscovery()
                        }
                    }, SCAN_PERIOD.toLong())

            isScanning = true

            return mBluetoothAdapter?.startDiscovery() ?: false
        } else {
            Log.v(TAG, "already scanning")
        }
        return false
    }

    /**
     * Stop Bluetooth LE scanning
     */
    fun stopScan() {
        mHandler?.removeCallbacksAndMessages(null)
        isScanning = false
        mBluetoothAdapter?.cancelDiscovery()
        broadcastUpdate(BluetoothConst.BT_EVENT_SCAN_END)
    }

    /**
     * Connect to device's GATT server
     */
    fun connect(address: String?): Boolean {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        val device = mBluetoothAdapter?.getRemoteDevice(address)

        var alreadyInList = false

        if (connectionList.containsKey(address)) {
            alreadyInList = true
        }

        if (alreadyInList) {
            val conn = connectionList[address]
            conn?.setGatt(gatt = device?.connectGatt(context, false, conn.gattCallback))
        } else {
            val conn = BluetoothConnnection(address, device?.name, this)
            connectionList.put(address, conn)
            conn?.setGatt(gatt = device?.connectGatt(context, false, conn.gattCallback))
        }

        return true
    }

    /**
     * Send broadcast data through broadcast receiver
     *
     * @param action action to be sent
     */
    fun broadcastUpdate(action: String) {
        context?.sendBroadcast(Intent(action))
    }

    /**
     * broadcast characteristic value
     *
     * @param action action to be sent (data available)
     */
    fun broadcastUpdateStringList(action: String, valueList: ArrayList<String>) {
        val intent = Intent(action)
        intent.putStringArrayListExtra("", valueList)
        context?.sendBroadcast(intent)
    }

    fun writeCharacteristic(characUid: String?, value: ByteArray?, gatt: BluetoothGatt?, listener: IPushListener?) {

        if (gatt != null && characUid != null && value != null) {

            gattThreadPool.execute(object : GattTask(gatt = gatt, gattUid = characUid, value = value, listener = listener) {
                override fun run() {
                    val charac = GattUtils.getCharacteristic(serviceList = gatt.services, characteristicUid = uid)
                    charac?.setValue(value)

                    gatt.writeCharacteristic(charac)

                    val startTime = System.currentTimeMillis()
                    eventManager.reset()
                    try {
                        eventManager.waitOne(BT_TIMEOUT.toLong())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val endTime = System.currentTimeMillis()

                    if (endTime - startTime >= BT_TIMEOUT) {
                        listener?.onPushFailure()
                    } else {
                        listener?.onPushSuccess()
                    }
                }
            })
            gattThreadPool.execute { }
        } else {
            Log.e(TAG, "Error int writeCharacteristic() input argument NULL")
        }
    }

    fun writeDescriptor(descriptorUid: String?, gatt: BluetoothGatt?, value: ByteArray, serviceUid: String, characUid: String) {
        if (gatt != null && descriptorUid != null) {
            gattThreadPool.execute(object : GattTask(
                    gatt = gatt,
                    descriptorUid = descriptorUid,
                    descriptorVal = value,
                    serviceUid = serviceUid,
                    characUid = characUid) {
                override fun run() {
                    val descriptor = gatt?.getService(UUID.fromString(serviceUid))
                            ?.getCharacteristic(UUID.fromString(descriptorUid))
                            ?.getDescriptor(UUID.fromString(uid))
                    descriptor?.setValue(value)

                    if (descriptor != null) {
                        gatt?.writeDescriptor(descriptor)
                    }
                    eventManager.reset()
                }
            })
            gattThreadPool.execute { }
        } else
            Log.e(TAG, "Error int writeCharacteristic() input argument NULL")
    }

    fun disconnect(deviceAddress: String?): Boolean {
        if (mBluetoothAdapter == null || deviceAddress == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        if (connectionList.containsKey(deviceAddress)) {
            connectionList[deviceAddress]?.bluetoothGatt?.disconnect()
            connectionList[deviceAddress]?.bluetoothGatt?.close()
            return true
        } else {
            Log.e(TAG, "device $deviceAddress not found in list")
        }
        return false
    }

    fun disconnectAll() {
        val it = connectionList.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Map.Entry<String, BluetoothConnnection>
            pair.value.disconnect()
        }
    }

    fun getScanningList(): List<BtDevice> {
        return scanList
    }

    fun unregister() {
        context?.unregisterReceiver(mReceiver)
    }

    companion object {
        private val TAG = BluetoothManager::class.java.name

        // set init pool size
        private val CORE_POOL_SIZE = 1

        // set max pool size
        private val MAXIMUM_POOL_SIZE = 1

        // Sets the amount of time an idle thread will wait for a task before terminating
        private val KEEP_ALIVE_TIME = 5

        // set time unit in seconds
        private val KEEP_ALIVE_TIME_UNIT: TimeUnit = TimeUnit.SECONDS

        //timeout for waiting for response frame from the device
        private val BT_TIMEOUT = 2000

        //set bluetooth scan period
        private val SCAN_PERIOD = 30000
    }
}