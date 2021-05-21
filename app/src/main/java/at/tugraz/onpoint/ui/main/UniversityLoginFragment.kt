package at.tugraz.onpoint.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.onpoint.R
import at.tugraz.onpoint.database.*
import at.tugraz.onpoint.moodle.API
import at.tugraz.onpoint.moodle.LoginErrorData
import at.tugraz.onpoint.moodle.LoginSuccessData
import at.tugraz.onpoint.todolist.TodoListAdapter
import at.tugraz.onpoint.todolist.TodoListDoneAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

class UniversityLoginFragment : DialogFragment(R.layout.fragment_university_login) {

    var universityList = arrayListOf<Moodle>()
    val db: OnPointAppDatabase = getDbInstance(null)
    val moodleDao: MoodleDao = db.getMoodleDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_university_login, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val db_entries = moodleDao.selectAll()
            universityList.addAll(moodleDao.selectAll())
            val dialogBuilder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.fragment_university_login, null)
            view.findViewById<Button>(R.id.moodle_loginButton).setOnClickListener { onAddUniversity(view) }
            dialogBuilder.setView(view)
            dialogBuilder.setNegativeButton(
                R.string.assignment_dialog_close_button
            ) { _, _ ->
                // User cancelled the dialog.
                // This callback does nothing.
            }

            // Create the AlertDialog object and return it
            dialogBuilder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun onAddUniversity(view: View) {
        val universityName: String = view.findViewById<TextView>(R.id.university_login_name).text.toString()
        val apiLink: String = view.findViewById<TextView>(R.id.university_login_api).text.toString()
        val apiUsername: String = view.findViewById<TextView>(R.id.university_login_username).text.toString()
        val apiPassword: String = view.findViewById<TextView>(R.id.university_login_password).text.toString()
        val moodle_api = API()
        moodle_api.setAuthority(apiLink)
        moodle_api.login(apiUsername, apiPassword) {
            response: Any ->
            run {
                if (response is LoginSuccessData) {
                    addUniversity(universityName, apiLink, apiUsername, apiPassword)
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }



    }

    fun addUniversity(universityName: String, apiLink: String, apiUsername: String, apiPassword: String) {
        val uid = moodleDao.insertOne(universityName, apiUsername, apiPassword, apiLink)
        val newUniversityFromDatabase = moodleDao.selectOne(uid)
        universityList.add(newUniversityFromDatabase)
    }
}
