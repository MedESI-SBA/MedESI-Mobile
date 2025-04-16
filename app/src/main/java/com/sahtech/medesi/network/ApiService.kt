package com.sahtech.medesi.network

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.sahtech.medesi.model.MedicalRecord
import com.sahtech.medesi.model.User
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


fun fetchUserInfo(context: Context, callback: (User?) -> Unit) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("auth_token", null) ?: return callback(null)

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://expert-duck-pleasing.ngrok-free.app/api/patients/me")
        .addHeader("Authorization", "Bearer $token")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("fetchUserInfo", "Failure: ${e.message}")
            callback(null)
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { jsonString ->
                try {
                    val json = JSONObject(jsonString)
                    val user = User(
                        id = json.getInt("id"),
                        familyName = json.getString("familyName"),
                        firstName = json.getString("firstName"),
                        email = json.getString("email"),
                        age = json.getInt("age"),
                        phoneNumber = json.getString("phoneNumber"),
                        patientType = json.getString("patientType")
                    )

                    prefs.edit(commit = true) {
                        putString("firstName", user.firstName)
                        putString("familyName", user.familyName)
                        putString("email", user.email)
                        putInt("age", user.age)
                        putString("phoneNumber", user.phoneNumber)
                        putString("patientType", user.patientType)
                    }

                    callback(user)
                } catch (e: Exception) {
                    Log.e("fetchUserInfo", "Parsing error", e)
                    callback(null)
                }
            } ?: callback(null)
        }
    })
}






fun fetchMedicalRecord(context: Context, callback: (MedicalRecord?) -> Unit) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("auth_token", null) ?: return callback(null)

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://expert-duck-pleasing.ngrok-free.app/api/patients/medical-record")
        .addHeader("Authorization", "Bearer $token")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("fetchMedicalRecord", "Failure: ${e.message}")
            callback(null)
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { jsonString ->
                try {
                    val json = JSONObject(jsonString)
                    val medicalRecord = MedicalRecord(
                        weight = json.optString("weight_kg").takeIf { it != "null"  } ?: "none",
                        height = json.optString("height_cm").takeIf { it != "null" } ?: "none",
                        bloodType = json.optString("blood_group").takeIf { it != "null"  } ?: "none",
                        allergies = json.optString("medication_allergies").takeIf { it != "null" } ?: "none",
                        maladiesGeneral = json.optString("general_diseases").takeIf { it != "null"  } ?: "none",
                        medications = json.optString("medication_details").takeIf { it != "null"  } ?: "none",
                        affectionCongenitals = json.optString("congenital_conditions").takeIf { it != "null"  } ?: "none"
                    )


                    prefs.edit(commit = true) {
                        putString("weight", medicalRecord.weight)
                        putString("height", medicalRecord.height)
                        putString("blood", medicalRecord.bloodType)
                        putString("allergies", medicalRecord.allergies)
                        putString("maladies", medicalRecord.maladiesGeneral)
                        putString("medications", medicalRecord.medications)
                        putString("affection", medicalRecord.affectionCongenitals)
                    }

                    callback(medicalRecord)
                } catch (e: Exception) {
                    Log.e("fetchMedicalRecord", "Parsing error", e)
                    callback(null)
                }
            } ?: callback(null)
        }
    })
}
