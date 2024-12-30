package com.example.moleculegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.moleculegame.view.GameView

class GameScreenActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = GameView(this)
        setContentView(gameView)
    }

    fun showResultScreen() {
        val intent = Intent(this, ResultScreenActivity::class.java)
        intent.putExtra("playerWeight", gameView.player.weight)
        startActivity(intent)
        finish() // Закрываем GameScreenActivity после перехода на экран результатов
    }
    override fun onPause() {
        super.onPause()
        gameView.pause()
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }
}