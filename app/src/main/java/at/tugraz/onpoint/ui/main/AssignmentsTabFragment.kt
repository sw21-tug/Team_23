@file:Suppress("unused")

package at.tugraz.onpoint.ui.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import at.tugraz.onpoint.R
import at.tugraz.onpoint.database.AssignmentDao
import at.tugraz.onpoint.database.MoodleDao
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.getDbInstance
import at.tugraz.onpoint.moodle.*
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class AssignmentsTabFragment : Fragment() {
    private lateinit var pageViewModel: PageViewModel
    var assignmentsList = arrayListOf<Assignment>()
    private var completeState = arrayListOf<Assignment>()
    private var adapter: AssignmentsAdapter? = null
    val db: OnPointAppDatabase = getDbInstance(null)
    private val moodleDao: MoodleDao = db.getMoodleDao()
    private val assignmentDao: AssignmentDao = db.getAssignmentDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
        // Fill the assignment list retrieved from the persistent database
        assignmentsList.addAll(assignmentDao.selectAll())
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
        adapter =
            this.context?.let { AssignmentsAdapter(assignmentsList, it, parentFragmentManager) }
        assignmentsRecView.adapter = this.adapter

        val searchView: SearchView = root.findViewById(R.id.assignment_searchview)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val temp = completeState.toMutableList()
                    assignmentsList.clear()
                    assignmentsList.addAll(filter(temp, newText) as ArrayList<Assignment>)
                    adapter!!.notifyDataSetChanged()
                    return true
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val temp = completeState.toMutableList()
                    assignmentsList.clear()
                    assignmentsList.addAll(filter(temp, query) as ArrayList<Assignment>)
                    adapter!!.notifyDataSetChanged()
                    return true
                }
                return false
            }
        })

        return root
    }

    private fun notifyUser(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    fun syncAssignments() {



        val moodleApi = API()
        assignmentDao.deleteMoodleAssignments()
        assignmentsList.clear()
        //debugging
        notifyUser("starting to sanch assignments")
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
                        notifyUser("Moodle: successful login")
                        moodleApi.getAssignments { response: Any ->
                            run {
                                if (response is AssignmentError) {
                                    println(response.message)
                                    notifyUser("Moodle: failed to get assignments")
                                }
                                if (response is AssignmentResponse) {
                                    notifyUser("Moodle: successfully obtained assignments")
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
        assignmentsList.addAll(assignmentDao.selectAll())
        adapter!!.notifyDataSetChanged()
    }

    private fun addAssignmentsFromMoodle(courses: List<Course>, token: String) {
        for (course in courses) {
            for (moodleAssignment in course.assignments) {
                val listOfUrlsStrings: List<String> = moodleAssignment.introattachments.map {
                    it.fileurl
                }
                val listOfUrls: List<URL> = listOfUrlsStrings.map { URL("$it?token=$token") }
                addAssignmentToAssignmentList(
                    title = moodleAssignment.name,
                    // TODO consider stripping the HTML from the description here
                    description = moodleAssignment.intro,
                    deadline = Date(moodleAssignment.duedate),
                    links = listOfUrls,

                )
            }
        }
    }

    /**
     * Appends an assignment written by the user, refreshing the recycler view and the notifications
     * for the deadlines.
     */
    private fun addAssignmentToAssignmentList(
        title: String, description: String, deadline: Date, links: List<URL>? = null, isCustom : Boolean = false
    ) {
        val uid: Long = assignmentDao.insertOne(title, description, deadline, links, isCustom)
        val assignment = assignmentDao.selectOne(uid)
        assignmentsList.add(assignment)
        completeState.add(assignment)
        adapter?.notifyDataSetChanged()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"


        fun filter(assignments: List<Assignment>, search: String): List<Assignment> {
            val lowerCaseQuery = search.toLowerCase(Locale.ROOT)
            val found: MutableList<Assignment> = ArrayList()
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

@Entity
data class Assignment(
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "deadline")
    var deadlineUnixTime: Long,

    @ColumnInfo(name = "links")
    var links: String = "",

    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null,

    @ColumnInfo(name = "moodle_id")
    var moodleId: Int? = null,

    @ColumnInfo(name = "is_custom")
    var isCustom: Boolean = false

) {

    companion object {
        fun convertDeadlineDate(deadline: Date): Long {
            return deadline.time / 1000
        }

        fun encodeLinks(linksList: List<URL>): String {
            var encoded = ""
            linksList.forEach {
                encoded += "$it;"
            }
            return encoded.trimEnd(';')
        }
    }

    fun getDeadlineDate(): Date {
        return Date(deadlineUnixTime * 1000)
    }

    fun getLinksAsUrls(): List<URL> {
        return links.split(";").map {
            URL(it)
        }
    }

    fun linksToMultiLineString(): String {
        val text: StringBuilder = StringBuilder()
        getLinksAsUrls().forEach {
            text.append(it)
            text.append('\n')
        }
        return text.toString()
    }

    // Call this function ONLY after the ID is set.
    fun buildAndScheduleNotification(context: Context, reminder_date: Calendar) {
        val intentToLaunchNotification = Intent(context, ScheduledNotificationReceiver::class.java)
        intentToLaunchNotification.putExtra(
            "title",
            context.getString(R.string.assignment_notification_title)
        )
        intentToLaunchNotification.putExtra(
            "text",
            this.title + ": " + this.getDeadlineDate().toString()
        )
        intentToLaunchNotification.putExtra("notificationId", uid)
        // Schedule notification
        val pending = PendingIntent.getBroadcast(
            context,
            uid!!,
            intentToLaunchNotification,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.set(AlarmManager.RTC_WAKEUP, reminder_date.timeInMillis, pending)
    }
}

private class AssignmentsAdapter(
    val items: ArrayList<Assignment>,
    val context: Context,
    val fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    // Factory creating each element of the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.assignment_list_entry, parent, false),
            fragmentManager
        )
    }

    // Filler of the content of each TextView in the RecyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.assignmentListEntryTextView.text = items[position].title
        holder.assignment = items[position]
    }
}

private class ViewHolder(view: View, val fragmentManager: FragmentManager) :
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
    }
}
