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
package fr.bmartel.android.bixi.utils

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.util.Log
import fr.bmartel.android.bixi.bluetooth.BluetoothConst
import fr.bmartel.android.bixi.model.BtDevice
import org.json.JSONException
import org.json.JSONObject

/**
 * Some gatt processing useful functions
 *
 * @author Bertrand Martel
 */
object GattUtils {

    /**
     * Retrieve gatt characteristic object from service list
     *
     * @param serviceList
     * @param characteristicUid
     * @return
     */
    fun getCharacteristic(serviceList: List<BluetoothGattService>, characteristicUid: String): BluetoothGattCharacteristic? {
        for (service: BluetoothGattService in serviceList) {
            for (charac: BluetoothGattCharacteristic in service.characteristics) {
                if (charac.uuid.toString() == characteristicUid) {
                    return charac
                }
            }
        }
        return null
    }
}
