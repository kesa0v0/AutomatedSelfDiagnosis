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

        vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

//        System.setProperty("webdriver.chrome.driver", "\bD:\\program\\selendroid\\chromedriver.exe")
//        println("property" + System.getProperty("webdriver.chrome.driver"))

        val timeView = findViewById<TextView>(R.id.currentTime)
        val yes = findViewById<Button>(R.id.yesSymptom)
        val no = findViewById<Button>(R.id.noSymptom)

        timeView.text = intent.getStringExtra("time")
        vib.vibrate(VibrationEffect.createWaveform(longArrayOf(500, 1000, 500, 2000), 0))

        yes.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(browserIntent)

            vib.cancel()

            finish()
        }

        no.setOnClickListener {
            DoAsync {
                val get = GettingStarted()
                get.testGoogleSearch()
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                startActivity(browserIntent)
            }.execute()

            vib.cancel()

            finish()
        }
    }

    override fun finish() {
        vib.cancel()

        super.finish()
    }
}

class DoAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}

class GettingStarted {
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