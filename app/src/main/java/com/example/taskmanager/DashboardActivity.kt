package com.example.taskmanager

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.edit_view.*
import java.text.SimpleDateFormat
import java.util.*


// ------------------------------------------------------------------------------------------------------------------------
class DashboardActivity : AppCompatActivity() {

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
        //dbHandler.onCreate()


//   Window Section -------------------------------------------------------------------------------------------------------------------------------

        val window: ImageView = findViewById(R.id.iv_menu)

        window.setOnClickListener {

            val popup = PopupWindow(this)
            val view = layoutInflater.inflate(R.layout.edit_view , null)
            popup.contentView = view

            val btn_filter =  view.findViewById<Button>(R.id.btn_filter)
            val dp_filter  = view.findViewById<DatePicker>(R.id.dp_filter)

                    btn_filter.setOnClickListener{

                        popup.dismiss()

                        val calendar: Calendar = Calendar.getInstance()
                        calendar.set(dp_filter.year, dp_filter.month, dp_filter.dayOfMonth)
                        val sdf = SimpleDateFormat("dd-MM-yyyy")
                        val date_filter = sdf.format(calendar.time)

                        val interventions: MutableList<Intervention> = dbHandler.getInterventions()
                        val filtered = mutableListOf<Intervention>()

                        interventions.forEach { intervention ->

                            if (intervention.date == date_filter) filtered.add(intervention)
                        }

                        refreshList(filtered)
                    }

                 Log.e("shwdropw","----------------------------------------------------------")
                 popup.showAsDropDown(window)

               true

                    }


        //----------------------------------------------------------------------------------------------------------------------------------------


        //The layoutManger positions the RecyclerView’s items and tells it when to recycle items that have transitioned off-screen.

        rv_dashboard.layoutManager = LinearLayoutManager(this)

        // Listener on the add button to create a pop up dialog ( dialog_dashboard is the view )

        fab_dashboard.setOnClickListener {

            val intervention = Intervention()

            val dialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            dialog.setView(view)

            val sp_type = view.findViewById<Spinner>(R.id.type_spinner)
            val sp_plumer = view.findViewById<Spinner>(R.id.plumer_spinner)
            val plumers = arrayOf("Smahi","Farah")
            val types = arrayOf("type1" , "type2" , "type3")

            sp_type.adapter =  ArrayAdapter(dialog.context, R.layout.support_simple_spinner_dropdown_item , types)
            sp_plumer.adapter = ArrayAdapter(dialog.context, R.layout.support_simple_spinner_dropdown_item,plumers)

            sp_plumer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {  intervention.plumer_name = "Farah"  }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
                               { intervention.plumer_name = plumers.get(position)}
            }

            sp_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {  intervention.type= "type1"  }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
                { intervention.type = types.get(position)}
            }

            val number = view.findViewById<EditText>(R.id.ev_number)
            val datePicker = view.findViewById<DatePicker>(R.id.dp_task)



            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->

                   intervention.number = number.text.toString()
                    Log.e("adding " , intervention.number)
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                    val sdf = SimpleDateFormat("dd-MM-yyyy")
                    intervention.date = sdf.format(calendar.time)
                    dbHandler.addIntervention(intervention)
                Log.e("Interventions" , "-----------------------------------------------------------------------------------")
                    Log.e("Interventions" , dbHandler.getInterventions().toString())
                    refreshList(dbHandler.getInterventions())

            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            }
            dialog.show()
        }


    }

    // function to refresh the list when resuming the app

    override fun onResume() {
        refreshList(dbHandler.getInterventions())
        super.onResume()
    }

    // a function to refresh the list of tasks ( read from the database )

     fun refreshList( interventions: MutableList< Intervention>){
        rv_dashboard.adapter = DashboardAdapter(this,interventions)
    }


 // Recycler View Section -----------------------------------------------------------------------------------------------------------------

    class DashboardAdapter(val activity: DashboardActivity , val interventions: MutableList<Intervention>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, p0, false))
        }

        override fun getItemCount(): Int {
            return interventions.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {

            holder.number.text = interventions[p1].number
            holder.date.text = interventions[p1].date
            holder.type.text = interventions[p1].type
            holder.plumer.text = interventions[p1].plumer_name

            Log.e("inside binder" ," ----------------------------------------------------" )

            holder.deleteBtn.setOnClickListener {
            activity.dbHandler.deleteIntervention(interventions[p1])
                activity.refreshList(activity.dbHandler.getInterventions())
        }

            holder.editBtn.setOnClickListener {

                val dialog = AlertDialog.Builder(activity)
                val view = activity.layoutInflater.inflate(R.layout.dialog_dashboard, null)
                dialog.setView(view)

                val sp_type = view.findViewById<Spinner>(R.id.type_spinner)
                val sp_plumer = view.findViewById<Spinner>(R.id.plumer_spinner)
                val plumers = arrayOf("Smahi","Farah")
                val types = arrayOf("type1" , "type2" , "type3")

                sp_type.adapter =  ArrayAdapter(dialog.context, R.layout.support_simple_spinner_dropdown_item , types)
                sp_plumer.adapter = ArrayAdapter(dialog.context, R.layout.support_simple_spinner_dropdown_item,plumers)

                sp_plumer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onNothingSelected(parent: AdapterView<*>?) {  interventions[p1].plumer_name = "Farah"  }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
                    { interventions[p1].plumer_name = plumers.get(position)}
                }

                sp_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onNothingSelected(parent: AdapterView<*>?) {  interventions[p1].type= "type1"  }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
                    { interventions[p1].type = types.get(position)}
                }



                dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
                    val number = view.findViewById<EditText>(R.id.ev_number)
                    val datePicker = view.findViewById<DatePicker>(R.id.dp_task)
                    interventions[p1].number = number.text.toString()
                    Log.e("adding " , interventions[p1].number)
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                    val sdf = SimpleDateFormat("dd-MM-yyyy")
                    interventions[p1].date = sdf.format(calendar.time)
                    activity.dbHandler.updateIntervention(p1,interventions[p1])
                    activity.refreshList(activity.dbHandler.getInterventions())
                    }
                dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                }
                dialog.show()
            }

        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val number : TextView = v.findViewById(R.id.number)
            val plumer : TextView= v.findViewById(R.id.plumer)
            val type: TextView = v.findViewById(R.id.type)
            val date: TextView = v.findViewById(R.id.date)
            val deleteBtn : FloatingActionButton = v.findViewById(R.id.rv_delete)
            val editBtn : FloatingActionButton = v.findViewById(R.id.rv_edit)

        }
    }

// ------------------------------------------------------------------------------------------------------------------------------------------------

    }
