package at.tugraz.onpoint.todolist

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import at.tugraz.onpoint.R


class TodoListDoneAdapter(private val fragment: TodoFragmentListView, context: Context, private val resource: Int, objects: ArrayList<String>) : ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = fragment.layoutInflater
        val rowView = inflater.inflate(resource, null, true)

        val textView = rowView.findViewById(R.id.todo_list_inactive_textview) as TextView
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        textView.text = getItem(position)

        return rowView
    }


}
