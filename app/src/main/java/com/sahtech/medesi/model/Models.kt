package com.sahtech.medesi.model

data class NavItem(val route: String, val iconRes: Int, val label: String)
data class Consultation(val time: String, val title: String, val description: String)
data class User(
    val id: Int,
    val familyName: String,
    val firstName: String,
    val email: String,
    val age: Int,
    val phoneNumber: String,
    val patientType: String
)


data class MedicalRecord(
   val weight: String,
   val height: String,
   val bloodType: String,
   val allergies: String,
   val maladiesGeneral: String,
   val medications: String,
   val affectionCongenitals: String
)
