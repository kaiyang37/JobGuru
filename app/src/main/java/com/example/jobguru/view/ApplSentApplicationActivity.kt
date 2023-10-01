package com.example.jobguru.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplSentApplicationBinding
import com.example.jobguru.databinding.ActivityApplSubmitApplicationBinding

class ApplSentApplicationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApplSentApplicationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplSentApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.doneBtn.setOnClickListener {
            val intent = Intent(this, ApplApplicationHistoryActivity::class.java)
            startActivity(intent)
        }
    }
}