package at.tugraz.onpoint

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        val languagehandler = LanguageHandler()
        languagehandler.setLanguageToSettings(baseContext, "en")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, MainTabbedActivity::class.java)
        startActivity(intent)
    }
}
