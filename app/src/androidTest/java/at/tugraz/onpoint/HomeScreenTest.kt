package at.tugraz.onpoint

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest{

    @Test
    fun homescreen_exists(){
        val homeScreen: ActivityScenario<HomeScreenActivity> =
            ActivityScenario.launch(HomeScreenActivity::class.java)
    }
}
