package com.example.guiainteractiva.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    private val _screenState = MutableStateFlow(LoginState())
    val screenState: StateFlow<LoginState> = _screenState.asStateFlow()

    private val _screenEvents = MutableSharedFlow<LoginEvent>()
    val screenEvents: SharedFlow<LoginEvent> = _screenEvents.asSharedFlow()
    // LOGIN
    fun login(email: String, password: String) {
        setLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                setLoading(false)

                if (result.isSuccessful) {
                    emitEvent(LoginEvent.LoginSuccess)
                } else {
                    emitEvent(LoginEvent.Error(firebaseError(result.exception)))
                }
            }
    }
    //REGISTRO
    fun createUser(email: String, password: String, confirmPassword: String) {

        if (password != confirmPassword) {
            emitEvent(LoginEvent.Error("Las contraseñas no coinciden"))
            return
        }

        if (password.length < 8) {
            emitEvent(LoginEvent.Error("La contraseña debe tener al menos 6 caracteres"))
            return
        }

        setLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                setLoading(false)

                if (result.isSuccessful) {
                    emitEvent(LoginEvent.RegisterSuccess)
                } else {
                    emitEvent(LoginEvent.Error(firebaseError(result.exception)))
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }
    //HELPERS
    private fun setLoading(value: Boolean) {
        _screenState.update { it.copy(loading = value) }
    }

    private fun emitEvent(event: LoginEvent) {
        viewModelScope.launch {
            _screenEvents.emit(event)
        }
    }


    // ERRORES FIREBASE
    private fun firebaseError(exception: Exception?): String {
        val code = (exception as? FirebaseAuthException)?.errorCode

        when (code) {
            "ERROR_INVALID_EMAIL" -> return "El correo tiene un formato inválido."
            "ERROR_WRONG_PASSWORD" -> return "La contraseña es incorrecta."
            "ERROR_USER_NOT_FOUND" -> return "No existe un usuario con ese correo."
            "ERROR_EMAIL_ALREADY_IN_USE" -> return "El correo ya está registrado."
            "ERROR_WEAK_PASSWORD" -> return "La contraseña debe tener al menos 6 caracteres."
            "ERROR_TOO_MANY_REQUESTS" -> return "Demasiados intentos. Intenta más tarde."
            "ERROR_INVALID_CREDENTIAL" -> return "El correo o la contraseña son incorrectos."
        }

        val msg = exception?.message.orEmpty()

        return when {
            msg.contains("badly formatted", ignoreCase = true) ->
                "El correo tiene un formato inválido."

            msg.contains("password is invalid", ignoreCase = true) ->
                "La contraseña es incorrecta."

            msg.contains("no user record", ignoreCase = true) ->
                "No existe un usuario con ese correo."

            msg.contains("already in use", ignoreCase = true) ->
                "El correo ya está registrado."

            msg.contains("blocked all requests", ignoreCase = true) ->
                "Demasiados intentos. Inténtalo más tarde."

            else ->
                "Error inesperado. Inténtalo de nuevo."
        }
    }
}
