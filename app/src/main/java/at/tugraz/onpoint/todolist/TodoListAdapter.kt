package at.tugraz.onpoint.todolist

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import at.tugraz.onpoint.R


class TodoListAdapter(private val fragment: TodoFragmentListView, context: Context, private val resource: Int, objects: ArrayList<String>) : ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = fragment.layoutInflater
        val rowView = inflater.inflate(resource, null, true)


        val textView = rowView.findViewById(R.id.todo_list_active_textview) as TextView
        val button = rowView.findViewById(R.id.todo_check_button) as Button
        button.setOnClickListener{ view: View -> checkItem(
            view
        ) }

        textView.text = getItem(position)

        return rowView
    }

    private fun checkItem(v: View?) {

        val view = v!!.parent as View
        val tv = view.findViewById<View>(R.id.todo_list_active_textview) as TextView
        val s = tv.text.toString()
        fragment.moveElementToDone(s)

    }
}
