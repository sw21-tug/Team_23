package at.tugraz.onpoint

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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

        ////////////////////////////////////////////////////////////////////////////////////////////
        /// source: https://proandroiddev.com/easy-approach-to-navigation-drawer-7fe87d8fd7e7
        setSupportActionBar(findViewById(R.id.toolbar))
        val sidebar = findViewById<DrawerLayout>(R.id.sidebar)
        val sidebarToggle = ActionBarDrawerToggle(this, sidebar, R.string.open, R.string.close)
        sidebar.addDrawerListener(sidebarToggle)
        sidebarToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sidebar = findViewById<DrawerLayout>(R.id.sidebar)
        return when (item.itemId) {
            android.R.id.home -> {
                sidebar.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val sidebar = findViewById<DrawerLayout>(R.id.sidebar)
        if (sidebar.isDrawerOpen(GravityCompat.START)) {
            sidebar.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /// source: https://proandroiddev.com/easy-approach-to-navigation-drawer-7fe87d8fd7e7
    ////////////////////////////////////////////////////////////////////////////////////////////////


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
