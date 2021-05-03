package at.tugraz.onpoint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import at.tugraz.onpoint.database.getDbInstance
import at.tugraz.onpoint.ui.main.AssignmentsTabFragment
import at.tugraz.onpoint.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout


class MainTabbedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Instantiation of the singleton DB once globally so it can be
        // available in all other tabs
        getDbInstance(this)
        setContentView(R.layout.activity_maintabbed)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}
