package at.tugraz.onpoint

import android.app.Activity
import android.content.ActivityNotFoundException
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import at.tugraz.onpoint.todolist.TodoActivity
import at.tugraz.onpoint.todolist.TodoFragmentAdd
import at.tugraz.onpoint.todolist.TodoFragmentListView
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.startsWith
import org.hamcrest.`object`.HasToString.hasToString
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

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
        //see https://developer.android.com/guide/navigation/navigation-testing

        val mockNavController = mock(NavController::class.java)

        val firstScenario = launchFragmentInContainer<TodoFragmentListView>()

        firstScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        onView(withId(R.id.todo_addButton)).check(matches(isClickable()))

    }

    /**
     * Check if input fields are correctly displayed after pressing the add button
     */
    @Test
    fun clickAddButton() {
        //see https://developer.android.com/guide/navigation/navigation-testing

        val mockNavController = mock(NavController::class.java)

        val firstScenario = launchFragmentInContainer<TodoFragmentListView>()

        firstScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        onView(withId(R.id.todo_addButton)).check(matches(isClickable()))
        onView(withId(R.id.todo_addButton)).perform(click())
        verify(mockNavController).navigate(R.id.action_todoFragmentListView_to_todoFragmentAdd)
    }

    /**
     * Check if input fields are correctly displayed after pressing the save button
     */
    @Test
    fun clickSaveButton() {
        val mockNavController = mock(NavController::class.java)

        val firstScenario = launchFragmentInContainer<TodoFragmentAdd>()

        firstScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        onView(withId(R.id.todo_saveButton)).check(matches(isClickable()))
        onView(withId(R.id.todo_saveButton)).perform(click())
        verify(mockNavController).navigate(R.id.action_todoFragmentAdd_to_todoFragmentListView)
    }

    /**
     * Check if TodoFragmentListView receives input text from TodoFragmentAdd
     */
    @Test
    fun receiveInput() {
        val mockNavController = mock(NavController::class.java)

        val firstScenario = launchFragmentInContainer<TodoFragmentAdd>()

        firstScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        val text = "This is a test text"
        onView(withId(R.id.todo_InputField)).perform(typeText(text), closeSoftKeyboard())
        onView(withId(R.id.todo_saveButton)).perform(click())

        val fragmentArgs = bundleOf(text to 0)
        val secondScenario = launchFragmentInContainer<TodoFragmentListView>(fragmentArgs)
        secondScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        onView(withId(R.id.todo_listview)).check(matches(isDisplayed()))
    }

    /**
     * Check if TodoFragmentListView receives input text from TodoFragmentAdd and displays it correctly
     */
    @Test
    fun receiveInputAndDisplay() {
        val mockNavController = mock(NavController::class.java)

        val text = "This is a test text"
        val fragmentArgs = bundleOf(text to 0)
        val secondScenario = launchFragmentInContainer<TodoFragmentListView>(fragmentArgs)
        secondScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        onView(withId(R.id.todo_listview)).check(matches(isDisplayed()))
        onData(hasToString(startsWith(text)))
            .inAdapterView(withId(R.id.todo_listview)).atPosition(0)
            .perform(click());
    }
}
