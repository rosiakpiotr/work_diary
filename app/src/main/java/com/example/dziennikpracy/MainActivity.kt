package com.example.dziennikpracy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.EventDay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private var filename = "calendar_days_details"
    private val editDayInfoRequestCode: Int = 1

    private var daysInfo: HashMap<Long, DayInfo> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadDaysInfo()

        calendarView.setOnDayClickListener { eventDay ->
            val timestamp: Long = eventDay.calendar.timeInMillis
            var selectedDayInfo = DayInfo()
            if (daysInfo.containsKey(timestamp)) {
                selectedDayInfo = daysInfo[timestamp]!!
            }

            val intent = Intent(this@MainActivity, DaySettingsActivity::class.java)
            intent.putExtra("timestamp", timestamp)
            intent.putExtra("dayInfo", selectedDayInfo)
            startActivityForResult(intent, editDayInfoRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == editDayInfoRequestCode) {
            val timestamp: Long = data!!.getLongExtra("timestamp", 0)
            val dayInfo: DayInfo = data.getParcelableExtra<DayInfo>("dayInfo")!!
            daysInfo[timestamp] = dayInfo
        }
    }

    override fun onResume() {
        super.onResume()

        val eventDays: ArrayList<EventDay> = ArrayList()

        for (dayInfo in daysInfo) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = Date(dayInfo.key)
            // Choose right drawable for each day.
            val drawableResource = when (dayInfo.value.state) {
                1 -> R.drawable.beer
                2 -> R.drawable.coin
                3 -> R.drawable.work
                else -> continue
            }
            eventDays.add(EventDay(calendar, drawableResource))
        }

        calendarView.setEvents(eventDays)
    }

    override fun onPause() {
        super.onPause()
        saveDaysInfo()
    }

    private fun saveDaysInfo() {
        applicationContext.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(Gson().toJson(daysInfo).toByteArray())
        }
    }

    private fun loadDaysInfo() {
        try {
            val content =
                applicationContext.openFileInput(filename).bufferedReader().useLines { lines ->
                    lines.fold("") { some, text ->
                        "$some\n$text"
                    }
                }
            val type: Type = object : TypeToken<HashMap<Long, DayInfo>>() {}.type
            daysInfo = Gson().fromJson(content, type)
        } catch (e: FileNotFoundException) {
            Log.e("Loading", e.toString())
        }
    }
}