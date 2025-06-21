// ToolbarUtils.kt
package com.example.wooriga.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import com.example.wooriga.ApiResponse
import com.example.wooriga.R
import com.example.wooriga.RetrofitClient2
import com.example.wooriga.model.FamilyGroupResponse
import com.example.wooriga.model.FamilyGroupWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ToolbarUtils {

    val groupList = mutableListOf<FamilyGroupWrapper>()

    // 현재 선택된 그룹 (nullable)
    var currentGroup: FamilyGroupWrapper? = null

    // 가족 그룹 리스트를 서버에서 받아오는 함수
    fun fetchFamilyGroupsFromServer(onComplete: () -> Unit = {}) {
        RetrofitClient2.familyGroupApi.getGroups().enqueue(object :
            Callback<ApiResponse<List<FamilyGroupWrapper>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<FamilyGroupWrapper>>>,
                response: Response<ApiResponse<List<FamilyGroupWrapper>>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        groupList.clear()
                        groupList.addAll(body.result)
                        onComplete() // 데이터 받아온 후 콜백 호출
                    } else {
                        Log.e("ToolbarUtils", "서버 응답 실패")
                    }
                } else {
                    Log.e("ToolbarUtils", "서버 오류")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<FamilyGroupWrapper>>>, t: Throwable) {
                Log.e("ToolbarUtils", "네트워크 오류", t)
            }
        })
    }

    // 툴바 아이콘 클릭 리스너 설정
    fun setupFamilyGroupIcon(view: View, context: Context, onSelected: (FamilyGroupWrapper) -> Unit) {
        val icon = view.findViewById<ImageView>(R.id.icon_select_family)
        icon.setOnClickListener {
            // 메뉴를 띄우기 전에 최신 데이터가 있어야 하므로 fetch 후 메뉴 띄우기
            fetchFamilyGroupsFromServer {
                showFamilyGroupMenu(it, context, groupList, onSelected)
            }
        }
    }

    fun showFamilyGroupMenu(
        anchor: View,
        context: Context,
        groupList: List<FamilyGroupWrapper>,
        onSelected: (FamilyGroupWrapper) -> Unit
    ) {
        val popupMenu = PopupMenu(context, anchor)

        groupList.forEachIndexed { index, groupWrapper ->
            popupMenu.menu.add(0, index, index, groupWrapper.familyGroup.familyName)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            val selectedGroup = groupList[item.itemId]
            currentGroup = selectedGroup  // 전역 변수 갱신
            onSelected(selectedGroup)
            true
        }

        popupMenu.show()
    }
}
