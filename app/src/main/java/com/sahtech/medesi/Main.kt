package com.sahtech.medesi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sahtech.medesi.ui.theme.MedESITheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate


data class NavItem(
    val route: String,
    val iconRes: Int,
    val label: String
)

data class Consultation(
    val time: String,
    val title: String,
    val description: String
)

data class User(
    val id: Int,
    val familyName: String,
    val firstName: String,
    val email: String,
    val age: Int,
    val phoneNumber: String,
    val patientType: String
)




class Main : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val userState = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        fetchUserInfo(context) { user ->
            userState.value = user
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
            composable("home") { HomeScreen(user = userState.value) }
            composable("profile") { ProfileScreen(user = userState.value) }
            composable("notifications") { HomeScreen(user = userState.value) }
            composable("search") { HomeScreen(user = userState.value) }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navItems = listOf(
        NavItem("home", R.drawable.ic_home, "Home"),
        NavItem("search", R.drawable.ic_search, "Discover"),
        NavItem("notifications", R.drawable.ic_notifications, "Notifications"),
        NavItem("profile", R.drawable.ic_profile, "Profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    val selectedWidth = totalWidth * (1f / 3f)
    val unselectedWidth = (totalWidth - selectedWidth) / (navItems.size - 1)
    val backgroundColor = if (currentRoute == "profile") {
        Color(0xFF1E1E1E)
    } else {
        Color(0xFFF6F4EF)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.Black),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val selected = currentRoute == item.route
                val animatedWidth by animateDpAsState(
                    targetValue = if (selected) selectedWidth else unselectedWidth,
                    label = "nav_item_width"
                )

                Box(
                    modifier = Modifier
                        .height(54.dp)
                        .width(animatedWidth)
                        .clip(RoundedCornerShape(30.dp))
                        .background(if (selected) Color.White else Color.Transparent)
                        .clickable {
                            if (item.route != currentRoute) {
                                navController.navigate(item.route)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            tint = if (selected) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        // Show label only after full expansion with fade animation
                        if (selected) {
                            val isFullyExpanded = animatedWidth == selectedWidth

                            AnimatedVisibility(
                                visible = isFullyExpanded && item.label.isNotEmpty(),
                                enter = fadeIn(animationSpec = tween(durationMillis = 150)),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = item.label,
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(user: User?) {
    val today = remember { LocalDate.now() }
    val allDates = remember {
        val end = today.withDayOfMonth(today.lengthOfMonth())
        val dates = mutableListOf<LocalDate>()
        var current = today
        while (!current.isAfter(end)) {
            dates.add(current)
            current = current.plusDays(1)
        }
        dates
    }

    val selectedDate = remember { mutableStateOf(today) }

    val consultationTitle = "Doctor Consultation"
    val consultationDescription = "You are scheduled to have a consultation with doctor House regarding your smoking addiction"
    val consultationTime = "9:30"
    val username = user?.firstName?.uppercase() ?: "Patient"
    val consultationList = listOf(
        Consultation(
            time = "10:00 AM",
            title = "General Checkup",
            description = "Routine physical examination and health assessment."
        ),
        Consultation(
            time = "11:30 AM",
            title = "Dermatology",
            description = "Consultation for skin-related issues like rashes or acne."
        ),
        Consultation(
            time = "1:00 PM",
            title = "Cardiology",
            description = "Heart health evaluation and ECG report review."
        ),
        Consultation(
            time = "3:00 PM",
            title = "Pediatrics",
            description = "Child wellness check and immunization schedule."
        ),
        Consultation(
            time = "4:30 PM",
            title = "Orthopedics",
            description = "Joint pain diagnosis and mobility evaluation."
        )
    )



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F4EF))
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // Greeting
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("HI $username", fontWeight = FontWeight.Bold,color = Color(0xFF252525))
                    Text("WELCOME BACK!", fontSize = 12.sp, color = Color.Gray)
                }
            }
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "Profile",
                    tint = Color.Unspecified
                )
            }

        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "INCOMING\nCONSULTATIONS",
            fontWeight = FontWeight.Normal,
            fontSize = 26.sp,
            color = Color(0xFF252525),
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Calendar Row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            items(allDates) { date ->
                val isSelected = date == selectedDate.value
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        date.dayOfWeek.name.take(3),
                        fontSize = 16.sp,
                        color = if (isSelected) Color.Black else Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF195EF2) else Color.White)
                            .clickable { selectedDate.value = date },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = if (isSelected) Color.White else Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            items(consultationList) { consultation ->
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        consultation.time,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF252525)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ConsultationCard(
                        title = consultation.title,
                        description = consultation.description
                    )
                }
            }
        }


    }
}

@Composable
fun ConsultationCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(Color(0xFFD43F3F), shape = RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF252525))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: User?) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var isEditing by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("${user?.firstName.orEmpty()} ${user?.familyName.orEmpty()}") }
    var phoneNumber by remember { mutableStateOf(user?.phoneNumber ?: "") }

    val email = user?.email ?: "Email"
    val age = user?.age?.toString() ?: "Age"

    val showBottomSheet = remember { mutableStateOf(false) }

    fun updateProfile(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val token = prefs.getString("auth_token", null) ?: return onError("No token")

        val nameParts = fullName.trim().split(" ", limit = 2)
        val firstNamePart = nameParts.getOrElse(0) { "" }
        val familyNamePart = nameParts.getOrElse(1) { "" }

        val json = JSONObject().apply {
            put("firstName", firstNamePart)
            put("familyName", familyNamePart)
            put("phoneNumber", phoneNumber)
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://medesi.loca.lt/api/patients/me")
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
                    )
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
                listOf(
                    "Weight" to "68.5",
                    "Height" to "172",
                    "Blood Type" to "O+",
                    "Allergies" to "None",
                    "Maladies General" to "Asthma",
                    "Medications" to "None",
                    "Affection Congenitals" to "None"
                ).forEach { (label, value) ->
                    Text(label, color = Color.White, fontSize = 14.sp)
                    ProfileField(label = "", value = value)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String, readOnly: Boolean = true, onValueChange: (String) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.White,
                disabledContainerColor = Color(0xFF2C2C2C),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MedESITheme {
        MainApp()
    }
}

fun fetchUserInfo(context: Context, callback: (User?) -> Unit) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("auth_token", null) ?: return callback(null)

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://medesi.loca.lt/api/patients/me")
        .addHeader("Authorization", "Bearer $token")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
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

                    prefs.edit().apply {
                        putString("firstName", user.firstName)
                        putString("familyName", user.familyName)
                        putString("email", user.email)
                        putInt("age", user.age)
                        putString("phoneNumber", user.phoneNumber)
                        putString("patientType", user.patientType)
                    }.commit()


                    callback(user)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                }
            } ?: callback(null)
        }
    })
}
