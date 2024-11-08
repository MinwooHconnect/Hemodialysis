package kr.co.hconnect.snuh.mhd.bluetooth.model

import android.bluetooth.BluetoothDevice

data class BondModel(
    val state: Int,
    val bondState: Int,
    val device: BluetoothDevice,
)