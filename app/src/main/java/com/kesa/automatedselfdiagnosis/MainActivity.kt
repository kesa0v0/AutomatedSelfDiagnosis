package com.kesa.automatedselfdiagnosis

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button



var alertHour:Int = 0
var alertMin:Int = 0
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSetTime = findViewById<Button>(R.id.setTime)

        btnSetTime.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    alertHour = hourOfDay
                    alertMin = minute

                    btnSetTime.text = "${alertHour.toString()} : ${alertMin.toString()}"
                }, 0, 0, true
            )
            timePickerDialog.setMessage("메시지")
            timePickerDialog.show()
        }
    }
}
