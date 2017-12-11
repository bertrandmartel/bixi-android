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

/**
 * Some Bluetooth specific constants.
 *
 * @author Bertrand Martel
 */
object BluetoothConst {
    val BT_EVENT_DEVICE_DISCONNECTED = "fr.bmartel.android.bixi.service.bluetooth.BT_EVENT_DEVICE_DISCONNECTED"
    val BT_EVENT_DEVICE_CONNECTED = "fr.bmartel.android.bixi.service.bluetooth.BT_EVENT_DEVICE_CONNECTED"
    val BT_EVENT_SCAN_START = "fr.bmartel.android.bixi.service.bluetooth.BT_EVENT_SCAN_START"
    val BT_EVENT_SCAN_END = "fr.bmartel.android.bixi.service.bluetooth.BT_EVENT_SCAN_END"
    val BT_EVENT_DEVICE_DISCOVERED = "fr.bmartel.android.bixi.service.bluetooth.BT_EVENT_DEVICE_DISCOVERED"
    val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    val DEVICE_ADDRESS = "address"
    val DEVICE_NAME = "deviceName"
}