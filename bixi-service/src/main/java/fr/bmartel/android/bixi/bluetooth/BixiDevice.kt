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

import android.bluetooth.BluetoothGattCharacteristic
import fr.bmartel.android.bixi.bluetooth.listener.ICharacteristicListener
import fr.bmartel.android.bixi.bluetooth.listener.IDeviceInitListener
import fr.bmartel.android.bixi.inter.IGestureListener
import fr.bmartel.android.bixi.model.BixiEvent
import fr.bmartel.android.bixi.model.BixiGesture
import fr.bmartel.android.bixi.utils.ByteUtils
import java.util.*

/**
 * Bixi device management.
 *
 * @author Bertrand Martel
 */
class BixiDevice {

    private val mConn: BluetoothConnnection

    private var characteristicListener: ICharacteristicListener

    private var mListener: IGestureListener? = null

    private var mInitListener: IDeviceInitListener? = null

    constructor(conn: BluetoothConnnection) {
        mConn = conn

        characteristicListener = object : ICharacteristicListener {

            override fun onCharacteristicReadReceived(charac: BluetoothGattCharacteristic) {}

            override fun onCharacteristicChangeReceived(charac: BluetoothGattCharacteristic) {

                if (charac.uuid.toString().equals(BIXI_GESTURE_CHAR)) {
                    val data = charac.value

                    when (data[0]) {
                        0x0B.toByte() -> {
                            val direction = ByteUtils.byteArrayToInt(Arrays.copyOfRange(data, 1, 5))
                            when (direction) {
                                0x00000001 -> setGesture(data, BixiGesture.CENTER_TO_TOP)
                                0x00000100 -> setGesture(data, BixiGesture.CENTER_TO_BOTTOM)
                                0x00010000 -> setGesture(data, BixiGesture.CENTER_TO_LEFT)
                                0x01000000 -> setGesture(data, BixiGesture.CENTER_TO_RIGHT)
                                else -> when (data[5]) {
                                    0x01.toByte() -> setGesture(data, BixiGesture.LINEAR_CHANGE)
                                    0x00.toByte() -> setGesture(data, BixiGesture.LINEAR_END)
                                    0xA5.toByte() -> setGesture(data, BixiGesture.DOUBLE_TAP)
                                    else -> setGesture(data, BixiGesture.UNKNOWN)
                                }
                            }
                        }
                    }
                }
            }

            override fun onCharacteristicWriteReceived(charac: BluetoothGattCharacteristic) {}
        }
    }

    private fun setGesture(payload: ByteArray, gesture: BixiGesture) {
        mListener?.onGestureChange(BixiEvent(payload, gesture))
    }

    fun setBixiGestureListener(gestureListener: IGestureListener) {
        mListener = gestureListener
    }

    fun init() {
        mConn?.enableDisableNotification(service = UUID.fromString(BIXI_SERVICE), charac = UUID.fromString(BIXI_GESTURE_CHAR), enable = true)
        mConn?.enableGattNotifications(serviceUid = BIXI_SERVICE, characUid = BIXI_GESTURE_CHAR)

        mConn?.writeCharacteristic(charac = BIXI_INTERNAL_CMD_CHAR, value = CMD_LEFT, listener = null)
        mConn?.writeCharacteristic(charac = BIXI_INTERNAL_CMD_CHAR, value = CMD_RIGHT, listener = null)

        mInitListener?.onInit()
    }

    fun setInitListener(listener: IDeviceInitListener) {
        mInitListener = listener
    }

    /**
     * notify characteristic read event
     *
     * @param characteristic Bluetooth characteristic
     */
    fun notifyCharacteristicReadReceived(characteristic: BluetoothGattCharacteristic) {
        characteristicListener?.onCharacteristicReadReceived(charac = characteristic)
    }

    fun notifyCharacteristicWriteReceived(characteristic: BluetoothGattCharacteristic) {
        characteristicListener?.onCharacteristicWriteReceived(charac = characteristic)
    }

    /**
     * notify characteristic change event
     *
     * @param characteristic Bluetooth characteristic
     */
    fun notifyCharacteristicChangeReceived(characteristic: BluetoothGattCharacteristic) {
        characteristicListener?.onCharacteristicChangeReceived(charac = characteristic)
    }

    companion object {

        @JvmField
        val BIXI_SERVICE = "deadbeef-8165-c341-9aeb-4ba40df63ace"
        @JvmField
        val BIXI_GESTURE_CHAR = "deadbeef-8165-c341-9aeb-4ba47ebded8e"
        @JvmField
        val BIXI_INTERNAL_CMD_CHAR = "deadbeef-8165-c341-9aeb-4ba4c31f84a3"

        @JvmField
        val CMD_LEFT = ByteUtils.concatByteArray(
                byteArrayOf(0x49, 0x43, 0x5F, 0x53, 0x45, 0x54, 0x50, 0x52, 0x4F, 0x4C),
                byteArrayOf(0x0B, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00))

        @JvmField
        val CMD_RIGHT = ByteUtils.concatByteArray(
                byteArrayOf(0x49, 0x43, 0x5F, 0x53, 0x45, 0x54, 0x50, 0x52, 0x4F, 0x52),
                byteArrayOf(0x0B, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00))
    }
}