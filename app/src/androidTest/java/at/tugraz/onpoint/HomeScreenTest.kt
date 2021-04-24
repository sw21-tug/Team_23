package at.tugraz.onpoint

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.test.espresso.Espresso.onView
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
class HomeScreenTest{


    //source functional item: https://stackoverflow.com/questions/28742495/testing-background-color-espresso-android
    fun matchesBackgroundColor(expectedResourceId: Int): Matcher<View?>? {
        return object : BoundedMatcher<View?, View>(View::class.java) {
            var actualColor = 0
            var expectedColor = 0
            var message: String? = null
            override fun matchesSafely(item: View): Boolean {
                if (item.getBackground() == null) {
                    message = item.getId().toString() + " does not have a background"
                    return false
                }
                val resources: Resources = item.getContext().getResources()
                expectedColor = ResourcesCompat.getColor(resources, expectedResourceId, null)
                actualColor = try {
                    (item.getBackground() as ColorDrawable).color
                } catch (e: Exception) {
                    (item.getBackground() as GradientDrawable).color!!.defaultColor
                } finally {
                }
                return actualColor == expectedColor
            }

            override fun describeTo(description: Description) {
                if (actualColor != 0) {
                    message = ("Background color did not match: Expected "
                        + String.format("#%06X", 0xFFFFFF and expectedColor) + " was " + String.format("#%06X", 0xFFFFFF and actualColor))
                }
                description.appendText(message)
            }
        }
    }
/*
    fun withTextColor(color: Int): Matcher<View?>? {
        Checks.checkNotNull(color)
        return object : BoundedMatcher<View?, EditText>(EditText::class.java) {
            override fun matchesSafely(warning: EditText): Boolean {
                return color == warning.currentTextColor
            }

            override fun describeTo(description: Description) {
                description.appendText("with text color: ")
            }
        }
    }*/

    //source end

    @Rule
    @JvmField var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun homescreen_exists(){
        onView(withId(R.id.frameLayout)).check((matches(isDisplayed())))
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
        onView(withId(R.id.todo_linear_layout)).check(matches(matchesBackgroundColor(R.color.darkGray_main)))
    }

    @Test
    fun item_design(){

        onView(withId(R.id.homescreen_recent_heading_id)).check(matches(hasTextColor(R.color.black)))
        onView(withId(R.id.homescreen_todo_heading_id)).check(matches(hasTextColor(R.color.black)))

    }
}
