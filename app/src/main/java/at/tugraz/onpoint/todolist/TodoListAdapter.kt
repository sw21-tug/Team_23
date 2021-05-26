package at.tugraz.onpoint.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.onpoint.R
import at.tugraz.onpoint.database.Todo


class TodoListAdapter(
    private val fragment: TodoFragmentListView,
    private val dataSet: ArrayList<Todo>
) :
    RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, private val fragment: TodoFragmentListView) :
        RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.todo_list_active_textview)
        val button: Button = view.findViewById(R.id.todo_check_button)
        lateinit var todo: Todo

        init {
            // Define click listener for the ViewHolder's View.
            button.setOnClickListener { checkItem() }
        }

        private fun checkItem() {
            // At this point this.to-do should never be null, as is is set by onBindViewHolder().
            // Otherwise it would not make much sense: how can we check (tick, mark) a view
            // which was never displayed?
            fragment.moveElementToDone(this.todo)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.todo_list_active_element, viewGroup, false)
        return ViewHolder(view, fragment)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].title
        viewHolder.todo = dataSet[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}

