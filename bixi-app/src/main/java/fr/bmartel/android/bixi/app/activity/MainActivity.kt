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
package fr.bmartel.android.bixi.app.activity

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import fr.bmartel.android.bixi.app.R
import fr.bmartel.android.bixi.app.fragment.DeviceFragment
import fr.bmartel.android.bixi.app.fragment.ScanFragment
import fr.bmartel.android.bixi.bluetooth.BixiDevice
import fr.bmartel.android.bixi.model.BtDevice

/**
 * Main activity starting Scan fragment.
 *
 * @author Bertrand Martel
 */
class MainActivity : BaseActivity() {

    /**
     * one dialog to show above the activity. We dont want to have multiple Dialog above each other.
     */
    private var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayout(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, ScanFragment(), resources.getString(R.string.scan_fragment_name)).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDialog?.dismiss()
        mSingleton?.removeListener(listener = this)
        try {
            mSingleton?.disconnect()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        mSingleton?.startScan()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun setCurrentDialog(dialog: Dialog) {
        mDialog = dialog
    }

    override fun connectDevice(deviceAddress: String) {
        mSingleton?.connectDevice(deviceAddress = deviceAddress)
    }

    override fun stopScan() {
        mSingleton?.stopScan()
    }

    override fun getDevice(mDevice: BtDevice): BixiDevice? {
        return mSingleton?.getDevice(device = mDevice)
    }

    override fun disconnectAll() {
        mSingleton?.disconnectAll()
    }

    override fun isConnected(device: BtDevice): Boolean {
        return mSingleton?.isConnected(device = device) ?: false
    }

    override fun onServiceConnected() {
        mSingleton?.startScan()
    }

    override fun onStartScan() {
        showProgressBar()
    }

    override fun onEndScan() {
        hideProgressBar()
    }

    override fun onDeviceDiscovered(device: BtDevice) {
        val scanFragment = supportFragmentManager.findFragmentByTag(resources.getString(R.string.scan_fragment_name)) as ScanFragment
        if (scanFragment != null && scanFragment.isVisible) {
            scanFragment.onDeviceDiscovered()
        }
    }

    override fun onDeviceDisconnected(device: BtDevice) {}

    override fun onDeviceConnected(device: BtDevice) {
        val scanFragment = supportFragmentManager.findFragmentByTag(resources.getString(R.string.scan_fragment_name)) as ScanFragment
        if (scanFragment != null && scanFragment.isVisible) {
            scanFragment.onDeviceConnected(device = device)
        } else {
            val deviceFragment = supportFragmentManager.findFragmentByTag(resources.getString(R.string.device_fragment_name)) as DeviceFragment
            (deviceFragment as? DeviceFragment)?.onDeviceConnected(device = device)
        }

    }

    override fun onBluetoothOff() {
        Toast.makeText(this, resources.getString(R.string.bluetooth_required), Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onPermissionDenied() {
        Toast.makeText(this, resources.getString(R.string.bluetooth_required), Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onPause() {
        super.onPause()
        mSingleton?.stopScan()
        mSingleton?.disconnectAll()
    }
}
