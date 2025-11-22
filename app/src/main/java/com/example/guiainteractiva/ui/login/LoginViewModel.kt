package com.example.guiainteractiva.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

data class LoginState(
    val loading: Boolean = false
)

sealed class LoginEvent {
    data object LoginSuccess : LoginEvent()
    data object RegisterSuccess : LoginEvent()
    data class Error(val message: String) : LoginEvent()
}

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _screenState = MutableStateFlow(LoginState())
    val screenState: StateFlow<LoginState> = _screenState.asStateFlow()

    private val _screenEvents = MutableSharedFlow<LoginEvent>()
    val screenEvents: SharedFlow<LoginEvent> = _screenEvents.asSharedFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            emitEvent(LoginEvent.Error("Por favor, completa todos los campos"))
            return
        }

        viewModelScope.launch {
            setLoading(true)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                emitEvent(LoginEvent.LoginSuccess)
            } catch (e: Exception) {
                emitEvent(LoginEvent.Error(firebaseError(e)))
            } finally {
                setLoading(false)
            }
        }
    }

    fun createUser(
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        employeeCodeInput: String? = null
    ) {
        // Validaciones iniciales
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            emitEvent(LoginEvent.Error("Por favor, completa todos los campos"))
            return
        }
        if (password != confirmPassword) {
            emitEvent(LoginEvent.Error("Las contraseñas no coinciden"))
            return
        }
        if (password.length < 8) {
            emitEvent(LoginEvent.Error("La contraseña debe tener al menos 8 caracteres"))
            return
        }

        viewModelScope.launch {
            setLoading(true)
            try {
                // Si es admin, valida el código primero
                if (role == "admin") {
                    val isCodeValid = validateEmployeeCode(employeeCodeInput)
                    if (!isCodeValid) {
                        throw Exception("Código de empleado incorrecto")
                    }
                }

                // 1. Crear usuario en Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("Error inesperado al crear usuario")

                // 2. Guardar datos adicionales en Firestore
                val userProfile = hashMapOf("email" to email, "role" to role)
                db.collection("users").document(user.uid).set(userProfile).await()

                // 3. Emitir evento de éxito
                emitEvent(LoginEvent.RegisterSuccess)

            } catch (e: Exception) {
                emitEvent(LoginEvent.Error(firebaseError(e)))
            } finally {
                setLoading(false)
            }
        }
    }

    private suspend fun validateEmployeeCode(inputCode: String?): Boolean {
        if (inputCode.isNullOrBlank()) {
            return false
        }
        return try {
            val document = db.collection("Config").document("access").get().await()
            val realCode = document.getString("EmployeeCode")
            realCode == inputCode
        } catch (e: Exception) {
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun setLoading(value: Boolean) {
        _screenState.update { it.copy(loading = value) }
    }

    private fun emitEvent(event: LoginEvent) {
        viewModelScope.launch {
            _screenEvents.emit(event)
        }
    }

    private fun firebaseError(exception: Exception?): String {
        // Primero, maneja el mensaje de excepciones personalizadas
        if (exception?.message == "Código de empleado incorrecto") {
            return exception.message!!
        }

        val code = (exception as? FirebaseAuthException)?.errorCode
        return when (code) {
            "ERROR_INVALID_EMAIL" -> "El correo tiene un formato inválido."
            "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta."
            "ERROR_USER_NOT_FOUND" -> "No existe un usuario con ese correo."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "El correo ya está registrado."
            "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 8 caracteres."
            "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Intenta más tarde."
            "ERROR_INVALID_CREDENTIAL" -> "El correo o la contraseña son incorrectos."
            else -> exception?.message ?: "Error inesperado. Inténtalo de nuevo."
        }
    }
}
