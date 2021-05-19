package at.tugraz.onpoint

import at.tugraz.onpoint.ui.main.Assignment
import org.junit.Test

import org.junit.Assert.*
import java.net.URL
import java.util.*
import at.tugraz.onpoint.ui.main.AssignmentsTabFragment


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class UnitTests {
    val atf: AssignmentsTabFragment = AssignmentsTabFragment()
    @Test
    fun checkFilter() {

        val test_input: MutableList<Assignment> = ArrayList()
        val a1 : Assignment = Assignment("Title 1", "description", Date(1000000000000),                     arrayListOf<URL>(
            URL("https://www.tugraz.at"),
            URL("https://tc.tugraz.at"),
        ), 1)

        val a2 : Assignment = Assignment("Title 2", "description", Date(1000000000000),                     arrayListOf<URL>(
            URL("https://www.tugraz.at"),
            URL("https://tc.tugraz.at"),
        ), 2)
        test_input.add(a1)
        test_input.add(a2)
        assertEquals(1, atf.filter(test_input, "Title 1")?.size)


    }
}
