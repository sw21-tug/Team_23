package at.tugraz.onpoint

import android.os.Bundle
import android.os.LocaleList
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import at.tugraz.onpoint.database.getDbInstance
import at.tugraz.onpoint.ui.main.AssignmentsTabFragment
import at.tugraz.onpoint.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import java.util.*


class MainTabbedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Instantiation of the singleton DB once globally so it can be
        // available in all other tabs
        getDbInstance(this)
        val languagehandler = LanguageHandler()
        languagehandler.loadLocale(baseContext)
        setContentView(R.layout.activity_maintabbed)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        findViewById<Button>(R.id.switch_language).setOnClickListener { onLanguageSwitch() }
    }

    fun onLanguageSwitch() {
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val currentLocal: String = sharedPref.getString("locale_to_set", "")!!
        val languagehandler = LanguageHandler()
        if(currentLocal == "en") {
            languagehandler.setLanguageToSettings(baseContext, "zh");
        }
        if(currentLocal == "zh") {
            languagehandler.setLanguageToSettings(baseContext, "en")
        }
        recreate()
    }
}
