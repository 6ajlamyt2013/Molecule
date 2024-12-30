package com.example.moleculegame.model

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.sqrt

class Molecule(var x: Float, var y: Float, var weight: Int, var color: Int = Color.BLUE) {
    var radius: Float = calculateRadius(weight)
    var speedX: Float = 0f
    var speedY: Float = 0f
    var maxSpeed:Float = 5f // Максимальная базовая скорость

    private val paint = Paint().apply {
        this.color = this@Molecule.color
        style = Paint.Style.FILL
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }
    fun move() {
        x += speedX
        y += speedY
    }

    // Обновление максимальной скорости в зависимости от веса
    fun updateSpeed(weight: Int) {
        maxSpeed = 5f + (100f - weight.coerceAtLeast(0)) / 20f // Примерная формула, нужно настроить
    }
    // Поглощение другой молекулы
    fun absorb(other: Molecule) {
        weight += other.weight
        radius = calculateRadius(weight)
        updateSpeed(weight)
        other.weight = 0 // Убираем поглощенную молекулу
    }

    // Передача веса другой молекуле (не используется в текущей логике, но может пригодиться)
    fun transferWeightTo(other: Molecule) {
        val weightToTransfer = weight / 4 // Примерно передаем четверть веса
        other.weight += weightToTransfer
        weight -= weightToTransfer
        radius = calculateRadius(weight)
        other.radius = other.calculateRadius(other.weight)
        updateSpeed(weight)
        other.updateSpeed(other.weight)
    }

    // Расчет радиуса на основе веса
    fun calculateRadius(weight: Int): Float {
        return sqrt(weight.toFloat()) * 5 // Примерная формула для расчета радиуса, нужно настроить
    }
    // Проверка столкновения с другой молекулой
    fun collidesWith(other: Molecule): Boolean {
        val distance = sqrt(((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)).toDouble()).toFloat()
        return distance < (radius + other.radius)
    }
}