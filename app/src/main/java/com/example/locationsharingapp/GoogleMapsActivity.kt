package com.example.locationsharingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.locationsharingapp.databinding.ActivityGoogleMapsBinding
import com.google.firebase.firestore.FirebaseFirestore

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityGoogleMapsBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Users on Map"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        loadUsersOnMap()
    }

    private fun loadUsersOnMap() {
        db.collection("AppUsers")
            .get()
            .addOnSuccessListener { snapshot ->
                var firstLocation: LatLng? = null

                snapshot.documents.forEach { doc ->
                    val user = doc.toObject(AppUser::class.java)
                    user?.let {
                        val position = LatLng(it.latitude, it.longitude)
                        if (firstLocation == null) {
                            firstLocation = position
                        }

                        googleMap.addMarker(
                            MarkerOptions()
                                .position(position)
                                .title(it.displayName ?: "No Name")
                                .snippet(it.userEmail)
                        )
                    }
                }

                firstLocation?.let {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 10f))
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading users: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}