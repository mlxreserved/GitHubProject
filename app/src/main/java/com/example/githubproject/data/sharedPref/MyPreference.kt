package com.example.githubproject.data.sharedPref

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPreference @Inject constructor(@ApplicationContext context: Context) {

    val prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun getStoredToken(): String {
        return prefs.getString(SHARED_PREF_TOKEN_KEY, "") ?: ""
    }

    fun setStoredToken(token: String) {
        prefs.edit().putString(SHARED_PREF_TOKEN_KEY, token).apply()
    }

    fun removeStoredToken() {
        prefs.edit().remove(SHARED_PREF_TOKEN_KEY).apply()
    }

    companion object {
        private const val SHARED_PREF_NAME = "shared_preferences"
        private const val SHARED_PREF_TOKEN_KEY = "token"
    }
}