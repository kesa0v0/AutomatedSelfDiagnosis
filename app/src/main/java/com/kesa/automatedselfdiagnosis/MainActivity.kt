package com.kesa.automatedselfdiagnosis

import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val picker = findViewById<TimePicker>(R.id.timePicker)
        picker.setIs24HourView(true)

        val sharedPreferences = getSharedPreferences("daily alarm", Context.MODE_PRIVATE)
        val milis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().timeInMillis)

        val nextNotifyTime = GregorianCalendar()
        nextNotifyTime.timeInMillis = milis

        val nextDate = nextNotifyTime.time
        val dateText = SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(nextDate)
        Toast.makeText(applicationContext,"[처음 실행시] 다음 알람은 " + dateText + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show()

        val currentTime = nextNotifyTime.time
        val hourFormat = SimpleDateFormat("kk", Locale.getDefault())
        val minuteFormat = SimpleDateFormat("mm", Locale.getDefault())

        val preHour = Integer.parseInt(hourFormat.format(currentTime))
        val preMinute = Integer.parseInt(minuteFormat.format(currentTime))

        picker.hour = preHour
        picker.minute = preMinute
        // Initialize TimePicker with previous settings

    }
}
