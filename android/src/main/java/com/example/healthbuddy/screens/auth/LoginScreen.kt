package com.example.healthbuddy.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.example.healthbuddy.screens.component.BrandCircle
import com.example.healthbuddy.ui.theme.AccentLime
import com.example.healthbuddy.ui.theme.BackgroundDark
import com.example.healthbuddy.ui.theme.ButtonBg
import com.example.healthbuddy.ui.theme.InputContainer
import com.example.healthbuddy.ui.theme.InputText
import com.example.healthbuddy.ui.theme.LavenderBand
import com.example.healthbuddy.ui.theme.SurfaceDark
import com.example.healthbuddy.ui.theme.TextPrimary
import com.example.healthbuddy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onBack: () -> Unit,
    onForgotPassword: () -> Unit,
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }

    uiState.error?.let { err ->
        Text(text = err, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))
    }

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
                        "Đăng nhập",
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
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LavenderBand)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Username hoặc email",
                    color = InputText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("example@example.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputContainer,
                        unfocusedContainerColor = InputContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = InputText
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Mật khẩu",
                    color = InputText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    placeholder = { Text("••••••••••••••••") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        val label = if (passwordVisible) "Hide" else "Show"
                        Text(
                            text = label,
                            color = TextSecondary,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { passwordVisible = !passwordVisible }
                        )
                    },
                    shape = RoundedCornerShape(22.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputContainer,
                        unfocusedContainerColor = InputContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = InputText
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Quên mật khẩu?",
                        color = TextPrimary,
                        modifier = Modifier
                            .clickable { onForgotPassword.invoke() }
                            .padding(4.dp),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onLogin(email.trim(), password) },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp),
                clip = false)
            ) {
                Text("Đăng nhập", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "hoặc đăng nhập với",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandCircle(
                    R.drawable.ic_google,
                    onClick = {

                    }
                )
                Spacer(Modifier.width(18.dp))
                BrandCircle(
                    R.drawable.ic_facebook,
                    onClick = {

                    }
                )
                Spacer(Modifier.width(18.dp))
                BrandCircle(
                    R.drawable.ic_fingerprint,
                    onClick = {

                    }
                )
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Bạn chưa có tài khoản? ", color = TextSecondary)
                Text(
                    "Đăng ký",
                    color = AccentLime,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onSignUp.invoke() }
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoginPreview() {
    LoginScreen(
        uiState = AuthUiState(),
        onBack = {},
        onForgotPassword = {},
        onSignUp = {}
    )
}
