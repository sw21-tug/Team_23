package at.tugraz.onpoint

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HomeScreenTest{

    @Rule
    @JvmField var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun homescreen_exists(){
        onView(withId(R.id.mainFragment)).check((matches(isDisplayed())))
    }

    @Test
    fun todo_exists(){
        onView(withId(R.id.homescreen_todo_id)).check(matches(isDisplayed()))
    }

    @Test
    fun recent_exists(){
        onView(withId(R.id.homescreen_recent_id)).check(matches(isDisplayed()))
    }

    @Test
    fun todo_list_exists(){
        onView(withId(R.id.homescreen_todo_list_id)).check(matches(isDisplayed()))
    }


    @Test
    fun background_color(){
        onView(withId(R.id.mainScreenLayout)).check(matches(hasBackground(R.color.light-grey)))
    }


}
