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
package fr.bmartel.android.bixi.app.fragment

import android.app.ProgressDialog

import fr.bmartel.android.bixi.app.R

/**
 * Fragment shared among Device & Scan fragment.
 *
 * @author Bertrand Martel
 */
open class CommonFragment : android.support.v4.app.Fragment() {

    private var progress: ProgressDialog? = null

    protected fun showProgress() {
        progress = ProgressDialog(context)
        progress?.setMessage(context?.resources?.getString(R.string.connection_message))
        progress?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress?.isIndeterminate = true
        progress?.progress = 0
        progress?.show()
    }

    protected fun hideProgress() {
        progress?.cancel()
    }
}
