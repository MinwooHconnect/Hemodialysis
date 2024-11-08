package kr.co.hconnect.snuh.mhd.bluetooth.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.hconnect.bluetooth_sdk_android.gatt.BLEState
import kr.co.hconnect.bluetooth_sdk_android_v2.HCBle
import kr.co.hconnect.snuh.mhd.R
import kr.co.hconnect.snuh.mhd.bluetooth.ble_sdk.BleSdkManager
import kr.co.hconnect.snuh.mhd.bluetooth.model.BondModel
import kr.co.hconnect.snuh.mhd.bluetooth.model.ScanResultModel
import kr.co.hconnect.snuh.mhd.bluetooth.ui.bonded_adapter.BluetoothBondedListAdapter
import kr.co.hconnect.snuh.mhd.bluetooth.ui.connectable_adapter.BluetoothScanListAdapter
import kr.co.hconnect.snuh.mhd.bluetooth.viewmodel.BluetoothConnectionViewModel
import kr.co.hconnect.snuh.mhd.databinding.ActivityBluetoothConnectionBinding
import kr.co.hconnect.snuh.mhd.util.Logger

@SuppressLint("MissingPermission")
class BluetoothConnectionActivity : AppCompatActivity() {

    private val viewModel: BluetoothConnectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityBluetoothConnectionBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_bluetooth_connection)

        binding.vm = viewModel
        binding.lifecycleOwner = this

        BleSdkManager.init()

        setRecyclerView(binding)
        setBondedRecyclerView(binding)
        setOnClickListener(binding)
    }

    private fun setBondedRecyclerView(binding: ActivityBluetoothConnectionBinding) {
        val recyclerView: RecyclerView = binding.recyclerViewBonded
        viewModel.apply {
            bondedAdapter = BluetoothBondedListAdapter { bondedDevice ->
                Logger.d("click: $bondedDevice")

                when (HCBle.isConnected(bondedDevice)) {
                    true -> {
                        updateBondsState(
                            BondModel(
                                BLEState.STATE_DISCONNECTING,
                                bondedDevice.bondState,
                                bondedDevice
                            )
                        )
                        HCBle.disconnect(bondedDevice.address) {
                            updateBondsState(
                                BondModel(
                                    BLEState.STATE_DISCONNECTED,
                                    bondedDevice.bondState,
                                    bondedDevice
                                )
                            )
                        }
                    }

                    false -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(300)
                            updateBondsState(
                                BondModel(
                                    BLEState.STATE_CONNECTING,
                                    bondedDevice.bondState,
                                    bondedDevice
                                )
                            )
                            connect(bondedDevice)
                        }
                    }
                }

            }
            recyclerView.adapter = bondedAdapter
            recyclerView.layoutManager = LinearLayoutManager(this@BluetoothConnectionActivity)

            // Divider 추가
            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
            recyclerView.addItemDecoration(dividerItemDecoration)
            bondedDevices.observe(this@BluetoothConnectionActivity) { bondedList ->
                Logger.d("Bond result updated $bondedList")
                bondedAdapter.submitList(
                    bondedList.toMutableList()
                        .filter { !it.device.name.isNullOrBlank() })
            }

            val bondedList = HCBle.getBondedDevices().toMutableList()
            if (bondedList.isEmpty()) return
            val filteredBondedList = bondedList.map {
                BondModel(
                    BLEState.BOND_BONDED,
                    it.bondState,
                    it
                )
            }

            bondedDevices.value = filteredBondedList.toMutableList()
        }


    }

    private fun setRecyclerView(binding: ActivityBluetoothConnectionBinding) {
        val recyclerView: RecyclerView = binding.recyclerView
        viewModel.apply {
            adapter =
                BluetoothScanListAdapter { scanResult ->
                    Logger.d("click: $scanResult")

                    when (HCBle.isConnected(scanResult.device)) {
                        true -> {
                            updateScanningResultsState(
                                ScanResultModel(
                                    BLEState.STATE_DISCONNECTING,
                                    scanResult.device.bondState,
                                    scanResult
                                )
                            )
                            HCBle.disconnect(scanResult.device.address) {
                                updateScanningResultsState(
                                    ScanResultModel(
                                        BLEState.STATE_DISCONNECTED,
                                        scanResult.device.bondState,
                                        scanResult
                                    )
                                )
                            }
                        }

                        false -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(300)
                                updateScanningResultsState(
                                    ScanResultModel(
                                        BLEState.STATE_CONNECTING,
                                        scanResult.device.bondState,
                                        scanResult
                                    )
                                )
                                connect(scanResult.device)
                            }
                        }
                    }


                }


            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this@BluetoothConnectionActivity)

            // Divider 추가
            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
            recyclerView.addItemDecoration(dividerItemDecoration)
            scanResults.observe(this@BluetoothConnectionActivity) { scanResultModels ->
                Logger.d("Scan result updated $scanResultModels")
                adapter.submitList(
                    scanResultModels.toMutableList()
                        .filter { !it.scanResult.device.name.isNullOrBlank() })
            }
        }
    }

    private fun setOnClickListener(binding: ActivityBluetoothConnectionBinding) {
        binding.scanRefresh.setOnClickListener {

            if (viewModel.isScanning.value == false) {
                viewModel.scanResults.value?.clear()
                viewModel.adapter.submitList(emptyList())
                viewModel.updateScanningStatus(true)
                viewModel.scanLeDevice()
            }


        }
    }
}
