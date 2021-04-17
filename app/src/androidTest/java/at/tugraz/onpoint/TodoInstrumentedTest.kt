package at.tugraz.onpoint

import android.content.ActivityNotFoundException
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import at.tugraz.onpoint.todolist.TodoActivity
import org.hamcrest.CoreMatchers.not
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoInstrumentedTest {
    @Rule
    @JvmField var activityRule: ActivityTestRule<TodoActivity> = ActivityTestRule(TodoActivity::class.java)

    @Test
    fun checkActivitySetup() {
        try {
            launchActivity<TodoActivity>()
        } catch(e: ActivityNotFoundException) {
            assert(false)
        }
    }


    /**
     * Checks if the initial view setup for the ToDo-Activity is correct
     */
    @Test
    fun checkInitialButtonSetup() {
        onView(withId(R.id.todo_addButton)).check(matches(isClickable()))
        onView(withId(R.id.todo_addButton)).check(matches(isDisplayed()))

        onView(withId(R.id.todo_InputField)).check(matches(not(isDisplayed())))

        onView(withId(R.id.todo_saveButton)).check(matches(not(isDisplayed())))
        onView(withId(R.id.todo_saveButton)).check(matches(not(isClickable())))
    }

    /**
     * Check if input fields are correctly displayed after pressing the add button
     */
    @Test
    fun clickAddButton() {
        onView(withId(R.id.todo_addButton)).perform(click())
        onView(withId(R.id.todo_InputField)).check(matches(isDisplayed()))
        onView(withId(R.id.todo_saveButton)).check(matches(isDisplayed()))
        onView(withId(R.id.todo_addButton)).check(matches(not(isDisplayed())))
    }

    /**
     * Check if input fields are correctly displayed after pressing the save button
     */
    @Test
    fun clickSaveButton() {
        onView(withId(R.id.todo_addButton)).perform(click())
        onView(withId(R.id.todo_InputField)).check(matches(isDisplayed()))
        onView(withId(R.id.todo_saveButton)).check(matches(isDisplayed()))
        onView(withId(R.id.todo_addButton)).check(matches(not(isDisplayed())))

        //press save button
        onView(withId(R.id.todo_saveButton)).perform(click())
        checkInitialButtonSetup()
    }
}
