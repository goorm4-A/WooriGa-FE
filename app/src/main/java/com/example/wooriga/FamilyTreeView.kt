package com.example.wooriga

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView



class FamilyTreeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    val members = mutableListOf<FamilyMember>()
    private val memberViews = mutableMapOf<FamilyMember, View>()

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private var container: ViewGroup? = null

    private var centerX = 0f// 고정 중심 변수

    private var isInitialized = false
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = width / 2f // 화면의 가로 중앙을 고정 중심으로 설정

        if (!isInitialized && members.isNotEmpty()) {
            layoutMembers()
            addMemberViews(context)
            invalidate()
            isInitialized = true
        }
    }

    fun setContainer(container: ViewGroup) {
        this.container = container
    }

    fun initializeTree(context: Context) {
        members.clear()
        memberViews.clear()
        container?.removeAllViews()

        // 기본 구성원
        val me = FamilyMember("", "나", "")
        val father = FamilyMember("", "아버지", "")
        val mother = FamilyMember("", "어머니", "")
        val pGrandFather = FamilyMember("", "친할아버지", "")
        val pGrandMother = FamilyMember("", "친할머니", "")
        val mGrandFather = FamilyMember("", "외할아버지", "")
        val mGrandMother = FamilyMember("", "외할머니", "")

        members.addAll(listOf(pGrandFather, pGrandMother, father, mGrandFather, mGrandMother, mother, me))
        isInitialized = false  // <- onSizeChanged에서 초기화되도록

    }

    fun addMember(context: Context, member: FamilyMember) {
        members.add(member)             // 리스트에 추가
        layoutMembers()                 // 모든 구성원의 위치 재배치
        addMemberViews(context)        // 새 멤버는 새 View 추가, 기존은 위치만 갱신
        invalidate()                    // 선 다시 그림
    }

    internal fun addMemberViews(context: Context) {

        container?.removeAllViews()
        for (member in members) {

            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_home_tree_member, container, false)
            view.setOnClickListener {
                (context as? FamilyTreeActivity)?.showEditFamilyMemberDialog(member)
            }
            view.findViewById<TextView>(R.id.name).text = member.name
            view.findViewById<TextView>(R.id.relation).text = member.relation
            view.findViewById<TextView>(R.id.birth).text = member.birth

            val width = 202
            val height = 300
            view.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
            view.layout(0, 0, width, height)

            view.x = member.x
            view.y = member.y

            container?.addView(view)
            memberViews[member] = view
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        fun centerOf(member: FamilyMember): PointF {
            return PointF(member.x + 100f, member.y + 80f)
        }

        val me = members.find { it.relation == "나" }
        val father = members.find { it.relation == "아버지" }
        val mother = members.find { it.relation == "어머니" }

        // 부모 수평선 + 부모 중앙 → 나
        if (father != null && mother != null && me != null) {
            val fc = centerOf(father)
            val mc = centerOf(mother)
            val child = centerOf(me)
            val midX = (fc.x + mc.x) / 2

            canvas.drawLine(fc.x, fc.y, mc.x, mc.y, paint) // 부모끼리 수평선
            canvas.drawLine(midX, fc.y, midX, child.y, paint) // 부모 중앙 → 자식 수직
            canvas.drawLine(midX, child.y, child.x, child.y, paint) // 자식 좌표로
        }

        // 조부모 → 부모 연결
        drawParents(canvas, "친할아버지", "친할머니", father)
        drawParents(canvas, "외할아버지", "외할머니", mother)

        // 할머니, 할아버지의 y값
        val grandParent = members.find { it.relation == "외할아버지" }


        // 엄마 형제자매 연결 (이모, 외삼촌)
        val motherSiblings = members.filter { it.relation in listOf("이모", "외삼촌") }
        for (sibling in motherSiblings) {
            val from = centerOf(sibling)
            val to = centerOf(mother ?: continue)
            if (grandParent != null) {
                canvas.drawLine(from.x, from.y, to.x, grandParent.y+ 100f, paint)
            }
        }

        // 아빠 형제자매 연결 (고모, 큰아버지, 작은아버지)
        val fatherSiblings = members.filter { it.relation in listOf("고모", "큰아버지", "작은아버지") }
        for (sibling in fatherSiblings) {
            val from = centerOf(sibling)
            val to = centerOf(father ?: continue)
            if (grandParent != null) {
                canvas.drawLine(from.x, from.y, to.x, grandParent.y+ 100f, paint)
            }
        }

        // 형제자매 → 부모 중앙 → 연결
        val siblings = members.filter {
            it.relation in listOf("오빠/형", "언니/누나", "여동생", "남동생")
        }
        if (father != null && mother != null) {
            val fc = centerOf(father)
            val mc = centerOf(mother)
            val midX = (fc.x + mc.x) / 2
            for (sibling in siblings) {
                val child = centerOf(sibling)
                canvas.drawLine(midX, fc.y, midX, child.y, paint)
                canvas.drawLine(midX, fc.y, child.x, child.y, paint)
            }
        }
    }

    private fun drawParents(canvas: Canvas, relation1: String, relation2: String, child: FamilyMember?) {
        val parent1 = members.find { it.relation == relation1 }
        val parent2 = members.find { it.relation == relation2 }
        if (parent1 != null && parent2 != null && child != null) {
            val p1 = PointF(parent1.x + 100f, parent1.y + 100f)
            val p2 = PointF(parent2.x + 100f, parent2.y + 100f)
            val c = PointF(child.x + 100f, child.y + 100f)
            val midX = (p1.x + p2.x) / 2
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
            canvas.drawLine(midX, p1.y, midX, c.y, paint)
            canvas.drawLine(midX, c.y, c.x, c.y, paint)
        }
    }

    internal fun layoutMembers() {
        val spacingY = 400f
        val spacingX = 300f
        val extraSpacing = 200f

        val me = members.find { it.relation == "나" }
        me?.x = centerX
        me?.y = height / 2f


        val father = members.find { it.relation == "아버지" }
        val mother = members.find { it.relation == "어머니" }

        father?.x = centerX - spacingX - extraSpacing
        father?.y = (me?.y ?: 0f) - spacingY
        mother?.x = centerX + spacingX + extraSpacing
        mother?.y = (me?.y ?: 0f) - spacingY

        // 조부모
        members.find { it.relation == "친할아버지" }?.apply {
            x = (father?.x ?: centerX) - spacingX
            y = (father?.y ?: 0f) - spacingY
        }
        members.find { it.relation == "친할머니" }?.apply {
            x = (father?.x ?: centerX) + spacingX
            y = (father?.y ?: 0f) - spacingY
        }
        members.find { it.relation == "외할아버지" }?.apply {
            x = (mother?.x ?: centerX) - spacingX
            y = (mother?.y ?: 0f) - spacingY
        }
        members.find { it.relation == "외할머니" }?.apply {
            x = (mother?.x ?: centerX) + spacingX
            y = (mother?.y ?: 0f) - spacingY
        }

        // 형제자매
        val siblings = members.filter {
            it.relation in listOf("오빠/형", "언니/누나", "여동생", "남동생")
        }
        var siblingIndex = 1
        for ((i, member) in siblings.withIndex()) {
            member.y = me?.y ?: 0f
            member.x = (me?.x ?: centerX) + if (i % 2 == 0) siblingIndex * spacingX else -siblingIndex * spacingX
            if (i % 2 == 1) siblingIndex++
        }

        // 엄마 형제자매 (오른쪽)
        val motherSiblings = members.filter {
            it.relation in listOf("이모", "외삼촌")
        }
        for ((i, member) in motherSiblings.withIndex()) {
            member.y = mother?.y ?: 0f
            member.x = (mother?.x ?: centerX) + (i + 1) * extraSpacing
        }

        // 아빠 형제자매 (왼쪽)
        val fatherSiblings = members.filter {
            it.relation in listOf("고모", "큰아버지", "작은아버지")
        }
        for ((i, member) in fatherSiblings.withIndex()) {
            member.y = father?.y ?: 0f
            member.x = (father?.x ?: centerX) - (i + 1) * extraSpacing
        }
    }
}
