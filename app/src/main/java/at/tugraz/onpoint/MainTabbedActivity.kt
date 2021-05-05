package at.tugraz.onpoint

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintSet
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import at.tugraz.onpoint.database.getDbInstance
import at.tugraz.onpoint.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout

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

        ////////////////////////////////////////////////////
        setSupportActionBar(findViewById(R.id.toolbar))
        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        val drawerToggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ///////////////////////////////////////////////////
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
