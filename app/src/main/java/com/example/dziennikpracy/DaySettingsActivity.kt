package com.example.dziennikpracy

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_day_settings.*
import java.text.SimpleDateFormat
import java.util.*

class DaySettingsActivity : AppCompatActivity() {

    private var timestamp: Long = 0
    private var dayInfo: DayInfo = DayInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_day_settings)

        timestamp = intent.getLongExtra("timestamp", 0)
        dayInfo = intent.getParcelableExtra<DayInfo>("dayInfo")!!

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateString = dateFormat.format(timestamp)

        dateTextView.text = dateString
        edit_note.setText(dayInfo.note)

        updateDateColor()
    }

    fun stateButtonsHandler(caller: View) {
        dayInfo.state = Integer.valueOf(caller.tag.toString())
        updateDateColor()
    }

    private fun updateDateColor() {
        var color: Int = ContextCompat.getColor(this@DaySettingsActivity, android.R.color.white)
        when (dayInfo.state) {
            1 -> color = (button_day_free.background as ColorDrawable).color
            2 -> color = (button_day_pay.background as ColorDrawable).color
            3 -> color = (button_day_work.background as ColorDrawable).color
        }
        dateTextView.setTextColor(color)
    }

    fun exitButtonsHandler(caller: View) {
        caller as Button
        if (caller.text.toString() == resources.getString(R.string.save)) {
            saveDataToIntentResult()
        }
        finish()
    }

    private fun saveDataToIntentResult() {
        dayInfo.note = edit_note.text.toString()

        val result = Intent()
        result.putExtra("timestamp", timestamp)
        result.putExtra("dayInfo", dayInfo)
        setResult(Activity.RESULT_OK, result)
    }
}