package at.tugraz.onpoint

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
    fun checkForAssignmentListExistence() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(ViewActions.click())
        onView(withId(R.id.assignmentsList)).check(matches(isDisplayed()))
    }

    @Test
    fun checkForContentInAssignmentList() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(ViewActions.click())
        onView(withId(R.id.assignmentsList))
            .check(matches(hasDescendant(withText("Dummy Assignment 5"))));
    }

    @Test
    fun checkForDetailsInAssignmentListEntry() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(ViewActions.click())
        // Click item at position 3
        onView(withId(R.id.assignmentsList))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(3, click()))
        // TODO inspect content of the details displayed for a list element at position X
        assert(false)
    }
}
