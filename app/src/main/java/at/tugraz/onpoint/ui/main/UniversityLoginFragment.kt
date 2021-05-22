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
import at.tugraz.onpoint.R
import at.tugraz.onpoint.database.Moodle
import at.tugraz.onpoint.database.MoodleDao
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.getDbInstance
import at.tugraz.onpoint.moodle.API
import at.tugraz.onpoint.moodle.LoginSuccessData

class UniversityLoginFragment : DialogFragment(R.layout.fragment_university_login) {

    private var universityList = arrayListOf<Moodle>()
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
            universityList.addAll(moodleDao.selectAll())
            val dialogBuilder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.fragment_university_login, null)
            view.findViewById<Button>(R.id.moodle_loginButton)
                .setOnClickListener { onAddUniversity(view) }
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

    private fun onAddUniversity(view: View) {
        val universityName: String =
            view.findViewById<TextView>(R.id.university_login_name).text.toString()
        val apiLink: String = view.findViewById<TextView>(R.id.university_login_api).text.toString()
        val apiUsername: String =
            view.findViewById<TextView>(R.id.university_login_username).text.toString()
        val apiPassword: String =
            view.findViewById<TextView>(R.id.university_login_password).text.toString()
        val moodleApi = API()
        moodleApi.setAuthority(apiLink)
        moodleApi.login(apiUsername, apiPassword) { response: Any ->
            run {
                if (response is LoginSuccessData) {
                    addUniversity(universityName, apiLink, apiUsername, apiPassword)
                    Toast.makeText(
                        context,
                        R.string.university_login_successful,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, R.string.university_login_failed, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun addUniversity(
        universityName: String,
        apiLink: String,
        apiUsername: String,
        apiPassword: String
    ) {
        val uid = moodleDao.insertOne(universityName, apiUsername, apiPassword, apiLink)
        val newUniversityFromDatabase = moodleDao.selectOne(uid)
        universityList.add(newUniversityFromDatabase)
    }
}
