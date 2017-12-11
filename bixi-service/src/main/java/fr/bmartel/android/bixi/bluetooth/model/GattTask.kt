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
package fr.bmartel.android.bixi.bluetooth.model

import android.bluetooth.BluetoothGatt

import fr.bmartel.android.bixi.bluetooth.listener.IPushListener

/**
 * A pull/push task to be queued.
 *
 * @author Bertrand Martel
 */
abstract class GattTask : Runnable {

    protected var uid: String
    private var value: ByteArray
    private var descriptorCharacUid: String = ""
    private var descriptorServiceUid: String = ""
    private var listener: IPushListener? = null
    private var gatt: BluetoothGatt? = null

    constructor(gatt: BluetoothGatt, descriptorUid: String, descriptorVal: ByteArray, serviceUid: String, characUid: String) {
        this.gatt = gatt
        this.uid = descriptorUid
        this.value = descriptorVal
        this.descriptorCharacUid = characUid
        this.descriptorServiceUid = serviceUid
        this.descriptorCharacUid = characUid
    }

    constructor(gatt: BluetoothGatt, gattUid: String, value: ByteArray, listener: IPushListener?) {
        this.gatt = gatt
        this.uid = gattUid
        this.value = value
        this.listener = listener
    }
}
