package com.example.pm26sproject2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var oneTapClient : SignInClient
    private lateinit var signInRequest : BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        oneTapClient = Identity.getSignInClient( this )

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        val tvGoToRegister: TextView = findViewById(R.id.tvGoToRegister)
        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        var currencyUser = auth.currentUser
    }

    fun btRegisterWithGoogleOnClick(view: View) {
        oneTapClient.beginSignIn( signInRequest )
            .addOnSuccessListener { result ->
                startIntentSenderForResult(
                    result.pendingIntent.intentSender,
                    2,
                    null,
                    0,
                    0,
                    0,
                    null
                )
            }
            .addOnFailureListener( this ) { e->
                Log.d( "Login", e.localizedMessage )
            }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
        val idToken = googleCredential.googleIdToken

        if ( idToken != null ) {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val name = user?.displayName

                        println(user?.email)
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        println("erro")
                    }
                }

        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Log.d(TAG, "signInWithEmailAndPassword:Success")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.w(TAG, "signInWithEmailAndPassword:Failurer", task.exception)
                Toast.makeText(baseContext, "Authentication Failure", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun btLoginOnClick(view: View) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if(email.isNotEmpty() && password.isNotEmpty()){
            signInWithEmailAndPassword(email, password)
        }
        else {
            Toast.makeText(this@MainActivity, "Fill in the fields correctly.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private var TAG = "EmailAndPassword"
    }
}