package com.example.taskmanager

import android.content.Context
import android.os.Environment

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

class DbHandler (private val context: Context)  {

    val gson = Gson()

    fun addIntervention(intervention: Intervention) {

        Log.e("adding intervention" , intervention.toString())
        val interventions : MutableList<Intervention> = getInterventions()
        interventions.add(intervention)
        Log.e("adding intervention" , interventions.toString())

        val jsonString = gson.toJson(interventions)
        Log.e("Json intervention" , jsonString)
        val file = getFichier("interventions")
        updateFile(file , jsonString)


        }


    fun updateIntervention(index : Int , intervention: Intervention) {

        var interventions = getInterventions()

        interventions[index].number = intervention.number
        interventions[index].type = intervention.type
        interventions[index].plumer_name = intervention.plumer_name
        interventions[index].date = intervention.date

        val jsonString = gson.toJson(interventions)
        Log.e("Json intervention" , jsonString)
        val file = getFichier("interventions")
        updateFile(file , jsonString)

    }


    fun deleteIntervention ( intervention: Intervention) {

        val interventions = getInterventions()
        interventions.remove(intervention)
        val jsonString = gson.toJson(interventions)
        Log.e("Json intervention" , jsonString)
        val file = getFichier("interventions")
        updateFile(file , jsonString)


    }

    fun getInterventions(): MutableList<Intervention> {

        var result =  mutableListOf<Intervention>()


       val bufferedReader: BufferedReader = getFichier("interventions").bufferedReader()
        val jsonFileString = bufferedReader.use { it.readText() }

        Log.e("data", jsonFileString)

        val returnType = object : TypeToken<MutableList<Intervention>>() {}.type

        result = gson.fromJson(jsonFileString, returnType)

            return result
        }

    // ----------------------------------------------------------------------------------------------------------------

    /*fun convertXmlString2DataObject(xmlString: String, cls: Class<*>): Any {
        val xmlMapper = XmlMapper()
        return xmlMapper.readValue(xmlString, cls)
    }

    fun convertXmlFile2DataObject(pathFile: String, cls: Class<*>): Any{
        val xmlMapper = XmlMapper()
        return xmlMapper.readValue(File(pathFile), cls)
    }

    fun write2XMLString(obj: Any): String {

        val xmlMapper = XmlMapper()
        // use the line of code for pretty-print XML on console. We should remove it in production.
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return xmlMapper.writeValueAsString(obj)
    }


    fun write2XMLFile(obj: Any, pathFile: String) {

        val xmlMapper = XmlMapper()
        // use the line of code for pretty-print XML on console. We should remove it in production.
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT)

        xmlMapper.writeValue(File(pathFile), obj)
    }
*/

    fun getFichier(nom: String): File {

        val file = File(Environment.getExternalStorageDirectory().toString() + "/$nom" )

        return file
    }


    fun updateFile (myExternalFile : File , jsonString : String) {

        try {
            val fileOutPutStream = FileOutputStream(myExternalFile)
            fileOutPutStream.write(jsonString.toByteArray())
            fileOutPutStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Log.e("adding intervention" , getInterventions().toString())

    }



}


