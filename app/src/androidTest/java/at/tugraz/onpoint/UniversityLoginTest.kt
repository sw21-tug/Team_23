package at.tugraz.onpoint

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import at.tugraz.onpoint.database.MoodleDao
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.TodoDao
import at.tugraz.onpoint.database.getDbInstance
import at.tugraz.onpoint.todolist.TodoFragmentListView
import at.tugraz.onpoint.ui.main.UniversityLoginFragment
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UniversityLoginTest {
    @Rule
    @JvmField
    var activityRule: ActivityTestRule<MainTabbedActivity> =
        ActivityTestRule(MainTabbedActivity::class.java)

    private lateinit var moodleDao: MoodleDao
    private lateinit var db: OnPointAppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, OnPointAppDatabase::class.java).build()
        moodleDao = db.getMoodleDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @After
    @Throws(IOException::class)
    fun emptyPersistentDb() {
        val persistentDb = getDbInstance(null) // Already created singleton in @Before
        persistentDb.clearAllTables()
    }

    /**
     * Check if university is added after pressing the login button
     */
    @Test
    fun clickLoginButton() {
        //see https://developer.android.com/guide/navigation/navigation-testing
        Espresso.onView(ViewMatchers.withId(R.id.moodle_loginButton)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withId(R.id.moodle_loginButton)).perform(ViewActions.click())
        val fragment = UniversityLoginFragment()
        assert(fragment.universityList.isNotEmpty())
    }
}
