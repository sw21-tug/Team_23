package at.tugraz.onpoint.ui.main

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import at.tugraz.onpoint.R
import at.tugraz.onpoint.database.*

class MainTabFragment : Fragment() {
    val db: OnPointAppDatabase = getDbInstance(null)
    private val assignmentDao: AssignmentDao = db.getAssignmentDao()
    private val todoDao: TodoDao = db.getTodoDao()
    private val assignmentList =  arrayListOf<Assignment>()
    private val todoList =  arrayListOf<Todo>()
    private lateinit var todoLayout: ViewGroup
    private lateinit var recentLayout: ViewGroup

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        // only update data in the lists when the activity itself is already created
        // method itself is always called when we switch to this tab (also for the ToDoFragments)
        if(activity == null) {
            return
        }
        updateLists()
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        //get todos and assignments from db
        todoLayout = root.findViewById(R.id.homescreen_todo_list_id) as ViewGroup
        recentLayout = root.findViewById(R.id.recent_linear_layout) as ViewGroup

        updateLists()

        return root
    }

    /**
     * updates the content of the lists displayed to the user
     */
    private fun updateLists() {
        todoList.clear()
        assignmentList.clear()
        todoList.addAll(todoDao.selectAllNotCompleted())
        assignmentList.addAll(assignmentDao.selectAll())

        for (todoItem in todoList) {
            val lptv = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            lptv.setMargins(10, 50, 10, 0)
            val textView = TextView(activity)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0F)
            textView.layoutParams = lptv
            textView.text = todoItem.title
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_grey))
            textView.setPadding(30, 40, 30, 40)
            textView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.lightGray_main
                )
            )
            textView.gravity = Gravity.CENTER

            // textView.textAlignment =
            todoLayout.addView(textView)


        }

        for (recentItem in assignmentList) {
            // TextView2
            println("In todo loop")
            val lptv = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            lptv.setMargins(10, 50, 10, 0)
            val textView = TextView(activity)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0F)
            textView.layoutParams = lptv
            textView.text = recentItem.title
            //textView.setTextColor(R.color.text_grey)
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_grey))

            textView.setPadding(30, 40, 30, 40)
            textView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.lightGray_main
                )
            )
            textView.gravity = Gravity.CENTER

            recentLayout.addView(textView)


        }
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
        fun newInstance(sectionNumber: Int): MainTabFragment {
            return MainTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
