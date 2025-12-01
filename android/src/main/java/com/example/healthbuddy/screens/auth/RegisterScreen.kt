@file:OptIn(ExperimentalMaterial3Api::class)
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegister: (name: String, emailOrPhone: String, password: String) -> Unit = { _, _, _ -> },
    onLogin: () -> Unit,
    onTerms: () -> Unit,
    onPrivacy: () -> Unit,
    onGoogle: () -> Unit,
    onFacebook: () -> Unit,
    onBiometric: () -> Unit,
) {
    var fullName by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    val confirmError = remember(password, confirm) { confirm.isNotEmpty() && confirm != password }

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
                        "Create Account",
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
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Let's Start!",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LavenderBand)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                FieldLabel("Username")
                TextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Your username") },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    colors = tfColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(Modifier.height(16.dp))
                FieldLabel("Email")
                TextField(
                    value = emailOrPhone,
                    onValueChange = { emailOrPhone = it },
                    placeholder = { Text("example@example.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    colors = tfColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(Modifier.height(16.dp))
                FieldLabel("Password")
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    placeholder = { Text("••••••••••••••••") },
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
                    colors = tfColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(Modifier.height(16.dp))
                FieldLabel("Confirm Password")
                TextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    singleLine = true,
                    isError = confirmError,
                    placeholder = { Text("••••••••••••••••") },
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
                    colors = tfColors(),
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
            }

            Spacer(Modifier.height(16.dp))
            TermsParagraph(
                onTerms = onTerms,
                onPrivacy = onPrivacy
            )

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { if (!confirmError) onRegister(fullName.trim(), emailOrPhone.trim(), password) },
                enabled = fullName.isNotBlank() && emailOrPhone.isNotBlank() && password.isNotBlank() && !confirmError,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBg),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp), clip = false)
            ) {
                Text("Sign Up", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "or sign up with",
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
                BrandCircle(R.drawable.ic_google) { onGoogle.invoke() }
                Spacer(Modifier.width(18.dp))
                BrandCircle(R.drawable.ic_facebook) { onFacebook.invoke() }
                Spacer(Modifier.width(18.dp))
                BrandCircle(R.drawable.ic_fingerprint) { onBiometric.invoke() }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 22.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ", color = TextSecondary)
                Text(
                    "Log in",
                    color = AccentLime,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onLogin.invoke() }
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = InputText,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
    )
}

@Composable
private fun tfColors() = TextFieldDefaults.colors(
    focusedContainerColor = InputContainer,
    unfocusedContainerColor = InputContainer,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    cursorColor = InputText
)

@Composable
private fun TermsParagraph(
    onTerms: (() -> Unit)?,
    onPrivacy: (() -> Unit)?
) {
    LocalUriHandler.current
    val annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = TextSecondary)) {
            append("By continuing, you agree to ")
        }
        pushStringAnnotation(tag = "terms", annotation = "terms")
        withStyle(SpanStyle(color = AccentLime, fontWeight = FontWeight.SemiBold)) {
            append("Terms of Use")
        }
        pop()
        withStyle(SpanStyle(color = TextSecondary)) { append(" and ") }
        pushStringAnnotation(tag = "privacy", annotation = "privacy")
        withStyle(SpanStyle(color = AccentLime, fontWeight = FontWeight.SemiBold)) {
            append("Privacy Policy")
        }
        pop()
        withStyle(SpanStyle(color = TextSecondary)) { append(".") }
    }
    Text(
        text = annotated,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { /* to enable span clicks area */ },
        fontSize = 14.sp
    )
}

@Preview(showSystemUi = true)
@Composable
private fun RegisterPreview() {
    RegisterScreen(
        onBack = {},
        onLogin = {},
        onTerms = {},
        onPrivacy = {},
        onGoogle = {},
        onFacebook = {},
        onBiometric = {}
    )
}
