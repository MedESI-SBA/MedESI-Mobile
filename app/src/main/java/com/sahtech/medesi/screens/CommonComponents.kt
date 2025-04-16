package com.sahtech.medesi.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

import com.sahtech.medesi.R
import com.sahtech.medesi.model.NavItem

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
