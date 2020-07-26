package com.kesa.automatedselfdiagnosis

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions


class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

//        System.setProperty("webdriver.chrome.driver", "\bD:\\program\\selendroid\\chromedriver.exe")
//        println("property" + System.getProperty("webdriver.chrome.driver"))

        val timeView = findViewById<TextView>(R.id.currentTime)
        val yes = findViewById<Button>(R.id.yesSymptom)
        val no = findViewById<Button>(R.id.noSymptom)

        timeView.text = intent.getStringExtra("time")

        yes.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://eduro.dge.go.kr/stv_cvd_co00_000.do?k=3xi5m3BNzatDvJ%2FX3nZguA%3D%3D"))
            startActivity(browserIntent)
        }

        no.setOnClickListener {
            DoAsync {
//                val get = GettingStarted()
//                get.testGoogleSearch()
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://eduro.dge.go.kr/stv_cvd_co00_000.do?k=3xi5m3BNzatDvJ%2FX3nZguA%3D%3D"))
                startActivity(browserIntent)
            }.execute()
        }
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
        val chromeOptions = ChromeOptions()
        chromeOptions.setExperimentalOption("androidPackage", "com.android.chrome")
        chromeOptions.setExperimentalOption("androidDeviceSerial", "PIXEL 3")
        System.setProperty("webdriver.chrome.driver", "\bC:\\selendroid\\chromedriver.exe")
        val driver: WebDriver = ChromeDriver(chromeOptions)
        driver["http://www.google.com/"]
        Thread.sleep(5000) // Let the user actually see something!
        val searchBox = driver.findElement(By.name("q"))
        searchBox.sendKeys("ChromeDriver")
        searchBox.submit()
        Thread.sleep(5000) // Let the user actually see something!
        driver.quit()
    }
}