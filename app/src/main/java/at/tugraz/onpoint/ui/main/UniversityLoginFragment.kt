package at.tugraz.onpoint.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.onpoint.R
import at.tugraz.onpoint.database.*
import at.tugraz.onpoint.todolist.TodoListAdapter
import at.tugraz.onpoint.todolist.TodoListDoneAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UniversityLoginFragment : Fragment(R.layout.fragment_university_login) {

    var universityList = arrayListOf<Moodle>()
    val db: OnPointAppDatabase = getDbInstance(null)
    val moodleDao: MoodleDao = db.getMoodleDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_university_login, container, false)
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fill the to-do lists of ongoing and completed tasks with the data retrieved from
        // the persistent database
        universityList.addAll(moodleDao.selectAll())
    }
}
