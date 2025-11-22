package com.example.guiainteractiva.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guiainteractiva.ui.theme.GuiaInteractivaTheme

@Composable
fun RegisterScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by loginViewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        loginViewModel.screenEvents.collect { event ->
            when (event) {
                is LoginEvent.RegisterSuccess -> {
                    showToast(context, "¡Registro exitoso!")
                    onRegisterSuccess()
                }

                is LoginEvent.Error -> {
                    showToast(context, event.message)
                }

                else -> {}
            }
        }
    }

    RegisterScreenContent(
        uiState = uiState,
        onRegister = { email, password, confirmPassword, role, employeeCode ->
            loginViewModel.createUser(email, password, confirmPassword, role, employeeCode)
        },
        onNavigateToLogin = onNavigateToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginState,
    onRegister: (String, String, String, String, String?) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("Visitante", "Empleado")
    var selectedRole by remember { mutableStateOf(roles[0]) }
    var employeeCode by remember { mutableStateOf("") }

    val fieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
        focusedContainerColor = MaterialTheme.colorScheme.tertiary,
        cursorColor = MaterialTheme.colorScheme.onSurface
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            //Header reutilizable
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Header()
            }

            //Formulario Inferior
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
                        colors = fieldColors,
                        enabled = !uiState.loading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contraseña
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = CircleShape,
                        colors = fieldColors,
                        enabled = !uiState.loading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirmar Contraseña
                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Confirmar contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = CircleShape,
                        colors = fieldColors,
                        enabled = !uiState.loading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de Rol
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !uiState.loading && !expanded }
                    ) {
                        TextField(
                            value = selectedRole,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = CircleShape,
                            colors = fieldColors,
                            enabled = !uiState.loading
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            roles.forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(role) },
                                    onClick = {
                                        selectedRole = role
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Código de Empleado (si es necesario)
                    if (selectedRole == "Empleado") {
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = employeeCode,
                            onValueChange = { employeeCode = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Código de empleado") },
                            shape = CircleShape,
                            colors = fieldColors,
                            enabled = !uiState.loading
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón Registrarse
                    Button(
                        onClick = {
                            val finalRole = if (selectedRole == "Empleado") "admin" else "user"
                            val code = if (selectedRole == "Empleado") employeeCode else null
                            onRegister(email, password, confirmPassword, finalRole, code)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CircleShape,
                        enabled = !uiState.loading
                    ) {
                        Text("Registrarse")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("¿Ya tienes una cuenta?")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Iniciar Sesión
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CircleShape,
                        enabled = !uiState.loading
                    ) {
                        Text("Iniciar sesión")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    GuiaInteractivaTheme {
        RegisterScreenContent(uiState = LoginState(), onRegister = { _, _, _, _, _ -> }, onNavigateToLogin = {})
    }
}
