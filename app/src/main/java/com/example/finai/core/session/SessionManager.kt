package com.example.finai.core.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
        
        // Tempo de expiração da sessão: 7 dias em milissegundos
        // 7 dias * 24 horas * 60 minutos * 60 segundos * 1000 milissegundos
        private const val SESSION_DURATION = 7L * 24 * 60 * 60 * 1000
    }

    fun saveUserSession(userId: Int) {
        val editor = prefs.edit()
        editor.putInt(KEY_USER_ID, userId)
        editor.putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
        editor.apply()
    }

    fun getUserId(): Int {
        // Verifica validade antes de retornar o ID
        if (!isSessionValid()) {
            clearSession()
            return -1
        }
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getUserId() != -1
    }

    private fun isSessionValid(): Boolean {
        val userId = prefs.getInt(KEY_USER_ID, -1)
        if (userId == -1) return false

        val loginTime = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0)
        val currentTime = System.currentTimeMillis()

        // Se o tempo atual menos o tempo de login for maior que a duração, expirou
        return (currentTime - loginTime) < SESSION_DURATION
    }
}
