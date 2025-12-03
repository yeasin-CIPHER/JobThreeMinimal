package com.example.locationsharingapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.locationsharingapp.databinding.ActivityMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: AppUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "My Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()

        binding.btnUpdateName.setOnClickListener {
            updateDisplayName()
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("AppUsers").document(userId)
            .get()
            .addOnSuccessListener { document ->
                currentUser = document.toObject(AppUser::class.java)
                currentUser?.let { user ->
                    binding.tvEmail.text = "Email: ${user.userEmail}"
                    binding.tvLatitude.text = "Latitude: ${user.latitude}"
                    binding.tvLongitude.text = "Longitude: ${user.longitude}"
                    binding.etDisplayName.setText(user.displayName ?: "")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDisplayName() {
        val newName = binding.etDisplayName.text.toString().trim()
        val userId = auth.currentUser?.uid ?: return

        if (newName.isEmpty()) {
            Toast.makeText(this, "Please enter a display name", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("AppUsers").document(userId)
            .update("displayName", newName)
            .addOnSuccessListener {
                Toast.makeText(this, "Display name updated", Toast.LENGTH_SHORT).show()
                loadUserProfile()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating name: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
