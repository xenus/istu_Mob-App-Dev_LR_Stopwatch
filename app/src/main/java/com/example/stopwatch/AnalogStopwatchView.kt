package com.example.stopwatch

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class AnalogStopwatchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    // Время в миллисекундах
    var timeMillis: Long = 0L
        set(value) {
            field = value
            invalidate() // Перерисовываем View при изменении времени
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = minOf(w, h) / 2f * 0.8f // Радиус циферблата
        rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Рисуем циферблат
        drawDial(canvas)

        // Рисуем стрелки
        drawHands(canvas)
    }

    private fun drawDial(canvas: Canvas) {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        canvas.drawCircle(centerX, centerY, radius, paint)

        // Рисуем деления
        paint.strokeWidth = 3f
        for (i in 0 until 60) {
            val angle = Math.toRadians(i * 6.0) // 360 градусов / 60 = 6 градусов на деление
            val startX = centerX + (radius * 0.9f * cos(angle)).toFloat()
            val startY = centerY + (radius * 0.9f * sin(angle)).toFloat()
            val stopX = centerX + (radius * cos(angle)).toFloat()
            val stopY = centerY + (radius * sin(angle)).toFloat()
            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }
    }

    private fun drawHands(canvas: Canvas) {
        val seconds = (timeMillis / 1000) % 60
        val minutes = (timeMillis / 60000) % 60
        val milliseconds = (timeMillis % 1000) / 10

        // Рисуем секундную стрелку
        paint.color = Color.RED
        paint.strokeWidth = 4f
        val secondAngle = Math.toRadians(seconds * 6.0) // 360 градусов / 60 = 6 градусов на секунду
        drawHand(canvas, secondAngle, radius * 0.8f)

        // Рисуем минутную стрелку
        paint.color = Color.BLUE
        paint.strokeWidth = 6f
        val minuteAngle = Math.toRadians(minutes * 6.0) // 360 градусов / 60 = 6 градусов на минуту
        drawHand(canvas, minuteAngle, radius * 0.6f)

        // Рисуем миллисекундную стрелку
        paint.color = Color.GREEN
        paint.strokeWidth = 2f
        val millisAngle = Math.toRadians(milliseconds * 3.6) // 360 градусов / 100 = 3.6 градусов на 10 мс
        drawHand(canvas, millisAngle, radius * 0.4f)
    }

    private fun drawHand(canvas: Canvas, angle: Double, length: Float) {
        val endX = centerX + (length * cos(angle)).toFloat()
        val endY = centerY + (length * sin(angle)).toFloat()
        canvas.drawLine(centerX, centerY, endX, endY, paint)
    }
}