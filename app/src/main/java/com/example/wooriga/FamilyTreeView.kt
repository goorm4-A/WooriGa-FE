package com.example.wooriga

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children

class FamilyTreeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private val linePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 4f
    }

    private val generationMap = mutableMapOf<Int, MutableList<View>>()

    fun addMemberView(view: View, generation: Int) {
        addView(view)
        generationMap.getOrPut(generation) { mutableListOf() }.add(view)
    }

    fun clearAll() {
        generationMap.clear()
        removeAllViews()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val childWidth = 300
        val childHeight = 150
        val genSpacing = 150

        val totalHeight = (generationMap.keys.maxOrNull() ?: 0 + 1) * (childHeight + genSpacing)

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), totalHeight)

        for (child in children) {
            child.measure(
                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY)
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var y = 0
        generationMap.toSortedMap().forEach { (_, views) ->
            val spacing = width / (views.size + 1)
            var x = spacing
            for (v in views) {
                v.layout(x - v.measuredWidth / 2, y, x + v.measuredWidth / 2, y + v.measuredHeight)
                x += spacing
            }
            y += views[0].measuredHeight + 150
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val sortedGens = generationMap.toSortedMap()
        for (i in 0 until sortedGens.size - 1) {
            val current = sortedGens.values.elementAt(i)
            val next = sortedGens.values.elementAt(i + 1)

            for (p1 in current) {
                for (p2 in next) {
                    val x1 = p1.left + p1.width / 2f
                    val y1 = p1.bottom.toFloat()
                    val x2 = p2.left + p2.width / 2f
                    val y2 = p2.top.toFloat()
                    canvas.drawLine(x1, y1, x2, y2, linePaint)
                }
            }
        }
    }
}