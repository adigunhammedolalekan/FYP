package app.fyp

import android.content.SharedPreferences
import app.fyp.models.User
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.TimeUnit

const val sessionKey = "x_session_pref_key"
const val accountKey = "x_account_pref_key"
const val sessionCreatedKey = "x_session_created_key"

class Session(private val pref: SharedPreferences) {
    fun store(token: String) {
        pref.edit().putString(sessionKey, token).apply()
    }

    fun getToken(): String = pref.getString(sessionKey, "") ?: ""

    fun putUser(account: User) {
        val s = Gson().toJson(account)
        pref.edit().putString(accountKey, s).apply()
    }

    fun currentUser(): User {
        val s = pref.getString(accountKey, "") ?: ""
        return if (s == "") User()
        else Gson().fromJson(s, User::class.java)
    }

    fun destroy() {
        pref.edit().apply {
            putString(sessionKey, "")
            putString(accountKey, "")
            putLong(sessionCreatedKey, 0)
            apply()
        }
    }
}