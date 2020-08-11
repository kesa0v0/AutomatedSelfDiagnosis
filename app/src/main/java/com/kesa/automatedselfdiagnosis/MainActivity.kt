package com.kesa.automatedselfdiagnosis

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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

        if (!Settings.canDrawOverlays(this)) { // 다른 화면 위에 그리기 권한 요청
            val builder = AlertDialog.Builder(this).apply {
                title = "권한 요청"
                setMessage("다른 화면 위에 그리기라는 권한이 필요합니다.")
                setCancelable(false)
                setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                setPositiveButton("수락") { dialog, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
                    startActivityForResult(intent, 0)
                    dialog.dismiss()
                }
            }.show()
        }

        val activeSwitch = findViewById<Switch>(R.id.activated)
        val picker = findViewById<TimePicker>(R.id.timePicker)
        picker.setIs24HourView(true)

        val sharedPreferences =
            getSharedPreferences("selfDiagnosis", Context.MODE_PRIVATE)
        val millis =
            sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().timeInMillis)
        // 이전에 설정된 시간 불러오기

        activeSwitch.isChecked = sharedPreferences.getBoolean("switchStatus", false)
        // 활성화 되어 있었는지 여부 불러오기
        
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

        picker.setOnTimeChangedListener { _, hourOfDay, minute -> // 시간선택이 변경됬을 때
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1) // 설정된 시간이 현재 시간보다 작으면 다음날로 설정
            }
            //  Preference 에 설정한 값 저장
            val editor = getSharedPreferences("selfDiagnosis", Context.MODE_PRIVATE).edit()
            editor.putLong("nextNotifyTime", calendar.timeInMillis)
            editor.apply()

            activeSwitch.isChecked = false // 스위치 끄기
            sharedPreferences.edit().putBoolean("switchStatus", false).apply() // 스위치 동기화
        }


        val pm = this.packageManager
        val receiver = ComponentName(this, DeviceBootReceiver::class.java)
        val alarmIntent = Intent(this, AlarmActivity::class.java)
        alarmIntent.putExtra("time", SimpleDateFormat("a hh : mm", Locale.getDefault()).format(calendar.time))
        // 알람 설정된 시간 보내기
        val pendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        activeSwitch.setOnCheckedChangeListener { _, isChecked -> 
            if (isChecked) {  // 스위치 체크되면
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent
                )   // 매일 AlarmActivity 열기

                // 부팅 후 실행되는 리시버 사용가능하게 설정
                pm.setComponentEnabledSetting(
                    receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )

                Toast.makeText(this, SimpleDateFormat("a hh시 mm분으로 설정되었습니다", Locale.getDefault()).format(calendar.time), Toast.LENGTH_LONG).show()
            }
            else {  // 스위치 해제되면
                alarmManager.cancel(pendingIntent) // 알람 끄기
                Toast.makeText(this, "해제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            sharedPreferences.edit().putBoolean("switchStatus", isChecked).apply() // 스위치 동기화
        }

        // 디버깅용 버튼 (알람 창 강제로 띄우기)
        val test = findViewById<Button>(R.id.test)
        test.setOnClickListener {
            startActivity(Intent(this, AlarmActivity::class.java)
                .putExtra("time", SimpleDateFormat("a hh : mm", Locale.getDefault()).format(calendar.time)))
        }
    }
}
