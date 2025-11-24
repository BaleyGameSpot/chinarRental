package com.chinarrental.app.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import java.util.*

object LocaleManager {
    private const val PREFS_NAME = "settings"
    private const val LANGUAGE_KEY = "language"

    enum class Language(val code: String, val displayName: String) {
        ENGLISH("en", "English"),
        URDU("ur", "اردو")
    }

    fun setLocale(context: Context, language: Language): Context {
        saveLanguage(context, language)
        return updateResources(context, language.code)
    }

    fun getLocale(context: Context): Language {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val languageCode = prefs.getString(LANGUAGE_KEY, Language.ENGLISH.code) ?: Language.ENGLISH.code
        return Language.values().find { it.code == languageCode } ?: Language.ENGLISH
    }

    private fun saveLanguage(context: Context, language: Language) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(LANGUAGE_KEY, language.code)
        }
    }

    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    fun applyLocale(context: Context): Context {
        val language = getLocale(context)
        return updateResources(context, language.code)
    }
}
