package at.tugraz.onpoint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import at.tugraz.onpoint.ui.main.HomeScreenFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //new test comment solved issue OnPoint-000A
        println("In main activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .replace(R.id.mainFragment, HomeScreenFragment()).commit()


        //Commit by Chritina and Julian
    }
}
