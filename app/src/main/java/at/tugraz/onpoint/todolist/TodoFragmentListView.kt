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
import at.tugraz.onpoint.database.OnPointAppDatabase
import at.tugraz.onpoint.database.Todo
import at.tugraz.onpoint.database.TodoDao
import at.tugraz.onpoint.database.getDbInstance
import com.google.android.material.floatingactionbutton.FloatingActionButton

const val EMPTY_STRING: String = " "

class TodoFragmentListView : Fragment(R.layout.activity_todo_listview) {
    private val args: TodoFragmentListViewArgs by navArgs()
    var todoList = arrayListOf<Todo>()
    var todoListDone = arrayListOf<Todo>()
    private var adapter: TodoListAdapter? = null
    private var adapterDone: TodoListDoneAdapter? = null
    val db: OnPointAppDatabase = getDbInstance(null)
    private val todoDao: TodoDao = db.getTodoDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.activity_todo_listview, container, false)
        rootView.findViewById<FloatingActionButton>(R.id.todo_addButton)
            .setOnClickListener { onAddButtonClick() }
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
            this,
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
        todoList.addAll(todoDao.selectAllNotCompleted())
        todoListDone.addAll(todoDao.selectAllCompleted())
    }

    private fun onAddButtonClick() {
        findNavController().navigate(R.id.action_todoFragmentListView_to_todoFragmentAdd)
    }

    fun addItemToTodoList(text: String): Todo {
        // Insert a new entry to the DB, which automatically generates a UID and a timestamp
        // for the entry. Then retrieve the whole entry with its default fields and add it to
        // the in-memory arraylist of To-do objects. Returns the newly generated Todo object.
        val uid = todoDao.insertNew(text)
        val newTodo = todoDao.selectOne(uid)
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
        todoDao.updateOne(todo)
        todoListDone.add(todo)
        adapterDone?.notifyDataSetChanged()
    }


    fun deleteTodo(todo: Todo) {
        todoListDone.remove(todo)
        adapterDone?.notifyDataSetChanged()
        todoDao.deleteOne(todo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = args.listItemText
        if (text == EMPTY_STRING) {
            return
        }
        addItemToTodoList(text)
    }
}
