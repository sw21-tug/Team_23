package at.tugraz.onpoint.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import at.tugraz.onpoint.R

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
}
