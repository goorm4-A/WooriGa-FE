package com.example.wooriga

data class FamilyMember(
    var name: String, // 실제 이름
    var relation: String, // 나와의 관계 - 스피너로 설정 (어머니, 외할아버지, 고모, 외삼촌, 언니/누나 등등)
    var birth: String, // 생년월일 - YYYY-MM-DD 형식
    var x: Float = 0f, // 뷰 위치 배치용 (좌표값)
    var y: Float = 0f,
    val isUserAdded: Boolean = false // 사용자가 추가한 멤버인지 여부 (true면 사용자 추가 멤버, false면 기본 구성원)
)