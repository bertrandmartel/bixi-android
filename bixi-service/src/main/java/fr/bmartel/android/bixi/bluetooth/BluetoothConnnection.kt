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

import android.bluetooth.*
import android.os.Build
import android.util.Log
import fr.bmartel.android.bixi.bluetooth.listener.IDeviceInitListener
import fr.bmartel.android.bixi.bluetooth.listener.IPushListener
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Bluetooth device connection management.
 *
 * @author Bertrand Martel
 */
class BluetoothConnnection {

    var gattCallback: BluetoothGattCallback

    var bluetoothGatt: BluetoothGatt? = null

    private var manager: BluetoothManager

    var device: BixiDevice? = null

    var isConnected = false

    /**
     * Build Bluetooth device connection
     *
     * @param address
     */
    constructor(address: String, deviceName: String?, manager: BluetoothManager) {

        this.manager = manager

        this.gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int,
                                                 newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.v(TAG, "Connected to GATT server.")
                    Log.v(TAG, "Attempting to start service discovery:" + gatt.discoverServices())
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    isConnected = false
                    Log.v(TAG, "Disconnected from GATT server.")
                    try {
                        val obj = JSONObject()
                        obj.put(BluetoothConst.DEVICE_ADDRESS, address)
                        obj.put(BluetoothConst.DEVICE_NAME, deviceName)

                        val values = ArrayList<String>()
                        values.add(obj.toString())

                        manager.broadcastUpdateStringList(action = BluetoothConst.BT_EVENT_DEVICE_DISCONNECTED, valueList = values)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    bluetoothGatt?.close()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val test = Runnable {
                        val btDevice = gatt.device
                        if (btDevice.bondState == 10 && Build.VERSION.SDK_INT >= 19) {
                            btDevice.createBond()
                        }
                        device = BixiDevice(this@BluetoothConnnection)

                        device?.setInitListener(object : IDeviceInitListener {
                            override fun onInit() {
                                try {
                                    val obj = JSONObject()
                                    obj.put(BluetoothConst.DEVICE_ADDRESS, address)
                                    obj.put(BluetoothConst.DEVICE_NAME, deviceName)

                                    val values = ArrayList<String>()
                                    values.add(obj.toString())

                                    isConnected = true

                                    manager.broadcastUpdateStringList(action = BluetoothConst.BT_EVENT_DEVICE_CONNECTED, valueList = values)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        })
                        device?.init()
                    }
                    val testThread = Thread(test)
                    testThread.start()

                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status)
                }
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                manager.eventManager.set()
                device?.notifyCharacteristicWriteReceived(characteristic = characteristic)
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt,
                                              characteristic: BluetoothGattCharacteristic,
                                              status: Int) {
                manager.eventManager.set()
                device?.notifyCharacteristicReadReceived(characteristic = characteristic)
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                manager.eventManager.set()
            }

            override// Characteristic notification
            fun onCharacteristicChanged(gatt: BluetoothGatt,
                                        characteristic: BluetoothGattCharacteristic) {
                device?.notifyCharacteristicChangeReceived(characteristic = characteristic)
            }
        }
    }

    fun writeCharacteristic(charac: String, value: ByteArray, listener: IPushListener?) {
        manager?.writeCharacteristic(characUid = charac, value = value, gatt = bluetoothGatt, listener = listener)
    }

    fun enableDisableNotification(service: UUID, charac: UUID, enable: Boolean) {
        bluetoothGatt?.setCharacteristicNotification(bluetoothGatt?.getService(service)?.getCharacteristic(charac), enable)
    }

    fun enableGattNotifications(serviceUid: String, characUid: String) {
        manager?.writeDescriptor(
                descriptorUid = BluetoothConst.CLIENT_CHARACTERISTIC_CONFIG,
                gatt = bluetoothGatt,
                value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE,
                serviceUid = serviceUid,
                characUid = characUid)
    }

    fun setGatt(gatt: BluetoothGatt?) {
        this.bluetoothGatt = gatt
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    companion object {
        private val TAG = BluetoothConnnection::class.java.name
    }
}
