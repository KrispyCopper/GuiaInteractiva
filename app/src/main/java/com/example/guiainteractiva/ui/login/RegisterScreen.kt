package com.example.guiainteractiva.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guiainteractiva.R
import com.example.guiainteractiva.ui.theme.GuiaInteractivaTheme

@Composable
fun RegisterScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by loginViewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    //Escuchar eventos de registro
    LaunchedEffect(Unit) {
        loginViewModel.screenEvents.collect { event ->
            when (event) {
                is LoginEvent.RegisterSuccess -> {
                    Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                    onRegisterSuccess()
                }

                is LoginEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is LoginEvent.LoginSuccess -> {
                    /* No se usa en esta pantalla */
                }
            }
        }
    }

    RegisterScreenContent(
        uiState = uiState,
        onRegister = { email, password, confirmPassword ->

            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                loginViewModel.createUser(email, password, confirmPassword)
            }
        },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
private fun RegisterScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginState,
    onRegister: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            //Encabezado
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Guía Museo Angostura",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { heading() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo_museo),
                    contentDescription = "Logo del Museo Angostura",
                    modifier = Modifier.height(120.dp)
                )
            }

            //Formulario inferior
            Surface(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(16.dp))

                    //Email
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Email") },
                        shape = CircleShape,
                        enabled = !uiState.loading,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //Contraseña
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Contraseña") },
                        shape = CircleShape,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !uiState.loading,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //Confirmar contraseña
                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Confirmar Contraseña") },
                        shape = CircleShape,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !uiState.loading,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón de registrarse
                    Button(
                        onClick = { onRegister(email, password, confirmPassword) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.loading,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Registrarse",
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "¿Ya tienes una cuenta?",
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón volver al login
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.loading,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            "Iniciar Sesión",
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        //Cargando
        if (uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Preview(name = "Register Screen", showBackground = true)
@Composable
fun RegisterScreenPreview() {
    GuiaInteractivaTheme {
        RegisterScreenContent(
            uiState = LoginState(),
            onRegister = { _, _, _ -> },
            onNavigateToLogin = {}
        )
    }
}
