# Bixi Android library #

[![License](http://img.shields.io/:license-mit-blue.svg)](LICENSE.md)

Bixi library is an Android service receiving gesture events from Bixi Bluetooth devices

## What is Bixi ?

[Bixi](https://bixi.io/) is a small bluetooth touch-free controller with built in battery made by Bluemint Labs

![bixi](https://user-images.githubusercontent.com/5183022/33852136-9f7792de-deb9-11e7-9e9c-9c1ef68cf721.png)


## Gestures

List of gesture events : 

* CENTER_TO_TOP
* CENTER_TO_BOTTOM
* CENTER_TO_LEFT
* CENTER_TO_RIGHT
* LINEAR_END
* LINEAR_CHANGE (the specific value is given)
* DOUBLE_TAP

## How to include it in your Android project ?

with Gradle, from jcenter or maven central :

```groovy
compile 'fr.bmartel:bixi-service:1.0'
```

## How to use it ?

Use `BixiClient` service wrapper :

* Kotlin example :

```kotlin
class MainActivity : Activity() {

    private lateinit var bixiClient: BixiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bixiClient = BixiClient(this, object : IBixiListener {
            override fun onServiceConnected() {
                Log.v("bixi-lib", "service is connected - ready to receive events")

                //clear the list before scanning
                bixiClient.clearScanningList()
                //start scanning
                bixiClient.startScan()
            }

            override fun onStartScan() {
                Log.v("bixi-lib", "scan started")
            }

            override fun onEndScan() {
                Log.v("bixi-lib", "scan stopped")
            }

            override fun onDeviceDiscovered(device: BtDevice) {
                Log.v("bixi-lib", "new Bixi device discovered : " + device.deviceName)

                //stop scanning
                bixiClient.stopScan()

                //connect to this device
                bixiClient.connectDevice(device.deviceAddress)
            }

            override fun onDeviceDisconnected(device: BtDevice) {
                Log.v("bixi-lib", "device disconnected : " + device.deviceName)
            }

            override fun onDeviceConnected(device: BtDevice) {
                Log.v("bixi-lib", "device connected : " + device.deviceName)

                //set a listener to be notified when a gesture event occur
                bixiClient.getDevice(device)!!.setBixiGestureListener(object : IGestureListener {
                    override fun onGestureChange(event: BixiEvent) {
                        Log.v("bixi-lib", "received gesture event : " + event.gesture)
                    }
                })
            }

            override fun onBluetoothOff() {
                Log.v("bixi-lib", "bluetooth hasn't been activated (user refused the popup)")
            }

            override fun onPermissionDenied() {
                Log.v("bixi-lib", "location permission was denied (necessary to scan)")
            }
        })
        bixiClient.init(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        bixiClient.onRequestPermissionsResult(requestCode, grantResults)
    }
}
```

* Java example :

```java
public class MainActivity extends Activity {

    BixiClient bixiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bixiClient = new BixiClient(this, new IBixiListener() {
            @Override
            public void onServiceConnected() {
                Log.v("bixi-lib", "service is connected - ready to receive events");

                //clear the list before scanning
                bixiClient.clearScanningList();
                //start scanning
                bixiClient.startScan();
            }

            @Override
            public void onStartScan() {
                Log.v("bixi-lib", "scan started");
            }

            @Override
            public void onEndScan() {
                Log.v("bixi-lib", "scan stopped");
            }

            @Override
            public void onDeviceDiscovered(BtDevice device) {
                Log.v("bixi-lib", "new Bixi device discovered : " + device.getDeviceName());

                //stop scanning
                bixiClient.stopScan();

                //connect to this device
                bixiClient.connectDevice(device.getDeviceAddress());
            }

            @Override
            public void onDeviceDisconnected(BtDevice device) {
                Log.v("bixi-lib", "device disconnected : " + device.getDeviceName());
            }

            @Override
            public void onDeviceConnected(BtDevice device) {
                Log.v("bixi-lib", "device connected : " + device.getDeviceName());

                //set a listener to be notified when a gesture event occur
                bixiClient.getDevice(device).setBixiGestureListener(new IGestureListener() {
                    @Override
                    public void onGestureChange(BixiEvent event) {
                        Log.v("bixi-lib", "received gesture event : " + event.getGesture());
                    }
                });
            }

            @Override
            public void onBluetoothOff() {
                Log.v("bixi-lib", "bluetooth hasn't been activated (user refused the popup)");
            }

            @Override
            public void onPermissionDenied() {
                Log.v("bixi-lib", "location permission was denied (necessary to scan)");
            }
        });
        bixiClient.init(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        bixiClient.onRequestPermissionsResult(requestCode, grantResults);
    }
}
```

In the app module, you can find an example using a Singleton

## Requirements

This project require Android SDK 19+

## Build Library

### Get source code

```bash
git clone git@github.com:bertrandmartel/bixi-android.git
cd bixi-android
```

### Build

```bash
./gradlew build
```

## About

* Hexagon Icon by Ayse Muskara from the Noun Project, https://thenounproject.com/term/hexagon/318525/
* appcompat-v7, design & recyclerview-v7
* source of Bixi image (on the drawer header) : http://gadgetsin.com/bixi-remote-control-lets-you-control-smart-devices-with-gestures.htm

## License

```
The MIT License (MIT) Copyright (c) 2017 Bertrand Martel
```