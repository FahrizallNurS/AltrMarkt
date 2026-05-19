package com.altermarkt.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altermarkt.app.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    // State untuk menyimpan status loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    //message error
    private val _errorMsg = MutableStateFlow("")
    val errorMsg: StateFlow<String> = _errorMsg

    //navigasi jika auth berhasil
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    //login
    fun login(email: String, password: String) {

        //validasi input
        if (email.isBlank() || password.isBlank()) {
            _errorMsg.value = "email dan password tidak boleh kosong"
        }

        viewModelScope.launch {

            _isLoading.value = true
            _errorMsg.value = ""

            val result = repo.login(email, password)

            result
                .onSuccess {
                    _isSuccess.value = true
                }
                .onFailure { e ->
                    _errorMsg.value = when (e) {
                        is FirebaseAuthInvalidCredentialsException -> "email atau password salah"
                        else -> "login gagal"
                    }
                }

            _isLoading.value = false
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        whatsapp: String
    ) {
        //validasi input
        if (name.isBlank()) {
            _errorMsg.value = "nama tidak boleh kosong"
            return
        }
        if (email.isBlank()) {
            _errorMsg.value = "email tidak boleh kosong"
            return
        }
        if (password.length < 6) {
            _errorMsg.value = "password minimal 6 karakter"
            return
        }
        if (whatsapp.isBlank()) {
            _errorMsg.value = "nomor WA tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = ""

            val result = repo.register(name, email, password, whatsapp)
            result
                .onSuccess {

                    _isSuccess.value = true
                }
                .onFailure { e ->
                    _errorMsg.value = when (e) {
                        is FirebaseAuthWeakPasswordException -> "password minimal 6 karakter"
                        is FirebaseAuthUserCollisionException -> "email sudah terdaftar"
                        else -> "register gagal"
                    }
                }
            _isLoading.value = false
        }
    }

    fun logout(){

        repo.logout()
        _isSuccess.value = false
    }

    fun resetState(){
        _isLoading.value = false
        _errorMsg.value = ""
        _isSuccess.value = false
    }
}