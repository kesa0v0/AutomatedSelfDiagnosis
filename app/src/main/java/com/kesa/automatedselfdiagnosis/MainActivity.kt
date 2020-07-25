package com.kesa.automatedselfdiagnosis

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val activeSwitch = findViewById<Switch>(R.id.activated)
        val picker = findViewById<TimePicker>(R.id.timePicker)
        picker.setIs24HourView(true)

        val sharedPreferences =
            getSharedPreferences("selfDiagnosis", Context.MODE_PRIVATE)
        val millis =
            sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().timeInMillis)

        activeSwitch.isChecked = sharedPreferences.getBoolean("switchStatus", false)
        val nextNotifyTime: Calendar = GregorianCalendar()
        nextNotifyTime.timeInMillis = millis

        // 이전 설정값으로 TimePicker 초기화
        val currentTime = nextNotifyTime.time
        val hourFormat =
            SimpleDateFormat("kk", Locale.getDefault())
        val minuteFormat =
            SimpleDateFormat("mm", Locale.getDefault())

        val preHour = hourFormat.format(currentTime).toInt()
        val preMinute = minuteFormat.format(currentTime).toInt()

        picker.hour = preHour
        picker.minute = preMinute

        val calendar = Calendar.getInstance()

        picker.setOnTimeChangedListener { _, hourOfDay, minute ->
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }
            //  Preference 에 설정한 값 저장
            val editor = getSharedPreferences("selfDiagnosis", Context.MODE_PRIVATE).edit()
            editor.putLong("nextNotifyTime", calendar.timeInMillis)
            editor.apply()
        }


        val pm = this.packageManager
        val receiver = ComponentName(this, DeviceBootReceiver::class.java)
        val alarmIntent = Intent(this, AlarmActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        activeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 60000, pendingIntent
                )

                // 부팅 후 실행되는 리시버 사용가능하게 설정
                pm.setComponentEnabledSetting(
                    receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
            else {
                alarmManager.cancel(pendingIntent)
            }
            sharedPreferences.edit().putBoolean("switchStatus", isChecked).apply()
        }
    }
}
