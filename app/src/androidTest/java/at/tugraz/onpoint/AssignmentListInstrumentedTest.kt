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
import at.tugraz.onpoint.database.Assignment
import at.tugraz.onpoint.database.AssignmentDao
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.getDbInstance
import at.tugraz.onpoint.moodle.API
import at.tugraz.onpoint.moodle.LoginSuccessData
import junit.framework.AssertionFailedError
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.startsWith
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

    private fun waitForViewToBeVisible(
        id: Int,
        maxTries: Int = 100,
        waitBetweenTriesMillis: Int = 100,
    ) {
        for (i in 0..maxTries) {
            try {
                onView(withId(id)).check(matches(isDisplayed()))
                return // View is displayed
            } catch (e: AssertionFailedError) {
                // View is NOT displayed. Wait instead
                Thread.sleep(waitBetweenTriesMillis.toLong())
            }
        }
        throw AssertionFailedError("View with $id not found after wait")
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
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
    }

    @Test
    fun checkForContentInAssignmentList() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList))
            .check(matches(hasDescendant(withText("Assignment 1"))))
    }

    @Test
    fun checkForDetailsInAssignmentListEntry() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
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
    fun mockNotificationTappingOpensAppInAssignmentTab() {
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
        onView(withId(R.id.assignment_sync_assignments)).check(matches(isDisplayed()))
    }

    @Test
    fun checkDeadlinePickerAppearsWhenClickingOnSetReminder() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
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
            .check(matches(isClickable()))
    }

    @Test
    fun storeNewAssignmentAndRetrieveItFromDb() {
        val links = arrayListOf(URL("https://tc.tugraz.at"), URL("https://www.tugraz.at"))
        val deadline = Date()
        val uid = assignmentDao.insertOneFromMoodle("my title", "my description", deadline, links)
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
        assignmentDao.insertOneFromMoodle(
            "my title1",
            "my description1",
            deadlineLate,
            links
        )
        assignmentDao.insertOneFromMoodle(
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
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
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
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
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
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
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
    }

    @Test
    fun getAssignmentFiles() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
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
    fun syncAssignmentsDoesNotDoubleInsertIntoList() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList)).check(RecyclerViewItemCounter())
        val itemsInListAfterFirstSync = RecyclerViewItemCounter.lastCount
        // Sync again: the recyclerview length should not change: previously-contained
        // assignments should still be there
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList)).check(RecyclerViewItemCounter())
        val itemsInListAfterSecondSync = RecyclerViewItemCounter.lastCount
        assert(itemsInListAfterFirstSync == itemsInListAfterSecondSync)
        // Note: for our dummy Moodle, the amount of assignments will never really change.
    }


    @Test
    fun checkAssignmentDescriptionIsNotHTMLCode() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignment_sync_assignments)).perform(click())
        waitForViewToBeVisible(R.id.assignmentsList)
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click()
                )
            )
        onView(withId(R.id.fragment_assignment_details_linearlayout))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(R.id.assignmentsListDetailsDescription))
            .check(matches(not(withSubstring("<p"))))

    }
    // TODO test that custom assignments are not overwritten/removed by the sync

    @Test
    fun checkForDoneButtonInAssignmentListEntry() {
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
        onView(withId(R.id.assignmentsListDetailsCompletedButton))
            .check(matches(isDisplayed()))
            .check(matches(withText("Done")))
    }

    @Test
    fun checkForAssignmentLabels() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.text_assignment_active)).check(matches(withText("Active Assignments")));
        onView(withId(R.id.text_assignment_done)).check(matches(withText("Done Assignments")));
    }

    @Test
    fun checkForDoneListview() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignmentListDone)).check(matches(isDisplayed()))
    }

    @Test
    fun clickDoneButtonDialogCloses() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    3,
                    click()
                )
            )
        onView(withId(R.id.assignmentsListDetailsCompletedButton))
            .perform(click())
        onView(withId(R.id.assignmentListDone))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkIfCustomAddButtonExists() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.custom_assignment_add_button)).check(matches(isClickable()))
    }

    @Test
    fun checkIfCustomDetailsDialogIsShown() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.custom_assignment_add_button)).perform(click())
        onView(withId(R.id.fragment_assignment_custom_details))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkIfCustomDetailsDialogShowsButton() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.custom_assignment_add_button)).perform(click())
        onView(withId(R.id.fragment_assignment_custom_details))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.custom_assignment_deadline_button)).inRoot(isDialog())
            .check(matches(isClickable()))
    }

    @Test
    fun checkIfCustomDetailsDialogShowsInputFields() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.custom_assignment_add_button)).perform(click())
        onView(withId(R.id.fragment_assignment_custom_details))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(R.id.custom_assignment_title_input))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(R.id.custom_assignment_description_input))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkIfCustomDetailsDialogShowsCancelSave() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.custom_assignment_add_button)).perform(click())
        onView(withId(R.id.fragment_assignment_custom_details))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withText(R.string.cancel_button))
            .inRoot(isDialog())
            .check(matches(isClickable()))

        onView(withText(R.string.save_button))
            .inRoot(isDialog())
            .check(matches(isClickable()))
    }

    @Test
    fun clickDoneButtonDoneListContainsAssignment() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignmentsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    3,
                    click()
                )
            )
        onView(withId(R.id.assignmentsListDetailsCompletedButton))
            .perform(click())

        onView(withId(R.id.assignmentListDone))
            .check(matches(hasDescendant(withText("Dummy Assignment 3"))))
    }

    @Test
    fun databaseSelectAllSpecific() {
        val assignmentDao = db.getAssignmentDao()
        assignmentDao.insertOneFromMoodle("Test", "Test", Date(0))
        assignmentDao.insertOneFromMoodle("Test2", "Test2", Date(2))
        assignmentDao.insertOneFromMoodle("Test3", "Test3", Date(1))
        assignmentDao.insertOneCustom("Test4", "Test4", Date(2 ))
        assignmentDao.insertOneCustom("Test5", "Test5", Date(1 ))

        val assignmentActive: List<Assignment> = assignmentDao.selectAllNotCompleted()
        val assignmentDone: List<Assignment> = assignmentDao.selectAllCompleted()
        assert(assignmentActive.size == 3)
        assert(assignmentDone.size == 2)
        // Sorted by deadline
        assert(assignmentActive[0].getDeadlineDate() == Date(0))
        assert(assignmentActive[1].getDeadlineDate() == Date(1))
        assert(assignmentActive[2].getDeadlineDate() == Date(2))
        assert(assignmentDone[0].getDeadlineDate() == Date(2))
        assert(assignmentDone[1].getDeadlineDate() == Date(1))
    }

    @Test
    fun checkDisabledDoneButtonForCompletedAssignment() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Assign.")).perform(click())
        onView(withId(R.id.assignmentListDone))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click()
                )
            )
        onView(withId(R.id.assignmentsListDetailsCompletedButton))
            .check(matches(not(isDisplayed())))
    }
}

class RecyclerViewItemCounter : ViewAssertion {
    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        val recyclerView = view as RecyclerView
        lastCount = recyclerView.adapter!!.itemCount
        assert(true)
    }

    companion object {
        var lastCount: Int = 0
    }
}
