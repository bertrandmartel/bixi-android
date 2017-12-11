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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import fr.bmartel.android.bixi.app.BixiSingleton
import fr.bmartel.android.bixi.app.R
import fr.bmartel.android.bixi.app.inter.IBixi
import fr.bmartel.android.bixi.app.listener.ICommonListener
import fr.bmartel.android.bixi.app.menu.MenuUtils
import fr.bmartel.android.bixi.model.BtDevice

/**
 * Base Activity which is the same for all activities.
 *
 * @author Bertrand Martel
 */
abstract class BaseActivity : AppCompatActivity(), ICommonListener, IBixi {

    /**
     * scan image button at top right
     */
    private lateinit var scanImage: ImageButton

    /**
     * navigationdrawer
     */
    private lateinit var mDrawer: DrawerLayout

    /**
     * toggle on the hamburger button
     */
    private lateinit var drawerToggle: ActionBarDrawerToggle

    /**
     * navigation view
     */
    private lateinit var nvDrawer: NavigationView

    /**
     * activity layout ressource id
     */
    private var layoutId: Int = 0

    /**
     * Bixi singleton.
     */
    protected var mSingleton: BixiSingleton? = null

    /**
     * toolbar progress bar at top right
     */
    private var progressBar: ProgressBar? = null

    override val deviceList: List<BtDevice>
        get() = mSingleton?.deviceList ?: ArrayList()

    override lateinit var toolbar: Toolbar

    /**
     * set activity ressource id
     *
     * @param resId
     */
    protected fun setLayout(resId: Int) {
        layoutId = resId
    }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSingleton = BixiSingleton.getInstance(context = applicationContext)
        mSingleton?.addListener(listener = this)
        mSingleton?.connect(activity = this)

        setContentView(layoutId)

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar_item)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.inflateMenu(R.menu.toolbar_menu)

        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout)
        drawerToggle = setupDrawerToggle()
        mDrawer.setDrawerListener(drawerToggle)
        nvDrawer = findViewById(R.id.nvView)

        // Setup drawer view
        setupDrawerContent(nvDrawer)
    }

    /**
     * setup navigation view
     *
     * @param navigationView
     */
    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            MenuUtils.selectDrawerItem(
                    menuItem = menuItem,
                    drawer = mDrawer,
                    context = this@BaseActivity,
                    activity = this@BaseActivity)
            false
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mSingleton?.onActivityResult(requestCode = requestCode, resultCode = resultCode, data = data)
    }

    /**
     * setup action drawer
     *
     * @return
     */
    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        return object : ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mDrawer.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Make sure this is the method with just `Bundle` as the signature
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onBackPressed() {
        if (this.mDrawer.isDrawerOpen(GravityCompat.START)) {
            this.mDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.toolbar_menu, menu)

        scanImage = menu.findItem(R.id.scanning_button).actionView.findViewById(R.id.bluetooth_scan_stop)
        progressBar = menu.findItem(R.id.scanning_button).actionView.findViewById(R.id.bluetooth_scanning)
        scanImage.setOnClickListener { mSingleton?.toggleScan() }
        progressBar?.setOnClickListener { mSingleton?.toggleScan() }
        runOnUiThread { showProgressBar() }

        return super.onCreateOptionsMenu(menu)
    }

    override fun hideMenuButton() {
        toolbar.menu.findItem(R.id.scanning_button).isVisible = false
    }

    override fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mSingleton?.onRequestPermissionsResult(requestCode = requestCode, permissions = permissions, grantResults = grantResults)
    }

    /**
     * show progress bar to indicate scanning
     */
    protected fun showProgressBar() {
        scanImage.visibility = View.GONE
        progressBar?.visibility = View.VISIBLE
    }

    /**
     * hide scanning progress bar
     */
    protected fun hideProgressBar() {
        scanImage.visibility = View.VISIBLE
        progressBar?.visibility = View.GONE
    }
}