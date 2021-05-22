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
            context.resources.configuration.setLocales(localeListToSet)
            context.resources.updateConfiguration(
                context.resources.configuration,
                context.resources.displayMetrics
            )
            setLanguageToSettings(context, localeToSet)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
    }
}
