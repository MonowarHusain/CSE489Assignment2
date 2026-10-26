package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityBatteryReceiverBinding

class BatteryReceiverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBatteryReceiverBinding
    private var isReceiverRegistered = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            val pct = if (level != -1 && scale > 0) {
                ((level * 100) / scale.toFloat()).toInt()
            } else {
                0
            }

            binding.tvBatteryPercentage.text = getString(R.string.current_battery, pct)
            binding.progressBarBattery.progress = pct

            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

            val statusString = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> {
                    when (chargePlug) {
                        BatteryManager.BATTERY_PLUGGED_AC -> getString(R.string.charging_ac)
                        BatteryManager.BATTERY_PLUGGED_USB -> getString(R.string.charging_usb)
                        BatteryManager.BATTERY_PLUGGED_WIRELESS -> getString(R.string.charging_wireless)
                        else -> getString(R.string.charging)
                    }
                }
                BatteryManager.BATTERY_STATUS_FULL -> getString(R.string.battery_full)
                BatteryManager.BATTERY_STATUS_DISCHARGING -> getString(R.string.discharging)
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> getString(R.string.not_charging)
                else -> getString(R.string.unknown)
            }

            binding.tvChargingStatus.text = getString(R.string.charging_status_format, statusString)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.battery_receiver_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        if (!isReceiverRegistered) {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.registerReceiver(this, batteryReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(batteryReceiver, filter)
            }
            isReceiverRegistered = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (isReceiverRegistered) {
            unregisterReceiver(batteryReceiver)
            isReceiverRegistered = false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}