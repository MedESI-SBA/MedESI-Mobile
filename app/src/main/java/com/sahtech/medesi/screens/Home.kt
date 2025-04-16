package com.sahtech.medesi.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sahtech.medesi.R
import com.sahtech.medesi.model.Consultation
import com.sahtech.medesi.model.User
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(user: User?, navController: NavHostController)
{
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
                        .clickable {
                            navController.navigate("profile")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

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
