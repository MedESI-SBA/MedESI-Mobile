package com.sahtech.medesi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.sahtech.medesi.network.fetchUserInfo
import com.sahtech.medesi.screens.MainApp
import com.sahtech.medesi.ui.theme.MedESITheme


class Main : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        if (token==null){
            startActivity(Intent(this,Login::class.java))
            finish()
        }
        fetchUserInfo(this) { user ->
            if (user != null) {
                Log.d("USERINFO", "Fetched user: $user")
            } else {
                Log.e("USERINFO", "Failed to fetch user")
            }
        }

        setContent {
            MedESITheme {
                MainApp()
            }
        }
    }
}


