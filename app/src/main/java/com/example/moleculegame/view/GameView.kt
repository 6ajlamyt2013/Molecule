package com.example.moleculegame.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.moleculegame.GameScreenActivity
import com.example.moleculegame.model.Molecule
import kotlin.math.sqrt
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {

    private var thread: Thread? = null
    private var running = false
    lateinit var canvas: Canvas
    private val surfaceHolder = holder
    val player: Molecule
    private val otherMolecules = mutableListOf<Molecule>()
    private val maxOtherMolecules = 5
    private var touchX = 0f
    private var touchY = 0f
    private var gameOver = false

    init {
        surfaceHolder.addCallback(this)
        player = Molecule(0f, 0f, 20, Color.RED) // Начальный вес игрока 20
        player.updateSpeed(player.weight)

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        player.x = width / 2f
        player.y = height / 2f
        startGame()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Не используется, но нужно реализовать по интерфейсу
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGame()
    }
    override fun run() {
        while (running) {
            if (surfaceHolder.surface.isValid) {
                canvas = surfaceHolder.lockCanvas()
                canvas.drawColor(Color.WHITE) // Фон

                // Перемещение игрока
                movePlayerTowardsTouch()

                // Логика игры
                updateGameLogic()

                // Отрисовка
                player.draw(canvas)
                for (molecule in otherMolecules) {
                    molecule.draw(canvas)
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
            if (gameOver) {
                // Завершить игру на текущем экране
                (context as GameScreenActivity).runOnUiThread {
                    (context as GameScreenActivity).showResultScreen()
                }
                return
            }
        }
    }
    private fun updateGameLogic() {
        // Перемещение других молекул
        for (molecule in otherMolecules) {
            molecule.move()
            // Отражение от границ
            if (molecule.x - molecule.radius < 0 || molecule.x + molecule.radius > width) {
                molecule.speedX *= -1
            }
            if (molecule.y - molecule.radius < 0 || molecule.y + molecule.radius > height) {
                molecule.speedY *= -1
            }
            //столкновение игрока и молекул
            if (player.collidesWith(molecule)) {
                if (player.weight >= molecule.weight) {
                    player.absorb(molecule)
                    if (molecule.weight == 0){
                        otherMolecules.remove(molecule) // Удаляем поглощенные молекулы
                    }

                } else {
                    gameOver = true
                    running = false // Остановка игры при проигрыше
                }
            }

        }
        // Создание новых молекул, если их меньше максимального количества
        if (otherMolecules.size < maxOtherMolecules) {
            generateRandomMolecule()
        }

    }

    // Плавное перемещение игрока к точке касания
    private fun movePlayerTowardsTouch() {
        val dx = touchX - player.x
        val dy = touchY - player.y
        val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

        if (distance > 10) { // Небольшой порог, чтобы избежать дрожания
            val speedFactor = player.maxSpeed / distance
            player.x += dx * speedFactor
            player.y += dy * speedFactor

            // Ограничение движения игрока пределами экрана
            player.x = player.x.coerceAtLeast(player.radius).coerceAtMost(width - player.radius)
            player.y = player.y.coerceAtLeast(player.radius).coerceAtMost(height - player.radius)
        }
    }

    // Обработка касаний экрана
    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Обработка перемещения игрока
            }
            // Другие действия, если нужны (например, пауза при касании двумя пальцами)
        }
        return true
    }

    fun startGame() {
        running = true
        gameOver = false
        thread = Thread(this)
        thread?.start()
    }

    fun stopGame() {
        running = false
        try {
            thread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
    fun pause() {
        running = false
        try {
            thread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume() {
        running = true
        thread = Thread(this)
        thread?.start()
    }

    // Генерация случайной молекулы
    private fun generateRandomMolecule() {
        val weight = Random.nextInt(3, 10) // Вес от 5 до 40 (нужно настроить диапазон)
        val radius = Molecule(0f,0f,weight).calculateRadius(weight)
        // Проверка наложения с другими молекулами
        var x:Float
        var y:Float
        var isOverlapping:Boolean
        do {
            x = Random.nextFloat() * (width - 2 * radius) + radius
            y = Random.nextFloat() * (height - 2 * radius) + radius
            // Проверяем, не перекрывает ли новая молекула уже существующие
            isOverlapping = otherMolecules.any { existingMolecule ->
                val distance =
                    sqrt(((x - existingMolecule.x) * (x - existingMolecule.x) + (y - existingMolecule.y) * (y - existingMolecule.y)).toDouble()).toFloat()
                distance < (radius + existingMolecule.radius)
            }
        }while(isOverlapping)
        // Случайное направление ботов
        val speedX = (Random.nextFloat() - 0.5f) * 6 // Скорость от -3 до 3
        val speedY = (Random.nextFloat() - 0.5f) * 6
        // Случайный цвет
        val color = Color.argb(
            255,
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        )
        val molecule = Molecule(x, y, weight, color)
        molecule.speedX = speedX
        molecule.speedY = speedY
        molecule.updateSpeed(molecule.weight)
        otherMolecules.add(molecule)
    }

}