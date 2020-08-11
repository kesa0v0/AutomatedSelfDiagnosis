package com.kesa.automatedselfdiagnosis

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions


class AlarmActivity : AppCompatActivity() {

    private lateinit var vib : Vibrator 
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val uri = "https://eduro.dge.go.kr/hcheck/index.jsp"

        vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator  // 진동

        val timeView = findViewById<TextView>(R.id.currentTime)
        val yes = findViewById<Button>(R.id.yesSymptom)
        val no = findViewById<Button>(R.id.noSymptom)

        timeView.text = intent.getStringExtra("time")   // 택스트 현재시간으로 변경
        vib.vibrate(VibrationEffect.createWaveform(longArrayOf(500, 1000, 500, 2000), 0))
        // 진동 무한 반복 울리기
        
        yes.setOnClickListener {// 증상 있음(예) 버튼을 누르면
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(browserIntent)

            finish() // 창 닫기
        }

        no.setOnClickListener {// 증상 없음(아니요) 버튼을 누르면
            DoAsync {
                val get = GettingStarted()
                get.testGoogleSearch()
//                val browserIntent =
//                    Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//                startActivity(browserIntent)
                
                // 뭔가....뭔가 아무튼 메크로임 아무튼 그럼 곧 할꺼임

                // 메크로 매크로 아무튼 생각해둔 방안
                // 1. Selendroid 계속 해본다. 단점: 가능함?
                // 2. 화면 터치 메크로? 단점: 화면 바뀌면 못쓰니 사용자가 화면 위치를 등록해야함, 아마도...
                // 3. 파이썬 서버 하나 올려서 앱에서 요청하면 서버에서 selendroid 말고 selenium 으로
                // 작동하는 메크로.

                // 아무튼 그럼 아무튼 그런거임 ㅇㅇ

                //TODO: Make some kind of Macro
            }.execute()
            
            finish() // 창 닫기
        }
    }

    override fun finish() { // 창이 닫기면 (어떤 방식으로든)
        vib.cancel() // 진동 끄기

        super.finish()
    }
}

class DoAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {    // 비동기
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}

class GettingStarted {  // 몬가...몬가 메크로 만들 곳 아마 테스트임 아무튼 그럼
    fun testGoogleSearch() {
        System.setProperty("webdriver.chrome.driver", "C:/selendroid/chromedriver.exe")
        val driver: WebDriver = ChromeDriver()
        driver["http://www.google.com/"]
        Thread.sleep(1000) // Let the user actually see something!
        val searchBox = driver.findElement(By.name("q"))
        searchBox.sendKeys("ChromeDriver")
        searchBox.submit()
        Thread.sleep(10-0) // Let the user actually see something!
        driver.quit()
    }
}