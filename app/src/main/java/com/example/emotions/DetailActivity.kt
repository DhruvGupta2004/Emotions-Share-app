package com.example.emotions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.emotions.R

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val heading = intent.getStringExtra("heading")
        val nContent = intent.getStringExtra("content")

        val headingTV = findViewById<TextView>(R.id.Heading)
        val ContentTV = findViewById<TextView>(R.id.Content)

        headingTV.text = heading
        ContentTV.text = nContent
    }
}