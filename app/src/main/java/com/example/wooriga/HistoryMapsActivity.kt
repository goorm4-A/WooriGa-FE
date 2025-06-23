package com.example.wooriga

import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.wooriga.databinding.ActivityHistoryMapsBinding
import com.example.wooriga.databinding.BottomSheetHistoryMapBinding
import com.example.wooriga.model.History
import com.example.wooriga.model.HistoryWithFamilyId
import com.example.wooriga.utils.ToolbarUtils
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale


// 지도에서 가족사 모아보기
class HistoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHistoryMapsBinding
    private lateinit var geocoder: Geocoder

    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory(HistoryRepository(RetrofitClient2.historyApi))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 상단바의 < 뒤로가기 버튼 클릭 시 이전 액티비티로 이동
        binding.historyMapToolbar.backButton.setOnClickListener {
            finish()
        }

        geocoder = Geocoder(this, Locale.getDefault())

        // 지도 준비
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.historyMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 모든 가족사 요청
        val groups = ToolbarUtils.groupList.map { it.familyGroup }
        viewModel.getAllFamilyMapEvents(groups)


        // LiveData 옵저버
        viewModel.allMapEvents.observe(this) { list ->
            drawAllMarkersIfReady()
        }

    }



    private var isMapReady = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        isMapReady = true

        // 초기 위치: 한국 중심
        val koreaCenter = LatLng(36.5, 127.5)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(koreaCenter, 7f))

        // 마커 클릭 이벤트
        mMap.setOnMarkerClickListener { marker ->
            val historyWithFamily = marker.tag as? HistoryWithFamilyId
            historyWithFamily?.let {
                showHistoryBottomSheet(it.history, it.familyId)
            }
            true
        }

        drawAllMarkersIfReady()

    }

    private fun drawAllMarkersIfReady() {
        if (!isMapReady) return

        mMap.clear()
        viewModel.allMapEvents.value?.forEach { item ->
            val latitude = item.history.latitude ?: return@forEach
            val longitude = item.history.longitude ?: return@forEach
            val position = LatLng(latitude, longitude)

            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(item.history.title)
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            getHueFromColor(getColorByFamilyId(item.familyId.toInt()))
                        )
                    )
            )
            marker?.tag = item
        }
    }


    private fun showHistoryBottomSheet(history: History, familyId: Long) {
        val dialog = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetHistoryMapBinding.inflate(LayoutInflater.from(this))

        bottomSheetBinding.textViewTitle.text = history.title
        bottomSheetBinding.textViewDate.text = history.dateString
        bottomSheetBinding.textViewLocation.text = history.locationName
        bottomSheetBinding.textViewFamily.text = history.family

        // familycolor 이미지뷰에 familyId에 맞는 색 입히기
        val color = getColorByFamilyId(familyId.toInt())
        bottomSheetBinding.familycolor.setColorFilter(color)

        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()
    }

    private fun getColorByFamilyId(familyId: Int): Int {
        val index = ToolbarUtils.groupList.indexOfFirst { it.familyGroup.familyGroupId.toInt() == familyId }
        val colors = listOf(
            R.color.peach,
            R.color.mint,
            R.color.yellow,
            R.color.red,
            R.color.blue,
            R.color.purple,
            R.color.orange,
            R.color.brown,
        )
        val colorRes = if (index != -1 && index < colors.size) {
            colors[index]
        } else {
            R.color.green
        }
        return ContextCompat.getColor(this, colorRes)
    }

    private fun getHueFromColor(color: Int): Float {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color, hsv)
        return hsv[0]
    }
}


