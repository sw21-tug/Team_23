package at.tugraz.onpoint

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TranslationTest{


    @Rule
    @JvmField var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun tab_language() {
        onView(
            Matchers.allOf(withContentDescription("O"),
                childAtPosition(
                    Matchers.allOf(withId(R.id.toolbar),
                        childAtPosition(
                            withClassName(Matchers.`is`("android.widget.LinearLayout")),
                            0)),
                    1),
                isDisplayed())).perform(click())

        onView(
            Matchers.allOf(withId(R.id.language_toggle),
                childAtPosition(
                    Matchers.allOf(withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.navigation_view),
                            0)),
                    3),
                isDisplayed())).perform(click())
        onView(withId(R.id.homescreen_recent_heading_id)).check(matches(withText("最近的")))
        onView(withId(R.id.homescreen_todo_heading_id)).check(matches(withText("去做")))
        onView(
            Matchers.allOf(withId(R.id.language_toggle),
                childAtPosition(
                    Matchers.allOf(withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.navigation_view),
                            0)),
                    3),
                isDisplayed())).perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int): Matcher<View> {

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
}
