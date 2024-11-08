package kr.co.hconnect.snuh.mhd.bluetooth.ble_sdk

import android.bluetooth.le.ScanResult
import androidx.activity.ComponentActivity
import kr.co.hconnect.bluetooth_sdk_android_v2.HCBle
import kr.co.hconnect.snuh.mhd.MyApplication
import kr.co.hconnect.snuh.mhd.util.Logger
import kr.co.hconnect.snuh.mhd.util.MyPermission

object BleSdkManager {

    fun init() {
        val context = MyApplication.getAppContext()
        HCBle.init(context)
    }

    /**
     * Start BLE scan
     */
    fun startBleScan(onScanResult: (ScanResult) -> Unit, onScanStop: () -> Unit) {

        val isGranted = MyPermission.isGrantedPermissions(
            MyApplication.getAppContext(),
            MyPermission.PERMISSION_BLUETOOTH
        )

        when (isGranted) {
            true -> {
                doScan(
                    onScanResult = { scanResult: ScanResult ->
                        onScanResult.invoke(scanResult)
                    },
                    onScanStop = onScanStop
                )
            }

            false -> {
                MyPermission.launchPermissions(MyPermission.PERMISSION_BLUETOOTH) { permissions ->
                    if (permissions.containsValue(false)) {
                        Logger.e("Permission denied")
                    } else {
                        Logger.d("Permission granted")
                        doScan(
                            onScanResult = { scanResult: ScanResult ->
                                onScanResult.invoke(scanResult)
                            },
                            onScanStop = onScanStop
                        )
                    }
                }
            }
        }
    }

    private fun doScan(onScanResult: (ScanResult) -> Unit, onScanStop: () -> Unit) {

        HCBle.scanLeDevice(
            onScanResult = { scanResult: ScanResult ->
                onScanResult.invoke(scanResult)
            },
            onScanStop = {
                onScanStop()
            },
        )
    }

    /**
     * Stop BLE scan
     */
    fun stopBleScan() {
        HCBle.scanStop()
    }
}