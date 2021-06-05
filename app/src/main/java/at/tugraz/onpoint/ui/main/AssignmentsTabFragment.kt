@file:Suppress("unused")

package at.tugraz.onpoint.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.onpoint.R
import at.tugraz.onpoint.database.*
import at.tugraz.onpoint.database.Assignment
import at.tugraz.onpoint.moodle.*
import java.net.URL
import java.util.*


class AssignmentsTabFragment : Fragment() {
    private lateinit var pageViewModel: PageViewModel
    var notCompletedAssignmentsList = arrayListOf<Assignment>()
    private var completedAssignmentsList = arrayListOf<Assignment>()
    private var notCompletedAdapter: AssignmentsAdapter? = null
    private var completedAdapter: AssignmentsAdapter? = null
    val db: OnPointAppDatabase = getDbInstance(null)
    private val moodleDao: MoodleDao = db.getMoodleDao()
    private val assignmentDao: AssignmentDao = db.getAssignmentDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
        // Fill the assignment list retrieved from the persistent database
        notCompletedAssignmentsList.addAll(assignmentDao.selectAllNotCompleted())
        completedAssignmentsList.addAll(assignmentDao.selectAllCompleted())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_assignments, container, false)
        root.findViewById<Button>(R.id.assignment_sync_assignments)
            .setOnClickListener { syncAssignments() }
        if (moodleDao.selectAll().isEmpty()) {
            notifyUser("Automatically added login info to di Vora's Moodle")
            moodleDao.insertOne("diVoraTestMoodle", "test", "onpoint!T23", "moodle.divora.at")
        }

        // Create the the Recyclerview, make it a linear list (not a grid), assign the list of
        // items to it and provide and adapter constructing each element of the list as a TextView
        val assignmentsRecView: RecyclerView = root.findViewById(R.id.assignmentsList)
        assignmentsRecView.layoutManager = LinearLayoutManager(this.context)
        notCompletedAdapter =
            this.context?.let {
                AssignmentsAdapter(
                    notCompletedAssignmentsList,
                    it,
                    parentFragmentManager,
                    this
                )
            }
        assignmentsRecView.adapter = this.notCompletedAdapter

        // Create the Recyclerview for completed assignments, make it a linear list (not a grid), assign the list of
        // items to it and provide and adapter constructing each element of the list as a TextView
        val completedAssignmentsRecView: RecyclerView =
            root.findViewById(R.id.assignmentListCompleted)
        completedAssignmentsRecView.layoutManager = LinearLayoutManager(this.context)
        completedAdapter =
            this.context?.let {
                AssignmentsAdapter(
                    completedAssignmentsList,
                    it,
                    parentFragmentManager,
                    this
                )
            }
        completedAssignmentsRecView.adapter = this.completedAdapter

        val searchView: SearchView = root.findViewById(R.id.assignment_searchview)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    var temp = notCompletedAssignmentsList.toMutableList()
                    notCompletedAssignmentsList.clear()
                    notCompletedAssignmentsList.addAll(
                        filter(
                            temp,
                            newText
                        ) as ArrayList<Assignment>
                    )
                    notCompletedAdapter!!.notifyDataSetChanged()
                    temp = completedAssignmentsList.toMutableList()
                    completedAssignmentsList.clear()
                    completedAssignmentsList.addAll(filter(temp, newText) as ArrayList<Assignment>)
                    completedAdapter!!.notifyDataSetChanged()
                    return true
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    var temp = notCompletedAssignmentsList.toMutableList()
                    notCompletedAssignmentsList.clear()
                    notCompletedAssignmentsList.addAll(filter(temp, query) as ArrayList<Assignment>)
                    notCompletedAdapter!!.notifyDataSetChanged()
                    temp = completedAssignmentsList.toMutableList()
                    completedAssignmentsList.clear()
                    completedAssignmentsList.addAll(filter(temp, query) as ArrayList<Assignment>)
                    completedAdapter!!.notifyDataSetChanged()
                    return true
                }
                return false
            }

        })
        searchView.setOnCloseListener {
            notCompletedAssignmentsList.clear()
            notCompletedAssignmentsList.addAll(assignmentDao.selectAllNotCompleted())
            notCompletedAdapter!!.notifyDataSetChanged()
            completedAssignmentsList.clear()
            completedAssignmentsList.addAll(assignmentDao.selectAllCompleted())
            completedAdapter!!.notifyDataSetChanged()
            false
        }

        return root
    }

    private fun notifyUser(msg: String) {
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addCustomAssignment() {
        val fragment = CustomAssignmentDialog(this)
        fragment.show(parentFragmentManager, null)
    }

    private fun syncAssignments() {
        val moodleApi = API()
        assignmentDao.deleteMoodleAssignments()
        notCompletedAssignmentsList.clear()
        notCompletedAdapter!!.notifyDataSetChanged()
        completedAssignmentsList.clear()
        completedAdapter!!.notifyDataSetChanged()
        // TODO syncing ignores the assignment being already checked: overwrites it completely
        val accounts = moodleDao.selectAll()
        if (accounts.isEmpty()) {
            notifyUser("No Moodle accounts saved")
            return
        }
        for (account in moodleDao.selectAll()) {
            moodleApi.setAuthority(account.apiLink)
            notifyUser("Accessing ${account.apiLink} as ${account.userName}")
            moodleApi.login(account.userName, account.password) { response: Any ->
                run {
                    if (response is LoginSuccessData) {
                        moodleApi.getAssignments { response: Any ->
                            run {
                                if (response is AssignmentError) {
                                    println(response.message)
                                    notifyUser("Moodle: failed to get assignments")
                                }
                                if (response is AssignmentResponse) {
                                    addAssignmentsFromMoodle(
                                        response.courses,
                                        moodleApi.token,
                                    )
                                }
                            }
                        }
                    }
                    if (response is LoginErrorData) {
                        println(response.error)
                        notifyUser("Moodle: failed to login")
                    }
                }
            }
        }
        notCompletedAssignmentsList.addAll(assignmentDao.selectAllNotCompleted())
        notCompletedAdapter!!.notifyDataSetChanged()
        completedAssignmentsList.addAll(assignmentDao.selectAllCompleted())
        completedAdapter!!.notifyDataSetChanged()
    }

    private fun addAssignmentsFromMoodle(courses: List<Course>, token: String) {
        for (course in courses) {
            for (moodleAssignment in course.assignments) {
                val listOfUrlsStrings: List<String> = moodleAssignment.introattachments.map {
                    it.fileurl
                }
                val listOfUrls: List<URL> = listOfUrlsStrings.map { URL("$it?token=$token") }
                addAssignmentFromMoodleToAssignmentList(
                    title = moodleAssignment.name,
                    description = moodleAssignment.intro,
                    deadline = Date(moodleAssignment.duedate),
                    links = listOfUrls,
                )
            }
        }
    }


    /**
     * Appends an assignment as received from Moodle, refreshing the recycler view and the
     * notifications for the deadlines.
     */
    private fun addAssignmentFromMoodleToAssignmentList(
        title: String,
        description: String,
        deadline: Date,
        links: List<URL>? = null,
        moodleId: Int? = null
    ) {
        val uid: Long =
            assignmentDao.insertOneFromMoodle(title, description, deadline, links, moodleId)
        val assignment = assignmentDao.selectOne(uid)
        notCompletedAssignmentsList.add(assignment)
        notCompletedAdapter?.notifyDataSetChanged()
    }

    /**
     * Appends an assignment written by the user, refreshing the recycler view and the notifications
     * for the deadlines.
     */
    fun addAssignmentCustomToAssignmentList(
        title: String, description: String, deadline: Date, links: List<URL>? = null
    ) {
        val uid: Long = assignmentDao.insertOneCustom(title, description, deadline, links)
        val assignment = assignmentDao.selectOne(uid)
        if (!assignment.isCompleted) {
            notCompletedAssignmentsList.add(assignment)
            notCompletedAdapter?.notifyDataSetChanged()
        } else {
            completedAssignmentsList.add(assignment)
            completedAdapter?.notifyDataSetChanged()
        }
    }

    fun markAssignmentAsCompleted(assignment: Assignment) {
        notCompletedAssignmentsList.remove(assignment)
        completedAssignmentsList.add(assignment)
        assignment.isCompleted = true
        assignmentDao.updateOne(assignment)
        notCompletedAdapter!!.notifyDataSetChanged()
        completedAdapter!!.notifyDataSetChanged()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        fun filter(assignments: List<Assignment>, search: String): List<Assignment> {
            val lowerCaseQuery = search.toLowerCase(Locale.ROOT)
            val found = arrayListOf<Assignment>()
            for (assi in assignments) {
                val text = assi.title.toLowerCase(Locale.ROOT)
                if (text.contains(lowerCaseQuery)) {
                    found.add(assi)
                }
            }
            return found
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): AssignmentsTabFragment {
            return AssignmentsTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}

private class AssignmentsAdapter(
    val items: ArrayList<Assignment>,
    val context: Context,
    val fragmentManager: FragmentManager,
    val fragment: AssignmentsTabFragment
) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    // Factory creating each element of the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.assignment_list_entry, parent, false),
            fragmentManager,
            fragment
        )
    }

    // Filler of the content of each TextView in the RecyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.assignmentListEntryTextView.text = items[position].title
        holder.assignment = items[position]
    }
}

private class ViewHolder(
    view: View,
    val fragmentManager: FragmentManager,
    val assignmentFragment: AssignmentsTabFragment
) :
    RecyclerView.ViewHolder(view) {
    lateinit var assignment: Assignment
    val assignmentListEntryTextView: TextView = view.findViewById(R.id.assignmentsListEntry)

    init {
        assignmentListEntryTextView.setOnClickListener {
            displayAssignmentDetailsAsDialog()
        }
    }

    private fun displayAssignmentDetailsAsDialog() {
        // At this point this.assignment should be initialised, as is is set by onBindViewHolder().
        // Otherwise it would not make much sense: how can we click (tick, mark) a view
        // which was never displayed?
        val fragment = AssignmentDetailsFragment(assignment)
        fragment.show(fragmentManager, null)
        fragment.onAssignmentCompletedButtonClicked = {
            if (fragment.isAssignmentCompleted) {
                val assignment = fragment.assignment
                assignmentFragment.markAssignmentAsCompleted(assignment)
            }
        }
    }
}
