package at.tugraz.onpoint.ui.main

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.onpoint.R
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class AssignmentsTabFragment : Fragment() {

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
        val root = inflater.inflate(R.layout.fragment_assignments, container, false)
        // List of things we want to put into the recyclerview
        val assignmentsList = ArrayList<Assignment>()
        for (i in 0..50) {
            assignmentsList.add(
                Assignment(
                    "Dummy Assignment $i",
                    "Dummy Description $i",
                    Date(),
                    arrayListOf<URL>(
                        URL("https://www.tugraz.at"),
                        URL("https://tc.tugraz.at"),
                    )
                )
            )
        }

        assignmentsList[0].buildAndFireNotification(this.requireContext(), 0)

        // Create the the Recyclerview, make it a linear list (not a grid), assign the list of
        // items to it and provide and adapter constructing each element of the list as a TextView
        val assignmentsRecView: RecyclerView = root.findViewById(R.id.assignmentsList)
        assignmentsRecView.layoutManager = LinearLayoutManager(this.context)
        assignmentsRecView.adapter = this.context?.let { AssignmentsAdapter(assignmentsList, it, parentFragmentManager) }
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
        fun newInstance(sectionNumber: Int): AssignmentsTabFragment {
            return AssignmentsTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}

data class Assignment(
    val title: String,
    val description: String,
    val deadline: Date,
    val links: ArrayList<URL>
) {
    fun linksToMultiLineString(): String {
        val text: StringBuilder = StringBuilder()
        links.forEach {
            text.append(it)
            text.append('\n')
        }
        return text.toString()
    }

    fun buildAndFireNotification(context : Context, id : Int) {
        val builder = NotificationCompat.Builder(context, context.getString(R.string.CHANNEL_ID))
            .setSmallIcon(R.drawable.ic_baseline_uni_24)
            .setContentTitle((context.getString(R.string.assignment_notification_title)))
            .setContentText(this.title + "\n" + this.deadline.toString())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        //launch notification
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(id, builder.build())
        }
    }
}

private class AssignmentsAdapter(val items: ArrayList<Assignment>, val context: Context, val fragmentManager: FragmentManager) :
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

private class ViewHolder(view: View, val fragmentManager: FragmentManager) : RecyclerView.ViewHolder(view) {
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
        fragment.show(fragmentManager,null)
    }
}
