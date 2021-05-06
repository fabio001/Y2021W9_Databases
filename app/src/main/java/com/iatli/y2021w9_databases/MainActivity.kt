package com.iatli.y2021w9_databases

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*

class MainActivity : AppCompatActivity() {
    val DB_NAME:String = "BABYNAMESDB"
    lateinit var graphView: GraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        load_database()
        graphView = findViewById<GraphView>(R.id.graph)
        graphView.viewport.setMinX(2000.0);
        graphView.viewport.setMaxX(2020.0)
        graphView.viewport.setMinY(0.0)
        graphView.viewport.setMaxY(2000.0)

    }

    fun plot_babynames(view: View) {
        val name = findViewById<EditText>(R.id.edt_babynames).text.toString()
        val query= "SELECT year, count FROM babynames WHERE name='$name'"


        //opendb
        val db= openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null)

        //execute query and process data
        val cursor: Cursor = db.rawQuery(query, null)
        //add data series
        val series = LineGraphSeries<DataPoint>()

        while(cursor.moveToNext()){
            val year = cursor.getInt(cursor.getColumnIndex("year"))
            val counts = cursor.getInt(cursor.getColumnIndex("count"))
            series.appendData(DataPoint(year.toDouble(), counts.toDouble()), false, 1000)
        }

        cursor.close()
        //remove series in graph if exist and update new values
        graphView.removeAllSeries()
        graphView.addSeries(series)
    }


    private fun load_database(){
        val db: SQLiteDatabase = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null)
        val raw_db_id = resources.getIdentifier("babynames", "raw", packageName)

        val scanner = Scanner(resources.openRawResource(raw_db_id))

        //read all file and import
        var query = ""
        var i=0
        while(scanner.hasNext()){
            i++
//            if(i%100==0)
//                Log.d("DBDB", "$i")

            query+= scanner.nextLine() + "\n";
            if(query.trim().endsWith(";")){
                db.execSQL(query);
                Log.d("DBDB",query);
                query="";
            }
        }
    }

}