package com.example.healthbuddy.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbuddy.R
import com.example.healthbuddy.screens.component.LabelText
import com.example.healthbuddy.screens.component.tfColorsCommon
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    onReset: (code: String, newPassword: String) -> Unit = { _, _ -> }
) {
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val confirmError = remember(password, confirm) { confirm.isNotEmpty() && confirm != password }
    val strongEnough = remember(password) { password.length >= 8 } // tweak policy
    val formValid = code.isNotBlank() && strongEnough && !confirmError

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBack.invoke() }) {
                        Image(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = {
                    Text(
                        "Set Password",
                        color = AccentLime,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Enter the verification code and set your new password.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LavenderBand)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                LabelText("Verification code")
                TextField(
                    value = code,
                    onValueChange = { code = it },
                    placeholder = { Text("6-digit code") },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    colors = tfColorsCommon(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(Modifier.height(16.dp))
                LabelText("Password")
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    placeholder = { Text("••••••••") },
                    visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val label = if (pwVisible) "Hide" else "Show"
                        Text(
                            text = label,
                            color = TextSecondary,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { pwVisible = !pwVisible }
                        )
                    },
                    shape = RoundedCornerShape(22.dp),
                    colors = tfColorsCommon(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(Modifier.height(16.dp))
                LabelText("Confirm Password")
                TextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    singleLine = true,
                    isError = confirmError,
                    placeholder = { Text("••••••••") },
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val label = if (confirmVisible) "Hide" else "Show"
                        Text(
                            text = label,
                            color = if (confirmError) Color(0xFFFF6B6B) else TextSecondary,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { confirmVisible = !confirmVisible }
                        )
                    },
                    shape = RoundedCornerShape(22.dp),
                    colors = tfColorsCommon(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                if (confirmError) {
                    Text(
                        text = "Passwords do not match",
                        color = Color(0xFFFF6B6B),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp, start = 8.dp)
                    )
                }
                if (!strongEnough && password.isNotEmpty()) {
                    Text(
                        text = "Use at least 8 characters.",
                        color = AccentLime, // subtle hint; change to warning color if you prefer
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { if (formValid) onReset(code.trim(), password) },
                enabled = formValid,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp), clip = false)
            ) {
                Text("Reset Password", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
@Preview(showSystemUi = true)
@Composable
private fun SetPwPreview() {
    ResetPasswordScreen(onBack = {})
}
