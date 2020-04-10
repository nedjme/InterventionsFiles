package com.example.taskmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.android.synthetic.main.activity_dashboard.*

class DbHandler (private val context: Context): SQLiteOpenHelper (context, DB_NAME,null, DB_VERSION ) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTasksTable = " CREATE TABLE $TABLE (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_TIME datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_NAME varchar,"+
                "$COL_ISCOMPLETED integer);"
        db?.execSQL(createTasksTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    fun addTask(task: Task): Boolean {
            val db = writableDatabase
            val cv = ContentValues()
            cv.put(COL_NAME, task.name)
            cv.put(COL_ISCOMPLETED, task.isCompleted)
            cv.put(COL_TIME, task.time)
            val result = db.insert(TABLE, null, cv)

            return result != (-1).toLong()
        }


    fun updateTask(task: Task) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, task.name)
        cv.put(COL_ISCOMPLETED,task.isCompleted)
        cv.put(COL_TIME, task.time)
        db.update(TABLE ,cv, "$COL_ID=?" , arrayOf(task.id.toString()))

    }

    fun deleteTask ( task: Task) {
            val db = writableDatabase
            db.delete(TABLE,"$COL_ID=?", arrayOf(task.id.toString()))


    }

    fun getTasks(): MutableList<Task> {
        val result: MutableList<Task> = ArrayList()
        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * from $TABLE", null)
        if (queryResult.moveToFirst()) {
            do {
                    val task = Task()

                    task.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                    task.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                    task.isCompleted = queryResult.getString(queryResult.getColumnIndex(COL_ISCOMPLETED)) == "1"
                    task.time = queryResult.getString(queryResult.getColumnIndex(COL_TIME))

                    result.add(task)
                } while (queryResult.moveToNext())
            }
            queryResult.close()
            return result
        }
    }


