package com.example.tropivault.data.repository

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tropivault_prefs", Context.MODE_PRIVATE)

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

    var activeUserId: Long
        get() = prefs.getLong(KEY_ACTIVE_USER_ID, -1L)
        set(value) = prefs.edit().putLong(KEY_ACTIVE_USER_ID, value).apply()

    var rememberMe: Boolean
        get() = prefs.getBoolean(KEY_REMEMBER_ME, false)
        set(value) = prefs.edit().putBoolean(KEY_REMEMBER_ME, value).apply()

    fun clearSession() {
        prefs.edit().remove(KEY_ACTIVE_USER_ID).apply()
    }

    companion object {
        private const val KEY_ONBOARDING_DONE = "key_onboarding_done"
        private const val KEY_ACTIVE_USER_ID = "key_active_user_id"
        private const val KEY_REMEMBER_ME = "key_remember_me"
    }
}
