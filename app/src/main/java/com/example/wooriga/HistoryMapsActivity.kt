package com.example.wooriga

import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.wooriga.databinding.ActivityHistoryMapsBinding
import com.example.wooriga.databinding.BottomSheetHistoryMapBinding
import com.example.wooriga.model.History
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.LocalDate
import java.util.Locale

class HistoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHistoryMapsBinding
    private lateinit var geocoder: Geocoder

    // 테스트용 데이터 (나중에 서버에서 받아올 부분)
    @RequiresApi(Build.VERSION_CODES.O)
    private val historyList = listOf(
        History(
            family = "A 가족",
            dateString = "2022.05.03",
            dateObject = LocalDate.of(2022, 5, 3),
            title = "할머니 댁 방문",
            locationName = "서울특별시 종로구 청운동",
            latitude = 37.5826,
            longitude = 126.9749
        ),
        History(
            family = "A 가족",
            dateString = "2023.09.12",
            dateObject = LocalDate.of(2023, 9, 12),
            title = "여행 추억",
            locationName = "부산 해운대",
            latitude = 35.1587,
            longitude = 129.1604
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상단바의 < 뒤로가기 버튼 클릭 시 이전 액티비티로 이동
        binding.historyMapToolbar.backButton.setOnClickListener {
            finish()
        }

        geocoder = Geocoder(this, Locale.getDefault())


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.historyMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 초기 위치: 서울
        val seoul = LatLng(37.5665, 126.978)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10f))

        // 지도에 마커 추가
        for (history in historyList) {
            val position = LatLng(history.latitude, history.longitude)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(history.title)
            )
            marker?.tag = history  // 마커에 해당 히스토리 객체 저장

        }

        // 마커 클릭 이벤트
        mMap.setOnMarkerClickListener { marker ->
            val history = marker.tag as? History
            history?.let {
                showHistoryBottomSheet(it)
            }
            true
        }

    }


    private fun showHistoryBottomSheet(history: History) {
        val dialog = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetHistoryMapBinding.inflate(LayoutInflater.from(this))

        bottomSheetBinding.textViewTitle.text = history.title
        bottomSheetBinding.textViewDate.text = history.dateString
        bottomSheetBinding.textViewLocation.text = history.locationName
        bottomSheetBinding.textViewFamily.text = history.family

        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()
    }
}

