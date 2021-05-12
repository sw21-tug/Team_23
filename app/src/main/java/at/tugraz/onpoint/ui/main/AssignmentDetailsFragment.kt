package at.tugraz.onpoint.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import at.tugraz.onpoint.R
import java.util.*

/**
 * A simple [Fragment] subclass for the details of an assignment displayed within a Dialog.
 * Use the [AssignmentDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssignmentDetailsFragment(val assignment: Assignment) : DialogFragment(R.layout.fragment_assignment_details) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignment_details, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val dialogBuilder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.fragment_assignment_details, null)
            view.findViewById<TextView>(R.id.assignmentsListDetailsDescription).text =
                assignment.description
            view.findViewById<TextView>(R.id.assignmentsListDetailsDeadline).text =
                getString(R.string.assignment_dialog_deadline).plus(assignment.deadline.toString())
            view.findViewById<TextView>(R.id.assignmentsListDetailsLinks).text =
                assignment.linksToMultiLineString()

            ////////////////////////// FOR Add To Calendar ///////////////////////////7

            val button: Button =  view.findViewById<Button>(R.id.addMeToCalendar)
            button.setOnClickListener { view: View ->
                addDeadlineToCalendar(
                    assignment
                )
            }

            ////////////////////////// FOR Add To Calendar ///////////////////////////7


            dialogBuilder.setView(view)
            dialogBuilder.setTitle(assignment.title)
            dialogBuilder.setNeutralButton(
                R.string.assignment_dialog_close_button
            ) { _, _ ->
                // User cancelled the dialog.
                // This callback does nothing.
            }


            // Create the AlertDialog object and return it
            dialogBuilder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun addDeadlineToCalendar(assignment : Assignment)
    {
        val cont_res : ContentResolver = context?.contentResolver!!
        //val deadlineToAdd : Calendar = Calendar.getInstance()

        val projection = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
        val calCursor : Cursor? = cont_res.query(CalendarContract.Calendars.CONTENT_URI, projection, CalendarContract.Calendars.VISIBLE + " = 1 AND "  + CalendarContract.Calendars.IS_PRIMARY + "=1", null, CalendarContract.Calendars._ID + " ASC");

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, assignment.deadline.toString())
            put(CalendarContract.Events.DTEND, assignment.deadline.toString())
            put(CalendarContract.Events.TITLE, assignment.title)
            put(CalendarContract.Events.DESCRIPTION, assignment.description)
            put(CalendarContract.Events.CALENDAR_ID, 0) /// Temporary hotfix TODO: find default ID
            put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles")
        }
        val uri: Uri? =cont_res.insert(CalendarContract.Events.CONTENT_URI, values)
        val intent = Intent(Intent.ACTION_VIEW)
            .setData(uri)
        startActivity(intent)
    }
}
