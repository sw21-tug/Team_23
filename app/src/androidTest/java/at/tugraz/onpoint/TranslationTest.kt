package at.tugraz.onpoint

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.util.Checks
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
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
        onView(withId(R.id.tabMain)).check(matches(withText("")))
        onView(withId(R.id.tabAssignments)).check(matches(withText("")))
        onView(withId(R.id.tabTodoList)).check(matches(withText("")))
        onView(withId(R.id.switch_language)).perform(click())
    }
}
