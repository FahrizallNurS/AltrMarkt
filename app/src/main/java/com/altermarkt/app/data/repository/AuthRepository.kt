package com.altermarkt.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.altermarkt.app.domain.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    // Register — buat akun baru
    suspend fun register(
        name     : String,
        email    : String,
        password : String,
        whatsapp : String
    ): Result<Unit> {
        return try {
            // 1. Buat akun di Firebase Auth
            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            // 2. Ambil UID yang dibuat Firebase
            val uid = result.user!!.uid

            // 3. Simpan nama & nomor WA ke Firestore
            val user = hashMapOf(
                "uid"      to uid,
                "name"     to name,
                "email"    to email,
                "whatsapp" to whatsapp,
            )
            db.collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login — masuk dengan akun yang sudah ada
    suspend fun login(
        email    : String,
        password : String
    ): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cek apakah user masih login — untuk cek sesi di MainActivity
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Ambil UID user yang sedang login
    fun currentUid(): String {
        return auth.currentUser?.uid ?: ""
    }

    // Ambil data user yang sedang login dari Firestore
    suspend fun getCurrentUser(): User? {
        return try {
            val uid = currentUid()
            if (uid.isEmpty()) return null

            val doc = db.collection("users")
                .document(uid)
                .get()
                .await()

            User(
                uid      = doc.getString("uid")      ?: "",
                name     = doc.getString("name")     ?: "",
                email    = doc.getString("email")    ?: "",
                whatsapp = doc.getString("whatsapp") ?: "",
            )
        } catch (e: Exception) {
            null
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }
}