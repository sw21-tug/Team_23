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
import androidx.room.Room
import at.tugraz.onpoint.R
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.Todo
import at.tugraz.onpoint.database.TodoDao
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TodoFragmentListView : Fragment(R.layout.activity_todo_listview) {
    val EMPTY_STRING: String = " "
    val args: TodoFragmentListViewArgs by navArgs()
    var todoList = arrayListOf<Todo>()
    var todoListDone = arrayListOf<Todo>()
    var adapter: TodoListAdapter? = null
    var adapterDone: TodoListDoneAdapter? = null
    var db: OnPointAppDatabase? = null
    var todoDao: TodoDao? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.activity_todo_listview, container, false)
        rootView.findViewById<FloatingActionButton>(R.id.todo_addButton)
            .setOnClickListener { view: View ->
                onAddButtonClick(
                    view
                )
            }
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
        adapterDone = TodoListDoneAdapter(
            todoListDone
        )
        todosDoneView.adapter = adapterDone
        adapterDone?.notifyDataSetChanged()
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fill the to-do lists of ongoing and completed tasks with the data retrieved from
        // the persistent database
        val builder = Room.databaseBuilder(
            requireContext(),
            OnPointAppDatabase::class.java,
            "OnPointDb_v1"
        )
        // DB queries in the main thread need to be allowed explicitly to avoid a compilation error.
        // By default IO operations should be delegated to a background thread to avoid the UI
        // getting stuck on long IO operations.
        // We have very fast IO operations (small updates) and introducing background threads
        // and async queries is a pain for what we need to achieve.
        builder.allowMainThreadQueries()
        db = builder.build()
        todoDao = db!!.getTodoDao()
        todoList.addAll(todoDao!!.selectAllNotCompleted())
        todoListDone.addAll(todoDao!!.selectAllCompleted())
    }

    fun onAddButtonClick(v: View?) {
        findNavController().navigate(R.id.action_todoFragmentListView_to_todoFragmentAdd)
    }

    fun addItemToTodoList(text: String): Todo {
        // Insert a new entry to the DB, which automatically generates a UID and a timestamp
        // for the entry. Then retrieve the whole entry with its default fields and add it to
        // the in-memory arraylist of To-do objects. Returns the newly generated Todo object.
        val uid = todoDao!!.insertNew(text)
        val newTodo = todoDao!!.selectOne(uid)
        todoList.add(newTodo)
        adapter?.notifyDataSetChanged()
        return newTodo
    }

    fun moveElementToDone(todo: Todo) {
        // Remove the element from one list, mark it as completed, save its new state into the
        // persistent database, display it in the second list
        todoList.remove(todo)
        adapter?.notifyDataSetChanged()
        todo.isCompleted = true
        todoDao!!.updateOne(todo)
        todoListDone.add(todo)
        adapterDone?.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = args.listItemText
        if (text.equals(EMPTY_STRING)) {
            return
        }
        addItemToTodoList(text)
    }
}
