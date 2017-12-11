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
package fr.bmartel.android.bixi.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Object used to wrap broadcast intent json output
 *
 * @author Bertrand Martel
 */
data class BtDevice(val deviceAddress: String, val deviceName: String) : Parcelable {

    constructor(source: Parcel) : this(source.readString(), source.readString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(this.deviceAddress)
        dest?.writeString(this.deviceName)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<BtDevice> = object : Parcelable.Creator<BtDevice> {
            override fun createFromParcel(source: Parcel): BtDevice {
                return BtDevice(source)
            }

            override fun newArray(size: Int): Array<BtDevice?> {
                return arrayOfNulls(size)
            }
        }
    }
}
