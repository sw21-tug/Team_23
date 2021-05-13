package at.tugraz.onpoint

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.startsWith
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AssignmentsListInstrumentedTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<MainTabbedActivity> =
        ActivityScenarioRule(MainTabbedActivity::class.java)
    var activityTestRule = ActivityTestRule(MainTabbedActivity::class.java)

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
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    3,
                    click()
                )
            )
        onView(withId(R.id.fragment_assignment_details_linearlayout))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(R.id.assignmentsListDetailsDescription))
            .check(matches(withText("Dummy Description 3")))
            .check(matches(isDisplayed()))
        onView(withId(R.id.assignmentsListDetailsDeadline))
            .check(matches(withText(startsWith("Deadline"))))
            .check(matches(isDisplayed()))
        onView(withId(R.id.assignmentsListDetailsLinks))
            .check(matches(withText(startsWith("http"))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun mockNotificationTappingOpensTheApp() {
        // Mock intent that is fired when tapping on the notification
        // Does exactly the same stuff as the one implemented inside the app.
        // Testing the notification behaviour is out of scope: we assume Android works properly.
        // Espresso additionally does not allow interactions with the notifications, as they
        // are not within the current View that Espresso can interact with.
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(targetContext, MainTabbedActivity::class.java)
        activityTestRule.launchActivity(intent)
        onView(withText("Main")).check(matches(isDisplayed()))
        onView(withText("Todo")).check(matches(isDisplayed()))
        onView(withText("Assign.")).check(matches(isDisplayed()))
    }

    @Test
    fun mockNotificationTappingOpensAppInAssignemntTab() {
        // Mock intent that is fired when tapping on the notification
        // Does exactly the same stuff as the one implemented inside the app.
        // Testing the notification behaviour is out of scope: we assume Android works properly.
        // Espresso additionally does not allow interactions with the notifications, as they
        // are not within the current View that Espresso can interact with.
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(targetContext, MainTabbedActivity::class.java)
        //Value 2 for assignmentTab
        intent.putExtra("tabToOpen", 2);
        activityTestRule.launchActivity(intent)
        onView(withText("Main")).check(matches(isDisplayed()))
        onView(withText("Todo")).check(matches(isDisplayed()))
        onView(withText("Assign.")).check(matches(isDisplayed()))
        onView(withId(R.id.assignmentsList)).check(matches(isDisplayed()))
    }


}
