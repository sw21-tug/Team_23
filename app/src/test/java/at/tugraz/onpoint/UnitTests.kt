package at.tugraz.onpoint

import at.tugraz.onpoint.database.Assignment
import at.tugraz.onpoint.ui.main.AssignmentsTabFragment
import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class UnitTests {
    @Test
    fun checkFilter() {
        val testInput = arrayListOf<Assignment>()
        val a1: Assignment = Assignment(
            "Title 1",
            "description",
            1000000000000,
            "https://www.tugraz.at;https://tc.tugraz.at",
            1
        )
        val a2: Assignment = Assignment(
            "Title 2",
            "description",
            1000000000000,
            "https://www.tugraz.at;https://tc.tugraz.at",
            2
        )
        testInput.add(a1)
        testInput.add(a2)
        val filteredList = AssignmentsTabFragment.filter(testInput, "Title 1")
        assertEquals(1, filteredList.size)
        assertEquals(filteredList[0], a1)
    }
}
