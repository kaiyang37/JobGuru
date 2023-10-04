package com.example.jobguru

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jobguru.view.EmpLoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, EmpLoginActivity::class.java)
        startActivity(intent)

//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.fragmentContainer, EmpSignUpFragment())
//        transaction.addToBackStack(null) // Optional: Add the transaction to the back stack
//        transaction.commit()
    }

}