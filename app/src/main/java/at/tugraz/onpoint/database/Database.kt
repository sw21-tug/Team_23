package at.tugraz.onpoint.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import at.tugraz.onpoint.ui.main.Assignment
import java.net.URL
import java.util.*

// https://developer.android.com/reference/android/arch/persistence/room/ColumnInfo
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `assignment` (`title` TEXT NOT NULL, `description` TEXT NOT NULL, `deadline` INTEGER NOT NULL, `links` TEXT NOT NULL, `uid` INTEGER PRIMARY KEY AUTOINCREMENT, `moodle_id` INTEGER)")
    }
}

@Database(entities = [Todo::class, Assignment::class], version = 2, exportSchema = true)
abstract class OnPointAppDatabase : RoomDatabase() {
    abstract fun getTodoDao(): TodoDao
    abstract fun getAssignmentDao() : AssignmentDao
}

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = -1,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "creation_unix_time")
    val creationUnixTime: Int = -1,

    @ColumnInfo(name = "expiration_unix_time", defaultValue = "NULL")
    var expirationUnixTime: Int? = null,

    @ColumnInfo(name = "is_completed", defaultValue = "0")
    var isCompleted: Boolean = false,
) {
    fun creationDateTime(): Date {
        return Date(creationUnixTime.toLong() * 1000);
    }

    fun expirationDateTime(): Date? {
        if (expirationUnixTime != null) {
            return Date(expirationUnixTime!!.toLong() * 1000);
        }
        return null;
    }
}

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun selectAll(): List<Todo>

    @Query("SELECT * FROM todo WHERE is_completed")
    fun selectAllCompleted(): List<Todo>

    @Query("SELECT * FROM todo WHERE NOT is_completed")
    fun selectAllNotCompleted(): List<Todo>

    @Query("SELECT * FROM todo WHERE uid = (:uid)")
    fun selectOne(uid: Long): Todo

    @Query("INSERT INTO todo (title, creation_unix_time, expiration_unix_time, is_completed) VALUES (:title, strftime('%s', 'now'), NULL, 0)")
    fun insertNew(title: String): Long

    @Insert
    fun insertOne(todo: Todo)

    @Update
    fun updateMany(vararg todo: Todo)

    @Update
    fun updateOne(todo: Todo)

    @Delete
    fun deleteOne(todo: Todo)

    @Delete
    fun deleteMany(vararg todo: Todo)

    @Query("DELETE FROM todo")
    fun deleteAll()
}

@Dao
interface AssignmentDao {

    @Query("SELECT * FROM assignment WHERE uid = (:uid)")
    fun selectOne(uid: Long): Assignment

    @Query("INSERT INTO assignment (title, description, deadline, links, moodle_id) VALUES (:title, :description, :deadline, :links, :moodleId)")
    fun insertOneFromMoodleRaw(title: String, description: String, deadline : Long, links : String, moodleId : Int): Long

    fun insertOneFromMoodle(title: String, description: String, deadline : Date, links : List<URL>, moodleId : Int) : Long {
        return insertOneFromMoodleRaw(title, description, Assignment.convertDeadlineDate(deadline), Assignment.encodeLinks(links), moodleId)
    }

    @Query("DELETE FROM assignment")
    fun deleteAll()
}

var INSTANCE: OnPointAppDatabase? = null

fun getDbInstance(context: Context?): OnPointAppDatabase {
    if (INSTANCE == null) {
        val builder = Room.databaseBuilder(
            context!!,
            OnPointAppDatabase::class.java,
            "OnPointDb_v1"
        )
        // DB queries in the main thread need to be allowed explicitly to avoid a compilation error.
        // By default IO operations should be delegated to a background thread to avoid the UI
        // getting stuck on long IO operations.
        // We have very fast IO operations (small updates) and introducing background threads
        // and async queries is a pain for what we need to achieve.
        builder.allowMainThreadQueries()
        builder.addMigrations(MIGRATION_1_2)
        INSTANCE = builder.build()
    }
    return INSTANCE as OnPointAppDatabase
}

// TODO ideas:
// - isExpired() method to the Todo class
// - pre-populate the DB with 2 dummy tasks
// - select the tasks sorted by creation date (ORDER BY deadlineTs, creationTs)
