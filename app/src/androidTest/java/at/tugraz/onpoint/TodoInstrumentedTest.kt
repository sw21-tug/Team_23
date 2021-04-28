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
import org.hamcrest.CoreMatchers.startsWith
import org.junit.After
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.IOException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.ArrayList
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TodoInstrumentedTest {
    @Rule
    @JvmField
    var activityRule: ActivityTestRule<MainTabbedActivity> =
        ActivityTestRule(MainTabbedActivity::class.java)

    private lateinit var todoDao: TodoDao
    private lateinit var db: OnPointAppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, OnPointAppDatabase::class.java).build()
        todoDao = db.getTodoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
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
        val listOfTodos = todoDao.selectAll()
        assertThat(listOfTodos.size, equalTo(1))
        assertThat(listOfTodos[0], equalTo(todo))
    }

    @Test
    fun storeTodoWithDefaultValues() {
        val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        todoDao.insertNew("Buy some carrots")
        val listOfTodos = todoDao.selectAll()
        assertThat(listOfTodos.size, equalTo(1))
        assert(listOfTodos[0].uid == 1)
        assertThat(listOfTodos[0].title, equalTo("Buy some carrots"))
        assert(
            listOfTodos[0].creationUnixTime > timestamp - 10
                && listOfTodos[0].creationUnixTime < timestamp + 10
        )
        assert(listOfTodos[0].expirationUnixTime == null)
        assert(!listOfTodos[0].isCompleted)
    }

    @Test
    fun storingNewTodosProvidesTheirUids() {
        val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        val uids = ArrayList<Long>()
        for (i in 0..9) {
            uids.add(todoDao.insertNew("Buy $i carrots"))
        }
        val listOfTodos = todoDao.selectAll()
        assertThat(listOfTodos.size, equalTo(10))
        for (i in 0..9) {
            assert(listOfTodos[i].uid.toLong() == uids[i])
            assertThat(listOfTodos[i].title, equalTo("Buy $i carrots"))
            assert(
                listOfTodos[i].creationUnixTime > timestamp - 10
                    && listOfTodos[i].creationUnixTime < timestamp + 10
            )
            assert(listOfTodos[i].expirationUnixTime == null)
            assert(!listOfTodos[0].isCompleted)
        }
    }

    @Test
    fun todoObjectConvertsUnixTimeToJavaObject() {
        val timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val uid = todoDao.insertNew("Buy some carrots")
        var todo = todoDao.selectOne(uid)
        assertThat(todo.creationLocalDateTime(), equalTo(timestamp))
        assertThat(todo.expirationLocalDateTime(), equalTo(null)) // When null
        todo.expirationUnixTime = todo.creationUnixTime + 10
        assertThat(todo.expirationLocalDateTime(), equalTo(timestamp.plusSeconds(10))) // When not-null
    }
}
