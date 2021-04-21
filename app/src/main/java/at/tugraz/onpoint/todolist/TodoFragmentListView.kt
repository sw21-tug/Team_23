package at.tugraz.onpoint.todolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.tugraz.onpoint.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodoFragmentListView : Fragment() {
    val EMPTY_STRING : String = " "
    val args: TodoFragmentListViewArgs by navArgs()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = args.listItemText
        if(text.equals(EMPTY_STRING)) {
            return
        }
    }
}
