package at.tugraz.onpoint

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import at.tugraz.onpoint.database.AssignmentDao
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.getDbInstance
import at.tugraz.onpoint.moodle.API
import at.tugraz.onpoint.moodle.AssignmentResponse
import at.tugraz.onpoint.moodle.LoginSuccessData
import at.tugraz.onpoint.ui.main.Assignment
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.net.URL
import java.util.*


@RunWith(AndroidJUnit4::class)
class AssignmentsListInstrumentedTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<MainTabbedActivity> =
        ActivityScenarioRule(MainTabbedActivity::class.java)
    private var activityTestRule = ActivityTestRule(MainTabbedActivity::class.java)

    private lateinit var assignmentDao: AssignmentDao
    private lateinit var db: OnPointAppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, OnPointAppDatabase::class.java).build()
        assignmentDao = db.getAssignmentDao()
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
    fun activityHasTabList() {
        launchActivity<MainTabbedActivity>()
        onView(withId(R.id.tabs)).check(matches(isEnabled()))
    }

    @Test
    fun tabsAreClickable() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Main")).perform(click())
        onView(withText("Todo")).perform(click())
        onView(withText("Assign.")).perform(click())
    }

    @Test
    fun checkForAssignmentListExistence() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignmentsList)).check(matches(isDisplayed()))
    }

    @Test
    fun checkForContentInAssignmentList() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignmentsList))
            .check(matches(hasDescendant(withText("Dummy Assignment 5"))))
    }

    @Test
    fun checkForDetailsInAssignmentListEntry() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
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
        intent.putExtra("tabToOpen", 2)
        activityTestRule.launchActivity(intent)
        onView(withText("Main")).check(matches(isDisplayed()))
        onView(withText("Todo")).check(matches(isDisplayed()))
        onView(withText("Assign.")).check(matches(isDisplayed()))
        onView(withId(R.id.assignmentsList)).check(matches(isDisplayed()))
    }

    @Test
    fun checkDeadlinePickerAppearsWhenClickingOnSetReminder() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        // Click item at position 3
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    3,
                    click()
                )
            )
        // The details view of the assignment dialog appears
        onView(withId(R.id.fragment_assignment_details_linearlayout))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        // There is a button to select the datetime of the reminder
        onView(withText("SET REMINDER"))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(android.R.id.button1)) // OK button, with default Android ID
            .check(matches(isDisplayed()))
            .perform(click())
    }

    @Test
    fun storeNewAssignmentAndRetrieveItFromDb() {
        val links = arrayListOf(URL("https://tc.tugraz.at"), URL("https://www.tugraz.at"))
        val deadline = Date()
        val uid = assignmentDao.insertOne("my title", "my description", deadline, links)
        val assignment: Assignment = assignmentDao.selectOne(uid)
        assert(assignment.uid!!.toLong() == uid)
        assert(assignment.title == "my title")
        assert(assignment.description == "my description")
        assert(assignment.getDeadlineDate().before(Date(deadline.time + 10000)))
        assert(assignment.getDeadlineDate().after(Date(deadline.time - 10000)))
        assert(assignment.getLinksAsUrls()[0] == links[0])
        assert(assignment.getLinksAsUrls()[1] == links[1])
        assert(assignment.moodleId == null)  // No Moodle Identifier in this case
    }

    @Test
    fun retrieveAllAssignmentsFromDb() {
        val links = arrayListOf(URL("https://tc.tugraz.at"), URL("https://www.tugraz.at"))
        val deadlineEarly = Date()
        val deadlineLate = Date(Date().time + 1000)
        val moodleId = 1234
        assignmentDao.insertOne(
            "my title1",
            "my description1",
            deadlineLate,
            links
        )
        assignmentDao.insertOne(
            "my title2",
            "my description2",
            deadlineEarly,
            links
        )
        val assignmentsList: List<Assignment> = assignmentDao.selectAll()
        assert(assignmentsList.size == 2)
        assert(assignmentsList[0].title == "my title2")
        assert(assignmentsList[0].description == "my description2")
        // Data is sorted by deadline (closest deadline first)
        assert(assignmentsList[0].getDeadlineDate().before(Date(deadlineEarly.time + 10000)))
        assert(assignmentsList[0].getDeadlineDate().after(Date(deadlineEarly.time - 10000)))
        assert(assignmentsList[0].getLinksAsUrls()[0] == links[0])
        assert(assignmentsList[0].getLinksAsUrls()[1] == links[1])
        assert(assignmentsList[1].title == "my title1")
        assert(assignmentsList[1].description == "my description1")
        assert(assignmentsList[1].getDeadlineDate().before(Date(deadlineLate.time + 10000)))
        assert(assignmentsList[1].getDeadlineDate().after(Date(deadlineLate.time - 10000)))
        assert(assignmentsList[1].getLinksAsUrls()[0] == links[0])
        assert(assignmentsList[1].getLinksAsUrls()[1] == links[1])
        assert(assignmentsList[0].uid != assignmentsList[1].uid)
    }

    @Test
    fun checkForAddToCalendarButton() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
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
        onView(withId(R.id.addMeToCalendar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkClickForAddToCalendarButton() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
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
        onView(withId(R.id.addMeToCalendar))
            .perform(click())
    }

    @Test
    fun checkIfSearchbarIsDisplayed() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignment_searchview)).check(matches(isDisplayed()))
    }

    @Test
    fun assignmentListDetailsHaveClickableLinks() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
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
        onView(withId(R.id.assignmentsListDetailsLinks))
            .check(matches(withText(startsWith("http"))))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .perform(click())
    }

    @Test
    fun getAssignmentFiles() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        Thread.sleep(5000) /// Sleeping to wait for request through moddle API to
        // be recieved and the list updated
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )
        onView(withId(R.id.fragment_assignment_details_linearlayout))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(R.id.assignmentsListDetailsDescription))
            .check(matches(withSubstring("Assignment 1 - Description")))
            .check(matches(isDisplayed()))
        onView(withId(R.id.assignmentsListDetailsDeadline))
            .check(matches(withText(startsWith("Deadline"))))
            .check(matches(isDisplayed()))
        onView(withId(R.id.assignmentsListDetailsLinks))
            .check(matches(withText(startsWith("http"))))
            .check(matches(withSubstring(".pdf?")))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .perform(click())
    }

    @Test
    fun verifyLoginToOnlineMoodle() {
        val moodleApi = API()
        moodleApi.setAuthority("moodle.divora.at")
        moodleApi.login("test", "onpoint!T23") { response: Any ->
            assert(response is LoginSuccessData)
        }
    }

    @Test
    fun getAssignmentsFromOnlineMoodle() {
        val moodleApi = API()
        moodleApi.setAuthority("moodle.divora.at")
        moodleApi.login("test", "onpoint!T23") { response: Any ->
            assert(response is LoginSuccessData)
        }
        moodleApi.getAssignments { response: Any ->
            assert(response is AssignmentResponse)
        }
    }

    @Test
    fun syncAssignmentsDoesNotDoubleInsertIntoList() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        Thread.sleep(2000) /// Sleeping to wait for request through Moodle API to
        // be received and the list updated
        onView(withId(R.id.assignmentsList)).check(RecyclerViewItemCounter())
        val itemsInListAfterFirstSync = RecyclerViewItemCounter.lastCount
        // Sync again: the recyclerview length should not change: previously-contained
        // assignments should still be there
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        Thread.sleep(2000) /// Sleeping to wait for request through Moodle API to
        // be recieved and the list updated
        onView(withId(R.id.assignmentsList)).check(RecyclerViewItemCounter())
        val itemsInListAfterSecondSync = RecyclerViewItemCounter.lastCount
        assert(itemsInListAfterFirstSync == itemsInListAfterSecondSync)
        // Note: for our dummy Moodle, the amount of assignments will never really change.
    }
    // TODO test that custom assignments are not overwritten/removed by the sync
}


class RecyclerViewItemCounter : ViewAssertion {
    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        val recyclerView = view as RecyclerView
        lastCount = recyclerView.adapter!!.itemCount
        assert(true)
    }

    companion object{
        var lastCount: Int = 0
    }
}
