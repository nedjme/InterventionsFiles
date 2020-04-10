package com.example.taskmanager

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.SimpleDateFormat
import java.util.*

// ------------------------------------------------------------------------------------------------------------------------
class DashboardActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    lateinit var dbHandler: DbHandler
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Toolbar and View

        setContentView(R.layout.activity_dashboard)
        toolbar = findViewById(R.id.dashboard_toolbar)
        setSupportActionBar(toolbar)

        // Data Base Handler

        dbHandler = DbHandler(this)


//   Menu  Section -------------------------------------------------------------------------------------------------------------------------------

        val menu: ImageView = findViewById(R.id.iv_menu)
        menu.setOnClickListener {

            val popup = PopupMenu(this, menu)
            popup.inflate(R.menu.dashboard_menu)
            popup.setOnMenuItemClickListener {

                when (it.itemId) {

                    R.id.menu_today -> {

                        val tasks: MutableList<Task> = dbHandler.getTasks()
                        val todayTasks = mutableListOf<Task>()
                        val formatter = SimpleDateFormat("dd-MM-yyyy")

                        tasks.forEach { task ->
                            val date: Date = formatter.parse(task.time)
                            if (DateUtils.isToday(date.time)) todayTasks.add(task)
                        }

                        refreshList(todayTasks)

                    }

                    R.id.menu_this_week -> {

                        val tasks: MutableList<Task> = dbHandler.getTasks()
                        val this_week_Tasks = mutableListOf<Task>()
                        val formatter = SimpleDateFormat("dd-MM-yyyy")
                        val calendar = Calendar.getInstance()
                        calendar.firstDayOfWeek = Calendar.SATURDAY
                        val current_year = calendar[Calendar.YEAR]
                        val current_week_number = calendar[Calendar.WEEK_OF_YEAR]

                        tasks.forEach { task ->

                            val date: Date = formatter.parse(task.time)
                            calendar.time = date
                            val year = calendar[Calendar.YEAR]
                            val week_number = calendar[Calendar.WEEK_OF_YEAR]
                            if (current_year == year && current_week_number == week_number) this_week_Tasks.add(
                                task
                            )

                        }
                        refreshList (this_week_Tasks)
                    }

                    R.id.menu_all -> {
                        refreshList(dbHandler.getTasks())
                    }

                }
                true
            }
            popup.show()
        }


        //----------------------------------------------------------------------------------------------------------------------------------------


        //The layoutManger positions the RecyclerView’s items and tells it when to recycle items that have transitioned off-screen.

        rv_dashboard.layoutManager = LinearLayoutManager(this)

        // Listener on the add button to create a pop up dialog ( dialog_dashboard is the view )

        fab_dashboard.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val task_name = view.findViewById<EditText>(R.id.ev_task)
            val datePicker = view.findViewById<DatePicker>(R.id.dp_task)
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (task_name.text.isNotEmpty()) {
                    val task = Task()
                    task.name = task_name.text.toString()

                    val calendar: Calendar = Calendar.getInstance()
                    calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                    val sdf = SimpleDateFormat("dd-MM-yyyy")
                    task.time = sdf.format(calendar.time)
                    dbHandler.addTask(task)
                    refreshList(dbHandler.getTasks())
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            }
            dialog.show()
        }


 // Navigation Drawer Section ---------------------------------------------------------------------------------------------------------------------

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)


    }

    override fun onNavigationItemSelected(item: MenuItem ): Boolean {
        when (item.itemId) {
            R.id.menu_today -> {

                val tasks: MutableList<Task> = dbHandler.getTasks()
                val todayTasks = mutableListOf<Task>()
                val formatter = SimpleDateFormat("dd-MM-yyyy")

                tasks.forEach { task ->
                    val date: Date = formatter.parse(task.time)
                    if (DateUtils.isToday(date.time)) todayTasks.add(task)
                }

                rv_dashboard.adapter = DashboardAdapter(this, todayTasks)

            }

            R.id.menu_this_week -> {

                val tasks: MutableList<Task> = dbHandler.getTasks()
                val this_week_Tasks = mutableListOf<Task>()
                val formatter = SimpleDateFormat("dd-MM-yyyy")
                val calendar = Calendar.getInstance()
                calendar.firstDayOfWeek = Calendar.SATURDAY
                val current_year = calendar[Calendar.YEAR]
                val current_week_number = calendar[Calendar.WEEK_OF_YEAR]

                tasks.forEach { task ->

                    val date: Date = formatter.parse(task.time)
                    calendar.time = date
                    val year = calendar[Calendar.YEAR]
                    val week_number = calendar[Calendar.WEEK_OF_YEAR]
                    if (current_year == year && current_week_number == week_number) this_week_Tasks.add(
                        task
                    )

                }
                rv_dashboard.adapter = DashboardAdapter(this, this_week_Tasks)
            }

            R.id.menu_all -> {
                this.refreshList(dbHandler.getTasks())
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

 //------------------------------------------------------------------------------------------------------------------------------

    // function to refresh the list when resuming the app

    override fun onResume() {
        refreshList(dbHandler.getTasks())
        super.onResume()
    }

    // a function to refresh the list of tasks ( read from the database )

     fun refreshList( tasks: MutableList<Task>){
        rv_dashboard.adapter = DashboardAdapter(this,tasks)
    }


 // Recycler View Section -----------------------------------------------------------------------------------------------------------------

    class DashboardAdapter(val activity: DashboardActivity , val tasks: MutableList<Task>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, p0, false))
        }

        override fun getItemCount(): Int {
            return tasks.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {

            holder.checkBox.text = tasks[p1].name
            holder.taskTime.text = tasks[p1].time
            holder.checkBox.isChecked = tasks[p1].isCompleted
            holder.checkBox.setOnClickListener {
                tasks[p1].isCompleted = !tasks[p1].isCompleted
                activity.dbHandler.updateTask(tasks[p1])

            }
            holder.deleteBtn.setOnClickListener {
            activity.dbHandler.deleteTask(tasks[p1])
                activity.refreshList(activity.dbHandler.getTasks())
        }

            holder.editBtn.setOnClickListener {
                val dialog = AlertDialog.Builder(activity)
                val view = activity.layoutInflater.inflate(R.layout.edit_view, null)
                dialog.setView(view)
               val editBtn = view.findViewById<EditText>(R.id.editbtn)
                dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
                    tasks[p1].name = editBtn.text.toString()
                        activity.dbHandler.updateTask(tasks[p1])
                    activity.refreshList(activity.dbHandler.getTasks())
                    }
                dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                }
                dialog.show()
            }

        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val checkBox : CheckBox = v.findViewById(R.id.cb_task)
            val taskTime: TextView = v.findViewById(R.id.tv_task_time)
            val deleteBtn : FloatingActionButton = v.findViewById(R.id.rv_delete)
            val editBtn : FloatingActionButton = v.findViewById(R.id.rv_edit)

        }
    }

// ------------------------------------------------------------------------------------------------------------------------------------------------

    }
