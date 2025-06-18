package com.example.wooriga

import android.content.Context
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan


object CalendarDecorators {
    fun eventDecorator(context: Context, eventDates: Set<CalendarDay>): DayViewDecorator =
        object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean = eventDates.contains(day)
            override fun decorate(view: DayViewFacade) {
                view.addSpan(DotSpan(10f, ContextCompat.getColor(context, R.color.green)))
            }
        }
}