package at.tugraz.onpoint.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.tugraz.onpoint.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TodoFragmentListView : Fragment(R.layout.activity_todo_listview) {
    val EMPTY_STRING : String = " "
    val args: TodoFragmentListViewArgs by navArgs()
    var todoList = arrayListOf<String>()
    var adapter: ArrayAdapter<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.activity_todo_listview, container, false)
        rootView.findViewById<FloatingActionButton>(R.id.todo_addButton).setOnClickListener { view: View -> onAddButtonClick(
            view
        ) }

        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, todoList
        )

        var listView: ListView = rootView.findViewById(R.id.todo_listview)

        listView?.adapter = adapter

        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            for(element in savedInstanceState.getStringArrayList("todolist")!!){
                todoList.add(element)
            }
        }
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
        todoList.add(text)
        adapter?.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("todolist", todoList)
    }
}
