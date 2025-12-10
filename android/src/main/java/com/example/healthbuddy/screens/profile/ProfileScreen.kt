@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.healthbuddy.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.android.car.ui.toolbar.MenuItem
import com.example.healthbuddy.R
import com.example.healthbuddy.data.model.HealthInfo
import com.example.healthbuddy.data.model.User
import com.example.healthbuddy.screens.component.Avatar
import com.example.healthbuddy.screens.component.HealthStatsCard
import com.example.healthbuddy.screens.component.MenuItem
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileOverviewScreen(
    user: User,
    healthInfo: HealthInfo?,
    avatarUrl: String? = null,
    onBack: (() -> Unit)? = null,
    onEditProfile: () -> Unit,
    onChooseNewPlan: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Profile",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = AccentLime
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LavenderBand)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Avatar(avatarUrl = avatarUrl, size = 88.dp)

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = user.name ?: user.username,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.email,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )

                    if (!user.birthDay.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Birthday · ${user.birthDay}",
                            color = TextPrimary.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    HealthStatsCard(healthInfo = healthInfo, age = user.age)
                }
            }

            // ---------- MENU LIST ----------
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                MenuItem(
                    icon = R.drawable.ic_profile,
                    label = "Update health info",
                    onClick = onEditProfile
                )
                MenuItem(
                    icon = R.drawable.ic_star,
                    label = "Choose new plan",
                    onClick = onChooseNewPlan
                )
                MenuItem(
                    icon = R.drawable.ic_lock,
                    label = "Privacy policy",
                    onClick = onOpenPrivacy
                )
                MenuItem(
                    icon = R.drawable.ic_setting,
                    label = "Settings",
                    onClick = onOpenSettings
                )
                MenuItem(
                    icon = R.drawable.ic_headphone,
                    label = "Help & support",
                    onClick = onOpenHelp
                )
                MenuItem(
                    icon = R.drawable.ic_logout,
                    label = "Logout",
                    onClick = onLogout
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
