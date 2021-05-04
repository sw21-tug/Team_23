package at.tugraz.onpoint

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TranslationTest{


    @Rule
    @JvmField var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun change_language_exists(){
        onView(withId(R.id.switch_language)).check(matches(isDisplayed()))
    }

    @Test
    fun flag_switched(){
        onView(withId(R.id.switch_language)).perform(click()).check(matches(withText("English")))
        onView(withId(R.id.switch_language)).perform(click()).check(matches(withText("Chinese")))
    }

    @Test
    fun tab_language() {
        onView(withId(R.id.switch_language)).perform(click())
        onView(withId(R.id.homescreen_recent_heading_id)).check(matches(withText("最近的")))
        onView(withId(R.id.homescreen_todo_heading_id)).check(matches(withText("去做")))
        onView(withId(R.id.switch_language)).perform(click())
    }
}
