package at.tugraz.onpoint

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import at.tugraz.onpoint.database.MoodleDao
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.getDbInstance
import at.tugraz.onpoint.ui.main.UniversityLoginFragment
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
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


    @Test
    fun addNewMoodleAndCheckValues() {
        assert(moodleDao.selectAll().isEmpty())
        moodleDao.insertOne("TuGraz", "user", "pw", "https://moodle.api.tugraz.at")
        assert(moodleDao.selectAll().isNotEmpty())
    }

    /**
     * Check if university is added after pressing the login button
     */
    @Test
    fun verifyUniversityAdd() {
        val fragment = UniversityLoginFragment()
        assert(fragment.moodleDao.selectAll().isEmpty())
        fragment.addUniversity("TuGraz", "User", "Password", "https://moodle.api.tugraz.at")
        assert(fragment.moodleDao.selectAll().isNotEmpty())
    }

    @Test
    fun verifyUI() {
        val appCompatImageButton = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withContentDescription("O"),
                childAtPosition(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.toolbar),
                        childAtPosition(
                            ViewMatchers.withClassName(Matchers.`is`("android.widget.LinearLayout")),
                            0
                        )
                    ),
                    1
                ),
                ViewMatchers.isDisplayed()
            )
        )
        appCompatImageButton.perform(ViewActions.click())

        val navigationMenuItemView = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.add_university_drawable_bar),
                childAtPosition(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.design_navigation_view),
                        childAtPosition(
                            ViewMatchers.withId(R.id.navigation_view),
                            0
                        )
                    ),
                    1
                ),
                ViewMatchers.isDisplayed()
            )
        )
        navigationMenuItemView.perform(ViewActions.click())

        val appCompatEditText = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.university_login_name),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(android.R.id.custom),
                        0
                    ),
                    8
                ),
                ViewMatchers.isDisplayed()
            )
        )
        appCompatEditText.perform(
            ViewActions.replaceText("Universität Graz"),
            ViewActions.closeSoftKeyboard()
        )

        val appCompatEditText2 = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.university_login_api),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(android.R.id.custom),
                        0
                    ),
                    1
                ),
                ViewMatchers.isDisplayed()
            )
        )
        appCompatEditText2.perform(
            ViewActions.replaceText("https://moodle.unigraz.at"),
            ViewActions.closeSoftKeyboard()
        )

        val appCompatEditText3 = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.university_login_username),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(android.R.id.custom),
                        0
                    ),
                    2
                ),
                ViewMatchers.isDisplayed()
            )
        )
        appCompatEditText3.perform(
            ViewActions.replaceText("studentId"),
            ViewActions.closeSoftKeyboard()
        )

        val appCompatEditText4 = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.university_login_password),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(android.R.id.custom),
                        0
                    ),
                    3
                ),
                ViewMatchers.isDisplayed()
            )
        )
        appCompatEditText4.perform(
            ViewActions.replaceText("studentPassword"),
            ViewActions.closeSoftKeyboard()
        )

        val materialButton = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.moodle_loginButton), ViewMatchers.withText("Add"),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(android.R.id.custom),
                        0
                    ),
                    0
                ),
                ViewMatchers.isDisplayed()
            )
        )
        materialButton.perform(ViewActions.click())

    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                    && view == parent.getChildAt(position)
            }
        }
    }

    @Test
    fun checkAssignmentsInList() {
        launchActivity<MainTabbedActivity>()
        Espresso.onView(ViewMatchers.withText("Assign.")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.assignment_sync_assignments))
            .perform(ViewActions.click()).check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }
}
