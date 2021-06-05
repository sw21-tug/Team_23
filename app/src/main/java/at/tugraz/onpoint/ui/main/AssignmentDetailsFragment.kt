package at.tugraz.onpoint.ui.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import at.tugraz.onpoint.R
import java.util.*

class AssignmentDetailsFragment(val assignment: Assignment) :
    DialogFragment(R.layout.fragment_assignment_details) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignment_details, container, false)
    }

    lateinit var alertDialog: AlertDialog;
    var isAssignmentDone: Boolean = false;
    var onDoneButtonClicked: () -> Unit = {};

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val dialogBuilder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.fragment_assignment_details, null)
            view.findViewById<TextView>(R.id.assignmentsListDetailsDescription).text =
                HtmlCompat.fromHtml(assignment.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
            view.findViewById<TextView>(R.id.assignmentsListDetailsDeadline).text =
                getString(R.string.assignment_dialog_deadline).plus(
                    assignment.getDeadlineDate().toString()
                )
            view.findViewById<TextView>(R.id.assignmentsListDetailsLinks).text =
                assignment.linksToMultiLineString()
            // Add-to-calendar button
            val button: Button = view.findViewById(R.id.addMeToCalendar)
            button.setOnClickListener { addDeadlineToCalendar(assignment) }
            val done_button: Button = view.findViewById(R.id.assignmentsListDetailsDoneButton)
            if(assignment.done == 0) {
                done_button.setOnClickListener { onAssignmentDone() }
            } else {
                done_button.visibility = View.INVISIBLE
            }
            dialogBuilder.setView(view)
            dialogBuilder.setTitle(assignment.title)
            dialogBuilder.setNegativeButton(
                R.string.assignment_dialog_close_button
            ) { _, _ ->
                // User cancelled the dialog.
                // This callback does nothing.
            }
            dialogBuilder.setPositiveButton(
                R.string.set_reminder
            ) { _, _ ->
                //display the dialog to set time for notification
                val fragment = AssignmentSetDateDialog(assignment)
                fragment.show(parentFragmentManager, null)
            }
            // Create the AlertDialog object and return it
            alertDialog = dialogBuilder.create()
            return alertDialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun onAssignmentDone() {
        if(assignment.done == 0) {
            isAssignmentDone = true;
            onDoneButtonClicked();
            alertDialog.dismiss()
        }
    }

    private fun addDeadlineToCalendar(assignment: Assignment) {
        context?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR),
                    1
                )
            } else {
                val contRes: ContentResolver = context?.contentResolver!!
                val projection = arrayOf(
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
                )
                val calCursor: Cursor? = contRes.query(
                    CalendarContract.Calendars.CONTENT_URI,
                    projection,
                    CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + " = 1",
                    null,
                    CalendarContract.Calendars._ID + " ASC"
                )
                var id = 0L
                if (calCursor != null && calCursor.moveToFirst()) {
                    val calIdCol = calCursor.getColumnIndex(projection[0])
                    id = calCursor.getLong(calIdCol)
                    calCursor.close()
                }
                val cal = Calendar.getInstance()
                cal.time = assignment.getDeadlineDate()
                val end = cal.timeInMillis
                cal.timeInMillis = cal.timeInMillis - (1000 * 3600)
                val start = cal.timeInMillis
                val values = ContentValues().apply {
                    put(CalendarContract.Events.DTSTART, start)
                    put(CalendarContract.Events.DTEND, end)
                    put(CalendarContract.Events.TITLE, assignment.title)
                    put(CalendarContract.Events.DESCRIPTION, assignment.description)
                    put(CalendarContract.Events.CALENDAR_ID, id)
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                }
                val uri: Uri? = contRes.insert(CalendarContract.Events.CONTENT_URI, values)
                val intent = Intent(Intent.ACTION_VIEW).setData(uri)
                startActivity(intent)
            }
        }
    }
}
