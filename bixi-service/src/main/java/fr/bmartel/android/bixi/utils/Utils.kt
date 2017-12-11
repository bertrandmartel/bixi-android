package fr.bmartel.android.bixi.utils

import android.content.Intent
import android.util.Log
import fr.bmartel.android.bixi.bluetooth.BluetoothConst
import fr.bmartel.android.bixi.model.BtDevice
import org.json.JSONException
import org.json.JSONObject

object Utils {

    fun parseArrayList(intent: Intent): BtDevice? {
        val actionsStr = intent.getStringArrayListExtra("")
        if (actionsStr.size > 0) {
            try {
                val mainObject = JSONObject(actionsStr[0])
                if (mainObject.has(BluetoothConst.DEVICE_ADDRESS) && mainObject.has(BluetoothConst.DEVICE_NAME)) {
                    return BtDevice(mainObject.get(BluetoothConst.DEVICE_ADDRESS).toString(),
                            mainObject.get(BluetoothConst.DEVICE_NAME).toString())
                }
            } catch (e: JSONException) {
                Log.e("BtDevice", "exception", e)
            }

        }
        return null
    }
}
