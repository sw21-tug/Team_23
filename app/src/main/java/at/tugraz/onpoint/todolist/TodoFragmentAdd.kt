package at.tugraz.onpoint.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import at.tugraz.onpoint.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodoFragmentAdd : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.activity_todo_add, container, false)
        rootView.findViewById<FloatingActionButton>(R.id.todo_saveButton)
            .setOnClickListener { onSaveButtonClick() }
        return rootView
    }

    private fun onSaveButtonClick() {
        val textInputField: EditText? = view?.findViewById(R.id.todo_InputField)
        val input = textInputField?.text.toString()
        val action = TodoFragmentAddDirections.actionTodoFragmentAddToTodoFragmentListView(input)
        findNavController().navigate(action)
    }
}
