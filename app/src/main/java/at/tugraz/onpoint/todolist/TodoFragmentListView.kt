package at.tugraz.onpoint.todolist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import at.tugraz.onpoint.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodoFragmentListView : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.activity_todo_listview, container, false)
        rootView.findViewById<FloatingActionButton>(R.id.todo_addButton).setOnClickListener { view: View -> onAddButtonClick(view) }
        return rootView
    }

    fun onAddButtonClick(v: View?) {
        findNavController().navigate(R.id.action_todoFragmentListView_to_todoFragmentAdd)
    }
}
