package com.smitcoderx.learn.trippin_business.Util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.smitcoderx.learn.trippin_business.Util.Constants.LOGGEDIN
import com.smitcoderx.learn.trippin_business.Util.Constants.TOKEN

class PreferenceManager(context: Context) {

    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean(LOGGEDIN, loggedIn).apply()
    }

    fun getLoggedIn(): Boolean {
        return prefs.getBoolean(LOGGEDIN, false)
    }

    fun setToken(token: String) {
        prefs.edit().putString(TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN, "")
    }

    fun logoutUser() {
        prefs.edit().clear().apply()
    }

}