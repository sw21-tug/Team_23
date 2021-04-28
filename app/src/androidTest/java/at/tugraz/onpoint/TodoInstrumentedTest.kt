package at.tugraz.onpoint

import android.content.ActivityNotFoundException
import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.Todo
import at.tugraz.onpoint.database.TodoDao
import at.tugraz.onpoint.todolist.TodoFragmentAdd
import at.tugraz.onpoint.todolist.TodoFragmentAddDirections
import at.tugraz.onpoint.todolist.TodoFragmentListView
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TodoInstrumentedTest {
    @Rule
    @JvmField var activityRule: ActivityTestRule<MainTabbedActivity> = ActivityTestRule(MainTabbedActivity::class.java)

    private lateinit var todoDao: TodoDao
    private lateinit var db: OnPointAppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.databaseBuilder(context, OnPointAppDatabase::class.java, "OnPointDB").build()
        todoDao = db.getTodoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun checkActivitySetup() {
        try {
            launchActivity<MainTabbedActivity>()
        } catch (e: ActivityNotFoundException) {
            assert(false)
        }
    }


    /**
     * Checks if the initial view setup for the ToDo-Activity is correct
     */
    @Test
    fun checkInitialButtonSetup() {
        //see https://developer.android.com/guide/navigation/navigation-testing

        val text = "This is a test text"
        val fragmentArgs = bundleOf(text to 0)
        val mockNavController = mock(NavController::class.java)

        val firstScenario = launchFragmentInContainer<TodoFragmentListView>(fragmentArgs)

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

        val text = "This is a test text"
        val fragmentArgs = bundleOf(text to 0)
        val firstScenario = launchFragmentInContainer<TodoFragmentListView>(fragmentArgs)

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

        val text = "This is a test text"
        val fragmentArgs = bundleOf(text to 0)
        val firstScenario = launchFragmentInContainer<TodoFragmentAdd>(fragmentArgs)

        firstScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        onView(withId(R.id.todo_saveButton)).check(matches(isClickable()))
        onView(withId(R.id.todo_saveButton)).perform(click())
        val action = TodoFragmentAddDirections.actionTodoFragmentAddToTodoFragmentListView("")
        verify(mockNavController).navigate(action)
    }

    /**
     * Check if TodoFragmentListView receives input text from TodoFragmentAdd
     */
    @Test
    fun receiveInput() {
        val mockNavController = mock(NavController::class.java)

        val text = "This is a test text"
        val fragmentArgs = bundleOf(text to 0)
        val firstScenario = launchFragmentInContainer<TodoFragmentAdd>()

        firstScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        onView(withId(R.id.todo_InputField)).perform(typeText(text), closeSoftKeyboard())
        onView(withId(R.id.todo_saveButton)).perform(click())

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
    }

    @Test
    fun storeNewTodoAndRetrieveItFromDb() {
        val todo = Todo(100, "Buy some carrots", 123, 1230000, false)
        todoDao.insertOne(todo)
        val listOfTodos = todoDao.loadAll()
        assertThat(listOfTodos.size, equalTo(1))
        assertThat(listOfTodos[0], equalTo(todo))
    }
}