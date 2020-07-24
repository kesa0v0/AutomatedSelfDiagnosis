package com.kesa.automatedselfdiagnosis

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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


        picker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }

            val currentDateTime = calendar.time
            val dateText =
                SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault())
                    .format(currentDateTime)
            Toast.makeText(
                applicationContext,
                dateText + "으로 알람이 설정되었습니다!",
                Toast.LENGTH_SHORT
            ).show()

            //  Preference 에 설정한 값 저장
            val editor = getSharedPreferences("daily alarm", Context.MODE_PRIVATE).edit()
            editor.putLong("nextNotifyTime", calendar.timeInMillis)
            editor.apply()


            diaryNotification(calendar)
        }
    }

    fun diaryNotification(calendar: Calendar) {
        val activated = findViewById<Switch>(R.id.activated)
        val dailyNotify = activated.isChecked

        val pm = this.packageManager
        val receiver = ComponentName(this, DeviceBootReceiver::class.java)
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (dailyNotify) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

        } else { //Disable Daily Notifications
            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null) {
                alarmManager.cancel(pendingIntent)
                Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show()
            }
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP)
        }
    }
}
