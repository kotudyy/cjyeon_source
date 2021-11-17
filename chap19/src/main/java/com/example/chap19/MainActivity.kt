package com.example.chap19

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    lateinit var providerClient: FusedLocationProviderClient
    lateinit var apiClient: GoogleApiClient
    var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?)!!.getMapAsync(this)
        providerClient = LocationServices.getFusedLocationProviderClient(this)
        apiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_NETWORK_STATE) !== PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE),
                    100
                )
        } else {
            //위치 제공자 준비
            apiClient.connect()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 &&
                grantResults[0] === PackageManager.PERMISSION_GRANTED &&
                grantResults[1] === PackageManager.PERMISSION_GRANTED &&
                grantResults[2] === PackageManager.PERMISSION_GRANTED) {
                    apiClient.connect()
        }
    }

    private fun moveMap(latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(16f)
            .build()

        //지도 중심 이동하기
        googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(position))

        //마커 옵션
        val markerOptions = MarkerOptions()
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        markerOptions.position(latLng)
        markerOptions.title("MyLocation")

        //마커 표시하시
        googleMap?.addMarker(markerOptions)
    }

    //위치 제공자 사용 가능한 상황에서 실행되는 함수
    override fun onConnected(p0: Bundle?) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        === PackageManager.PERMISSION_GRANTED) {
            providerClient.lastLocation.addOnSuccessListener (
                this@MainActivity,
            object : OnSuccessListener<Location> {
                override fun onSuccess(location: Location?) {
                    location?.let {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        //지도 중심 이동하기
                        moveMap(latitude, longitude)
                    }
                }
            })
            apiClient.disconnect()
        }
    }

    //위치 제공자 사용할 수 없는 경우
    override fun onConnectionSuspended(p0: Int) {
    }

    //사용할 수 있는 위치 제공자가 없는 경우
    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    //지도 객체를 이용할 수 있는 상황이 된 경우
    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
    }
}