package at.tugraz.onpoint.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import at.tugraz.onpoint.R


/**
 * A placeholder fragment containing a simple view.
 */
class HomeScreenFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("begin of fragment")
        val root = inflater.inflate(R.layout.fragment_home_screen, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)


        val todoLayout = root.findViewById(R.id.todo_linear_layout) as ViewGroup
        val recentLayout = root.findViewById(R.id.recent_linear_layout) as ViewGroup
        val todoList = listOf<String>("todo1", "todo2")
        val recentList = listOf<String>("recent1")
        for(todoItem in todoList){
            // TextView2
            println("In todo loop")
            val textView = TextView(activity)
            textView.setTextSize(1, 20.0F)
            textView.setText(todoItem)
            textView.setTextColor(Color.MAGENTA)
            todoLayout.addView(textView)
        }

        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): HomeScreenFragment {
            return HomeScreenFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
