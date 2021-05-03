package at.tugraz.onpoint

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
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
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import java.io.IOException
import java.util.*
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

    @Before
    fun selectTodoTab() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Todo")).perform(ViewActions.click()) // Select To-do tab
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
        onView(withId(R.id.todo_addButton)).check(matches(isClickable()))
    }

    /**
     * Check if input fields are correctly displayed after pressing the add button
     */
    @Test
    fun clickAddButton() {
        //see https://developer.android.com/guide/navigation/navigation-testing
        onView(withId(R.id.todo_addButton)).check(matches(isClickable()))
        onView(withId(R.id.todo_addButton)).perform(click())
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
        onView(withId(R.id.todo_listview_active)).check(matches(isDisplayed()))
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

        onView(withId(R.id.todo_listview_active)).check(matches(isDisplayed()))
    }

    /**
     * Check if TodoFragmentListView displays its elements
     */
    @Test
    fun displayTodoElement() {
        val mockNavController = mock(NavController::class.java)

        val text = "This is a test text"
        val fragmentArgs = bundleOf(text to 0)
        val secondScenario = launchFragmentInContainer<TodoFragmentListView>(fragmentArgs)
        secondScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        onView(withId(R.id.todo_listview_active)).check(matches(isDisplayed()))
        onView(withId(R.id.todo_listview_done)).check(matches(isDisplayed()))
    }


    @Test
    fun checkButtonDone() {
        val text = "This is a test text"

        val fragment = TodoFragmentListView()
        fragment.addItemToTodoList(text)

        assert(fragment.todoList.isNotEmpty())
    }

    @Test
    fun checkMoveElementToDone() {
        val text = "This is a test text"

        val fragment = TodoFragmentListView()
        val todo = fragment.addItemToTodoList(text)
        fragment.moveElementToDone(todo)

        assert(fragment.todoList.isEmpty())
        assert(fragment.todoListDone.isNotEmpty())
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
        val timestamp = Date();
        val uid = todoDao.insertNew("Buy some carrots")
        var todo = todoDao.selectOne(uid)
        assert(todo.creationDateTime().before(Date(timestamp.getTime() + 10000)))
        assert(todo.creationDateTime().after(Date(timestamp.getTime() - 10000)))
        assertThat(todo.expirationDateTime(), equalTo(null)) // When null
        todo.expirationUnixTime = todo.creationUnixTime + 10
        assert(timestamp.before(todo.expirationDateTime())) // When not-null
    }

    @Test
    fun insertObjectsAndCheckPersistency() {
        launchActivity<MainTabbedActivity>()
        onView(withText("Todo")).perform(ViewActions.click()) // Select To-do tab
        // Assumption for the test: the list is empty before the first input
        val text = "This is a test text"
        onData(anything())
            .inAdapterView(withId(R.id.todo_listview_active))
            .atPosition(0)
            .onChildView(withId(R.id.todo_list_active_textview))
            .check(matches(not(withText(text))))
        // Add some text to the list of to-dos
        onView(withId(R.id.todo_addButton)).check(matches(isClickable()))
        onView(withId(R.id.todo_addButton)).perform(click())
        onView(withId(R.id.todo_InputField)).perform(typeText(text), closeSoftKeyboard())
        onView(withId(R.id.todo_saveButton)).perform(click())
        // Check if the to-do entry is there
        onView(withId(R.id.todo_listview_active)).check(matches(isDisplayed()))
        onData(anything())
            .inAdapterView(withId(R.id.todo_listview_active))
            .atPosition(0)
            .onChildView(withId(R.id.todo_list_active_textview))
            .check(matches(withText(text)))
        // Close the app completely and reopen it
        pressBackUnconditionally()
        activityRule.finishActivity()
        activityRule.launchActivity(Intent()) // Restarts at the main activity
        onView(withText("Todo")).perform(ViewActions.click()) // Select To-do tab
        // Check if the to-do entry is still there, thus persistency is working
        onView(withId(R.id.todo_listview_active)).check(matches(isDisplayed()))
        onData(anything())
            .inAdapterView(withId(R.id.todo_listview_active))
            .atPosition(0)
            .onChildView(withId(R.id.todo_list_active_textview))
            .check(matches(withText(text)))
    }
}
