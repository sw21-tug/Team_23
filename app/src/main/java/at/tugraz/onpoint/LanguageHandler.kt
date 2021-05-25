package at.tugraz.onpoint

import android.content.Context
import android.os.LocaleList
import java.util.*

/**
 * Source: https://lokalise.com/blog/android-app-localization/
 */
class LanguageHandler {
    fun setLanguageToSettings(context: Context, locale: String) {
        val sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        sharedPref.putString("locale_to_set", locale)
        sharedPref.apply()
    }

    fun loadLocale(context: Context) {
        val sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val localeToSet: String = sharedPref.getString("locale_to_set", "")!!
        setLocale(context, localeToSet)
    }

    fun setLocale(context: Context, localeToSet: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val localeListToSet =
                LocaleList(Locale(localeToSet))
            LocaleList.setDefault(localeListToSet)
            val overrideConfiguration = context.resources.configuration
            overrideConfiguration.setLocales(localeListToSet)
            val updatedContext: Context = context.createConfigurationContext(overrideConfiguration)
            setLanguageToSettings(updatedContext, localeToSet)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
    }
}
