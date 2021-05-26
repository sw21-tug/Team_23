package at.tugraz.onpoint.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import at.tugraz.onpoint.R
import java.util.*

class CustomAssignmentDialog :
    DialogFragment(R.layout.custom_assignment){

    var cal : Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.custom_assignment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val dialogBuilder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.custom_assignment, null)

            val button: Button = view.findViewById(R.id.custom_assignment_deadline_button)
            button.setOnClickListener { getDeadline() }

            dialogBuilder.setView(view)
            // Create the AlertDialog object and return it
            dialogBuilder.setTitle(R.string.custom_assignment_title)
            dialogBuilder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun getDeadline() {
        //display the dialog to set time for notification
        val fragment = CustomAssignmentSetDeadlineDialog(this)
        fragment.show(parentFragmentManager, null)
    }

}
