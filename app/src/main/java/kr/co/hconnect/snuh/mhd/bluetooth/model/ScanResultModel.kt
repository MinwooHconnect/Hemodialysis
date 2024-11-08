package kr.co.hconnect.snuh.mhd.bluetooth.model

import android.bluetooth.le.ScanResult

data class ScanResultModel(
    val state: Int,
    val bondState: Int,
    val scanResult: ScanResult,
)