package at.tugraz.onpoint.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.onpoint.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TodoFragmentListView : Fragment(R.layout.activity_todo_listview) {
    val EMPTY_STRING : String = " "
    val args: TodoFragmentListViewArgs by navArgs()
    var todoList = arrayListOf<String>()
    var todoListDone = arrayListOf<String>()
    var adapter: TodoListAdapter? = null
    var adapterDone: TodoListDoneAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.activity_todo_listview, container, false)
        rootView.findViewById<FloatingActionButton>(R.id.todo_addButton).setOnClickListener { view: View -> onAddButtonClick(
            view
        ) }

        val todosView: RecyclerView = rootView.findViewById(R.id.todo_listview_active)
        todosView.layoutManager = LinearLayoutManager(this.context)

        adapter = TodoListAdapter(
            this,
            todoList
        )
        todosView.adapter = adapter

        adapter?.notifyDataSetChanged()


        val todosDoneView: RecyclerView = rootView.findViewById(R.id.todo_listview_done)
        todosDoneView.layoutManager = LinearLayoutManager(this.context)

        adapterDone = TodoListDoneAdapter (
            todoListDone
        )

        todosDoneView.adapter = adapterDone

        adapterDone?.notifyDataSetChanged()

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

    fun addItemToTodoList(text : String) {
        todoList.add(text)
        adapter?.notifyDataSetChanged()
    }

    fun moveElementToDone(text : String) {
        todoList.remove(text)
        adapter?.notifyDataSetChanged()
        todoListDone.add(text)
        adapterDone?.notifyDataSetChanged()



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = args.listItemText
        if(text.equals(EMPTY_STRING)) {
            return
        }
        addItemToTodoList(text)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("todolist", todoList)
    }
}
