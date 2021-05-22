package at.tugraz.onpoint.todolist

import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.onpoint.R
import at.tugraz.onpoint.database.Todo


class TodoListDoneAdapter(
    private val fragment: TodoFragmentListView,
    private val dataSet: ArrayList<Todo>
) :
    RecyclerView.Adapter<TodoListDoneAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, private val fragment: TodoFragmentListView) :
        RecyclerView.ViewHolder(view) {
        val textView: TextView
        val button: Button
        lateinit var todo: Todo

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.todo_list_inactive_textview)
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            button = view.findViewById(R.id.todo_delete_button)
            button.setOnClickListener { deleteItem() }
        }

        private fun deleteItem() {
            fragment.deleteTodo(this.todo)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.todo_list_inactive_element, viewGroup, false)
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


