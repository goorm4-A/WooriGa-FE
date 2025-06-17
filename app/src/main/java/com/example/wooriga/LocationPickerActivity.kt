package com.example.wooriga

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private var selectedLatLng: LatLng? = null
    private var selectedAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapPickerFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geocoder = Geocoder(this, Locale.getDefault())

        findViewById<Button>(R.id.selectLocationButton).setOnClickListener {
            selectedLatLng?.let {
                val resultIntent = Intent().apply {
                    putExtra("latitude", it.latitude)
                    putExtra("longitude", it.longitude)
                    putExtra("address", selectedAddress)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } ?: Toast.makeText(this, "위치를 먼저 선택해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val koreaCenter = LatLng(36.5, 127.5)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(koreaCenter, 7f))

        mMap.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng))

            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                selectedAddress = addressList?.firstOrNull()?.getAddressLine(0) ?: "주소 정보 없음"
            } catch (e: Exception) {
                selectedAddress = "주소 변환 실패"
            }
        }
    }

}
