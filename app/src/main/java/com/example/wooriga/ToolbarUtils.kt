// ToolbarUtils.kt
package com.example.wooriga.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import com.example.wooriga.R
import com.example.wooriga.model.FamilyGroup

object ToolbarUtils {

    val groupList = mutableListOf<FamilyGroup>()

    init {
        groupList.add(FamilyGroup(R.drawable.ic_family, "가족G", 3))
        groupList.add(FamilyGroup(R.drawable.ic_family, "가족Q", 7))
        groupList.add(FamilyGroup(R.drawable.ic_family, "가족P", 4))
    }

    // 툴바 아이콘 클릭 리스너 설정
    fun setupFamilyGroupIcon(view: View, context: Context, groupList: List<FamilyGroup>, onSelected: (FamilyGroup) -> Unit) {
        val icon = view.findViewById<ImageView>(R.id.icon_select_family)
        icon.setOnClickListener {
            showFamilyGroupMenu(it, context, groupList, onSelected)
        }
    }

    fun showFamilyGroupMenu(
        anchor: View,
        context: Context,
        groupList: List<FamilyGroup>,
        onSelected: (FamilyGroup) -> Unit
    ) {
        val popupMenu = PopupMenu(context, anchor)

        groupList.forEachIndexed { index, group ->
            popupMenu.menu.add(0, index, index, group.title)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            val selectedGroup = groupList[item.itemId]
            onSelected(selectedGroup)
            true
        }

        popupMenu.show()
    }
}

