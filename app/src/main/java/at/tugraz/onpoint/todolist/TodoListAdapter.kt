package at.tugraz.onpoint.todolist

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.onpoint.R



class TodoListAdapter(private val fragment: TodoFragmentListView, private val dataSet: ArrayList<String>) :
    RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, private val fragment: TodoFragmentListView) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val button: Button


        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.todo_list_active_textview)

            button = view.findViewById(R.id.todo_check_button)
            button.setOnClickListener { view: View ->
                checkItem(
                    view
                )
            }

        }

        private fun checkItem(v: View?) {

            val view = v!!.parent as View
            val tv = view.findViewById<View>(R.id.todo_list_active_textview) as TextView
            val s = tv.text.toString()
            fragment.moveElementToDone(s)

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
            viewHolder.textView.text = dataSet[position]


        }

        // Return the size of your dataset (invoked by the layout manager)


        override fun getItemCount() = dataSet.size



}

