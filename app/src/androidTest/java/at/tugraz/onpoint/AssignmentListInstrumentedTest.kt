package at.tugraz.onpoint

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class AssignmentsListInstrumentedTest {
    @get:Rule
     var activityRule: ActivityScenarioRule<MainTabbedActivity> =
        ActivityScenarioRule(MainTabbedActivity::class.java)

    @Test
    fun activityHasTabList() {
        launchActivity<MainTabbedActivity>()
        onView(withId(R.id.tabs)).check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun tabsAreClickable() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Main")).perform(ViewActions.click())
        onView(withText("Todo")).perform(ViewActions.click())
        onView(withText("Assign.")).perform(ViewActions.click())
    }

    @Test
    fun assignmentLayoutVisible() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(ViewActions.click())
        onView(withId(R.id.assignmentsListTitle)).check(matches(isDisplayed()))
    }
}
