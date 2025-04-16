package com.sahtech.medesi.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sahtech.medesi.model.MedicalRecord
import com.sahtech.medesi.model.User
import com.sahtech.medesi.network.fetchMedicalRecord
import com.sahtech.medesi.network.fetchUserInfo

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val userState = remember { mutableStateOf<User?>(null) }
    val medicalRecordState = remember { mutableStateOf<MedicalRecord?>(null) }

    LaunchedEffect(Unit) {
        fetchUserInfo(context) { user ->
            userState.value = user
        }
        fetchMedicalRecord(context) { medicalRecord ->
            medicalRecordState.value = medicalRecord
        }
    }
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(user = userState.value, navController = navController) }
            composable("profile") { ProfileScreen(user = userState.value, medicalRecord = medicalRecordState.value) }
            composable("notifications") { HomeScreen(user = userState.value, navController = navController) }
            composable("search") { HomeScreen(user = userState.value, navController = navController) }
        }
    }
}