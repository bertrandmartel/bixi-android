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
package fr.bmartel.android.bixi.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import fr.bmartel.android.bixi.bluetooth.BluetoothConnnection
import fr.bmartel.android.bixi.bluetooth.BluetoothManager
import fr.bmartel.android.bixi.model.BtDevice
import java.util.*

/**
 * Service connecting to Bixi device.
 *
 * @author Bertrand Martel
 */
class BixiService : Service() {

    /**
     * Service binder
     */
    private val mBinder = LocalBinder()

    private lateinit var btManager: BluetoothManager

    val scanningList: List<BtDevice>
        get() = btManager.getScanningList()

    val isScanning: Boolean
        get() = btManager.isScanning

    val connectionList: HashMap<String, BluetoothConnnection>
        get() = btManager.connectionList

    /*
     * LocalBinder that render public getService() for public access
     */
    inner class LocalBinder : Binder() {
        val service: BixiService
            get() = this@BixiService
    }

    override fun onCreate() {
        btManager = BluetoothManager(context = this)
    }

    override fun onDestroy() {
        btManager.unregister()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    fun stopScan() {
        btManager.stopScan()
    }

    fun connect(deviceAddress: String) {
        btManager.connect(deviceAddress)
    }

    fun startScan(): Boolean {
        return btManager.startScan()
    }

    fun clearScanningList() {
        btManager.clearScanningList()
    }

    fun disconnect(deviceAddress: String): Boolean? {
        return btManager.disconnect(deviceAddress)
    }

    fun disconnectall() {
        btManager.disconnectAll()
    }

}