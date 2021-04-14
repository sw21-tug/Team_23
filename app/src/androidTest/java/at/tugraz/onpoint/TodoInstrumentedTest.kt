package at.tugraz.onpoint

import android.content.ActivityNotFoundException
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.tugraz.onpoint.todolist.TodoActivity

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoInstrumentedTest {
    @Test
    fun checkActivitySetup() {
        try {
            launchActivity<TodoActivity>()
        } catch(e: ActivityNotFoundException) {
            assert(false)
        }
    }
}
