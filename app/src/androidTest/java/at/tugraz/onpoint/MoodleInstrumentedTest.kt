package at.tugraz.onpoint

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import at.tugraz.onpoint.database.MoodleDao
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.TodoDao
import at.tugraz.onpoint.moodle.API
import com.github.kittinunf.fuel.core.FuelError
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

class MockedAPIInstrumentedTest: API() {
    override fun request(path: String, query_params: Map<String,String>, onSuccess: (data: String) -> Unit, onError: (error: FuelError) -> Unit) {
        if(path == "/webservice/rest/server.php") {
            onSuccess("{\"courses\":[{\"id\":2,\"fullname\":\"SW21 Course\",\"shortname\":\"SW21C\",\"timemodified\":1620767043,\"assignments\":[{\"id\":1,\"cmid\":2,\"course\":2,\"name\":\"Assignment 1\",\"nosubmissions\":0,\"submissiondrafts\":0,\"sendnotifications\":0,\"sendlatenotifications\":0,\"sendstudentnotifications\":1,\"duedate\":1626562800,\"allowsubmissionsfromdate\":1620687600,\"grade\":100,\"timemodified\":1621408087,\"completionsubmit\":1,\"cutoffdate\":0,\"gradingduedate\":1627167600,\"teamsubmission\":0,\"requireallteammemberssubmit\":0,\"teamsubmissiongroupingid\":0,\"blindmarking\":0,\"hidegrader\":0,\"revealidentities\":0,\"attemptreopenmethod\":\"none\",\"maxattempts\":-1,\"markingworkflow\":0,\"markingallocation\":0,\"requiresubmissionstatement\":0,\"preventsubmissionnotingroup\":0,\"configs\":[{\"plugin\":\"file\",\"subtype\":\"assignsubmission\",\"name\":\"enabled\",\"value\":\"1\"},{\"plugin\":\"file\",\"subtype\":\"assignsubmission\",\"name\":\"maxfilesubmissions\",\"value\":\"20\"},{\"plugin\":\"file\",\"subtype\":\"assignsubmission\",\"name\":\"maxsubmissionsizebytes\",\"value\":\"41943040\"},{\"plugin\":\"file\",\"subtype\":\"assignsubmission\",\"name\":\"filetypeslist\",\"value\":\"\"},{\"plugin\":\"comments\",\"subtype\":\"assignsubmission\",\"name\":\"enabled\",\"value\":\"1\"},{\"plugin\":\"comments\",\"subtype\":\"assignfeedback\",\"name\":\"enabled\",\"value\":\"1\"},{\"plugin\":\"comments\",\"subtype\":\"assignfeedback\",\"name\":\"commentinline\",\"value\":\"0\"}],\"intro\":\"<p dir=\\\"ltr\\\" style=\\\"text-align: left;\\\">Assignment 1 - Description<\\/p><ul><li>123<\\/li><li>456<br><\\/li><\\/ul>\",\"introformat\":1,\"introfiles\":[],\"introattachments\":[]}]}],\"warnings\":[]}")
        }
    }
}

@RunWith(AndroidJUnit4::class)
class MoodleInstrumentedTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<MainTabbedActivity> =
        ActivityScenarioRule(MainTabbedActivity::class.java)

    private lateinit var moodleDao: MoodleDao
    private lateinit var db: OnPointAppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, OnPointAppDatabase::class.java).build()
        moodleDao = db.getMoodleDao()
    }

    @Test
    fun checkAssignmentsInList() {
        launchActivity<MainTabbedActivity>()
        moodleDao.insertOne("TUGRAZ", "test", "CGR9*bcLuUtQye*2ZmMx5rv@CTitG6", "https://moodle.divora.at")
        onView(withText("Assign.")).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.assignment_sync_assignments)).perform(click())
        sleep(4000)
        onView(withText("Assignment 1")).check(matches(isDisplayed()))
    }
}
