package com.example.guiainteractiva.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guiainteractiva.ui.theme.gradientColors

// Pantalla de registro de usuario
@Composable
fun RegisterScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by loginViewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Efecto para manejar los eventos de la pantalla
    LaunchedEffect(Unit) {
        loginViewModel.screenEvents.collect { event ->
            when (event) {
                is LoginEvent.RegisterSuccess -> {
                    showToast(context, "¡Registro exitoso!")
                    onRegisterSuccess()
                }
                is LoginEvent.Error -> showToast(context, event.message)
                else -> {}
            }
        }
    }

    // Contenido de la pantalla de registro
    RegisterScreenContent(
        uiState = uiState,
        onRegister = { email, password, confirmPassword, role, employeeCode ->
            loginViewModel.createUser(email, password, confirmPassword, role, employeeCode)
        },
        onNavigateToLogin = onNavigateToLogin
    )
}

// Contenido principal de la pantalla de registro
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

    // Fondo con gradiente o color sólido
    val backgroundModifier = MaterialTheme.gradientColors?.let {
        Modifier.background(
            Brush.verticalGradient(
                colors = listOf(it.top, it.center, it.bottom)
            )
        )
    } ?: Modifier.background(MaterialTheme.colorScheme.background)

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(backgroundModifier),
        contentAlignment = Alignment.Center
    ) {

        // Tarjeta de registro
        Card(
            modifier = Modifier.fillMaxWidth(0.90f),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.92f)
            )
        ) {

            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Header()

                Spacer(modifier = Modifier.height(32.dp))

                // Estilo de los campos de texto
                val textFieldColors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF2F2F2),
                    unfocusedContainerColor = Color(0xFFF2F2F2),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                )

                // Campo de texto para el email
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("User") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors,
                    enabled = !uiState.loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de texto para la contraseña
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors,
                    enabled = !uiState.loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de texto para confirmar la contraseña
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors,
                    enabled = !uiState.loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Menú desplegable para seleccionar el rol
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded && !uiState.loading }
                ) {

                    TextField(
                        value = selectedRole,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = textFieldColors,
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

                // Campo para el código de empleado
                if (selectedRole == "Empleado") {
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = employeeCode,
                        onValueChange = { employeeCode = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Código de empleado") },
                        shape = RoundedCornerShape(14.dp),
                        colors = textFieldColors,
                        enabled = !uiState.loading
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Botón de registro
                Button(
                    onClick = {
                        val finalRole =
                            if (selectedRole == "Empleado") "admin" else "user"
                        val code =
                            if (selectedRole == "Empleado") employeeCode else null

                        onRegister(
                            email,
                            password,
                            confirmPassword,
                            finalRole,
                            code
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !uiState.loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Registrarse",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Botón para volver a la pantalla de inicio de sesión
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !uiState.loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Indicador de carga
        if (uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}
