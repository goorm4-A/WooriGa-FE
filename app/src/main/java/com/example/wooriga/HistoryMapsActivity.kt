package com.example.wooriga

import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.wooriga.databinding.ActivityHistoryMapsBinding
import com.example.wooriga.databinding.BottomSheetHistoryMapBinding
import com.example.wooriga.model.History
import com.example.wooriga.utils.ToolbarUtils
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale


// 지도에서 가족사 모아보기
class HistoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHistoryMapsBinding
    private lateinit var geocoder: Geocoder

    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory())[HistoryViewModel::class.java]

        // 상단바의 < 뒤로가기 버튼 클릭 시 이전 액티비티로 이동
        binding.historyMapToolbar.backButton.setOnClickListener {
            finish()
        }

        geocoder = Geocoder(this, Locale.getDefault())

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.historyMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 모든 가족사 요청
        val groups = ToolbarUtils.groupList.map { it.familyGroup }
        viewModel.getAllFamilyMapEvents(groups)


        // LiveData 관찰 - 지도 준비 전에 미리 해두기
        viewModel.historyList.observe(this) { historyList ->
            // 구글맵 준비됐으면 마커 찍기
            viewModel.allMapEvents.observe(this) { list ->
                for (item in list) {
                    val lat = item.history.latitude ?: continue
                    val lng = item.history.longitude ?: continue
                    val position = LatLng(lat, lng)

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
                    marker?.tag = item.history
                }
            }
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 초기 위치: 한국 중심
        val koreaCenter = LatLng(36.5, 127.5)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(koreaCenter, 7f))

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
        return if (index != -1 && index < colors.size) {
            this.getColor(colors[index])
        } else {
            this.getColor(R.color.green)
        }
    }

    private fun getHueFromColor(colorRes: Int): Float {
        val color = getColor(colorRes)
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color, hsv)
        return hsv[0] // Hue 값만 추출
    }
}


