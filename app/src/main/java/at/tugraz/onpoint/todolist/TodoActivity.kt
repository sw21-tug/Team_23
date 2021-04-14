package at.tugraz.onpoint.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import at.tugraz.onpoint.R

class TodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
    }
}
