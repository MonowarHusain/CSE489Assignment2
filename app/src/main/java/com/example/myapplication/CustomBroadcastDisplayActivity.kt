package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityCustomBroadcastDisplayBinding

class CustomBroadcastDisplayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomBroadcastDisplayBinding

    companion object {
        const val CUSTOM_ACTION = "com.example.cse489assignment2.CUSTOM_BROADCAST"
        const val EXTRA_BROADCAST_TEXT = "broadcast_text"
    }

    private val customReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val text = intent?.getStringExtra(EXTRA_BROADCAST_TEXT)
            binding.tvBroadcastText.text = text ?: ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomBroadcastDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.custom_broadcast_receiver)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val filter = IntentFilter(CUSTOM_ACTION)
        ContextCompat.registerReceiver(
            this,
            customReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        val receivedText = intent.getStringExtra(EXTRA_BROADCAST_TEXT) ?: ""

        val broadcastIntent = Intent(CUSTOM_ACTION).apply {
            putExtra(EXTRA_BROADCAST_TEXT, receivedText)
            setPackage(packageName)
        }
        sendBroadcast(broadcastIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(customReceiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}