package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityCustomBroadcastInputBinding

class CustomBroadcastInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomBroadcastInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomBroadcastInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.custom_broadcast_input_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSendBroadcast.setOnClickListener {
            val inputText = binding.etInputText.text?.toString()?.trim() ?: ""
            if (inputText.isEmpty()) {
                binding.etInputText.error = getString(R.string.error_empty_input)
                Toast.makeText(this, getString(R.string.error_empty_input), Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, CustomBroadcastDisplayActivity::class.java).apply {
                    putExtra("broadcast_text", inputText)
                }
                startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}