package com.sahtech.medesi.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.sahtech.medesi.R
import com.sahtech.medesi.model.MedicalRecord
import com.sahtech.medesi.model.User

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: User?,medicalRecord: MedicalRecord?) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var isEditing by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("${user?.firstName} ${user?.familyName}") }
    var phoneNumber by remember { mutableStateOf(user?.phoneNumber ?: "Phone Number") }

    val email = user?.email ?: "Email"
    val age = user?.age?.toString() ?: "Age"

    val showBottomSheet = remember { mutableStateOf(false) }

    fun updateProfile(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val token = prefs.getString("auth_token", null) ?: return onError("No token")

        val nameParts = fullName.trim().split(" ", limit = 2)
        val firstNamePart = nameParts.getOrElse(0) { "firstName" }
        val familyNamePart = nameParts.getOrElse(1) { "familyName" }

        val json = JSONObject().apply {
            put("firstName", firstNamePart)
            put("familyName", familyNamePart)
            put("phoneNumber", phoneNumber)
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://expert-duck-pleasing.ngrok-free.app/api/patients/me")
            .put(requestBody)
            .addHeader("Authorization", "Bearer $token")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.message ?: "Request failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    (context as? Activity)?.runOnUiThread {
                        Toast.makeText(context, "Info Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                    onSuccess()
                } else {
                    onError("Update failed: ${response.code}")
                }
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F4EF))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF6F4EF))
                .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                        ,
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF252525)
                        )
                        Text(email, fontSize = 13.sp, color = Color.Gray)
                    }
                }
                Button(
                    onClick = {
                        if (isEditing) {
                            updateProfile(
                                onSuccess = { isEditing = false },
                                onError = { Log.e("UPDATE", it) }
                            )
                        } else {
                            isEditing = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF195EF2)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(if (isEditing) "Submit" else "Edit", color = Color.White, fontSize = 14.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFF1E1E1E))
                .padding(24.dp)
        ) {
            ProfileField("Full Name", fullName, readOnly = !isEditing) { fullName = it }
            ProfileField("Phone Number", phoneNumber, readOnly = !isEditing) { phoneNumber = it }
            ProfileField("Email", email, readOnly = true)
            ProfileField("Age", age, readOnly = true)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Med History", fontSize = 14.sp, color = Color.White)
            Button(
                onClick = { showBottomSheet.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF195EF2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View", fontSize = 14.sp, color = Color.White)
            }
        }
    }

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if(medicalRecord !=null){
                    listOf(
                        "Weight" to medicalRecord.weight,
                        "Height" to medicalRecord.height,
                        "Blood Type" to medicalRecord.bloodType,
                        "Allergies" to medicalRecord.allergies,
                        "Maladies General" to medicalRecord.maladiesGeneral,
                        "Medications" to medicalRecord.medications,
                        "Affection Congenitals" to medicalRecord.affectionCongenitals
                    ).forEach { (label, value) ->
                        Text(label, color = Color.White, fontSize = 14.sp)
                        ProfileField(label = "", value = value)
                    }
                }
                else{
                listOf(
                    "Weight" to "Weight",
                    "Height" to "Height",
                    "Blood Type" to "Blood Type",
                    "Allergies" to "Allergies",
                    "Maladies General" to "Maladies General",
                    "Medications" to "Medications",
                    "Affection Congenitals" to "Affection Congenitals"
                ).forEach { (label, value) ->
                    Text(label, color = Color.White, fontSize = 14.sp)
                    ProfileField(label = "", value = value)
                }}
                Spacer(modifier = Modifier.height(26.dp))
            }
        }
    }
}