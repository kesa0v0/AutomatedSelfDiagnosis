package com.kesa.automatedselfdiagnosis

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import org.openqa.selenium.support.ui.WebDriverWait


class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val timeView = findViewById<TextView>(R.id.currentTime)
        val yes = findViewById<Button>(R.id.yesSymptom)
        val no = findViewById<Button>(R.id.noSymptom)

        timeView.text = intent.getStringExtra("time")

        yes.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://eduro.dge.go.kr/hcheck/index.jsp"))
            startActivity(browserIntent)
        }

        no.setOnClickListener {
            val driver = ChromeDriver()
            val wait = WebDriverWait(driver, 10)
            try {
                driver.get("https://google.com/ncr")
            } finally {
                driver.quit()
            }
        }
    }
}