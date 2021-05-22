package at.tugraz.onpoint

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        val languagehandler = LanguageHandler()
        languagehandler.setLanguageToSettings(baseContext, "en")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Redirect to the tabbed activity
        val intent = Intent(this, MainTabbedActivity::class.java)
        startActivity(intent)
    }
}
