package com.example.moleculegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ResultScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_screen)

        val playerWeight = intent.getIntExtra("playerWeight", 0)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        resultTextView.text = "Your weight: $playerWeight"

        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, StartScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Очистка стека активностей
            startActivity(intent)
        }
    }
}