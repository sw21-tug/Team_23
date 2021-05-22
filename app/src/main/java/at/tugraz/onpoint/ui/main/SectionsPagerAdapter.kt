@file:Suppress("unused")

package at.tugraz.onpoint.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import at.tugraz.onpoint.R

private val TAB_TITLES = arrayOf(
    R.string.tab_title_main,
    R.string.tab_title_todo,
    R.string.tab_title_assignments,
)

const val TAB_INDEX_MAIN = 0
const val TAB_INDEX_TODO = 1
const val TAB_INDEX_ASSIGNMENT = 2

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    // TODO [MG] replace the deprecated FragmentPagerAdapter with the newer thing (no idea what it is)

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MainTabFragment.newInstance(position)
            1 -> TodoTabFragment.newInstance(position)
            2 -> AssignmentsTabFragment.newInstance(position)
            else -> {
                // Selected non-existing tab (impossible branch).
                MainTabFragment.newInstance(0)
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 3
    }
}
