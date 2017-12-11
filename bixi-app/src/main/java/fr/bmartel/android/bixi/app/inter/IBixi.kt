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
package fr.bmartel.android.bixi.app.inter

import android.app.Dialog
import android.support.v7.widget.Toolbar

import fr.bmartel.android.bixi.bluetooth.BixiDevice
import fr.bmartel.android.bixi.model.BtDevice

/**
 * Bixi interface implemented by root activity.
 *
 * @author Bertrand Martel
 */
interface IBixi {

    /**
     * Get Bixi devices list.
     *
     * @return
     */
    val deviceList: List<BtDevice>

    var toolbar: Toolbar

    /**
     * set opened dialog in activity
     *
     * @param dialog
     */
    fun setCurrentDialog(dialog: Dialog)

    /**
     * Hide all toolbar button.
     */
    fun hideMenuButton()

    /**
     * set toolbar title
     *
     * @param title
     */
    fun setToolbarTitle(title: String)

    /**
     * connect to device with given address
     *
     * @param deviceAddress
     */
    fun connectDevice(deviceAddress: String)

    /**
     * stop bluetooth scan
     */
    fun stopScan()

    /**
     * Get Bixi device by BtDevice
     *
     * @param mDevice
     * @return
     */
    fun getDevice(mDevice: BtDevice): BixiDevice?

    /**
     * Disconnect all devices.
     */
    fun disconnectAll()

    /**
     * Check if input Bixi device is connected
     *
     * @param mDevice
     * @return
     */
    fun isConnected(mDevice: BtDevice): Boolean
}
