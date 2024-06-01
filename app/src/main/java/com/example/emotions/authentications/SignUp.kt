package com.example.emotions.authentications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.emotions.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString()
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()
            val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString().trim()

            if (password == confirmPassword) {
                signUpUser(email, password, name)
            } else {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpUser(email: String, password: String, name: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign up success, update UI with the signed-in user's information
                        Log.d("com.example.emotions.authentications.SignUp", "createUserWithEmail:success")
                        val user = auth.currentUser
                        if (user != null) {
                            saveUserInfo(user.uid, name, email)
                        }
                        // Optionally update the UI or navigate to the next screen
                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w("com.example.emotions.authentications.SignUp", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Email and Password must not be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserInfo(userId: String, name: String, email: String) {
        val database = Firebase.database
        val usersRef = database.getReference("users")
        val userInfo = HashMap<String, Any>()
        userInfo["name"] = name
        userInfo["email"] = email

        usersRef.child(userId).setValue(userInfo)
            .addOnSuccessListener {
                Log.d("com.example.emotions.authentications.SignUp", "User info saved successfully")
                // Optionally handle success
            }
            .addOnFailureListener { e ->
                Log.w("com.example.emotions.authentications.SignUp", "Error saving user info", e)
                // Handle failures
            }
    }
}
