package com.example.pm26sproject2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private var isMenuVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnMenu: Button = findViewById(R.id.btnMenu)
        val sideMenu: LinearLayout = findViewById(R.id.sideMenu)

        btnMenu.setOnClickListener {
            toggleMenu(sideMenu)
        }

        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun toggleMenu(menu: View) {
        if (isMenuVisible) {
            menu.animate()
                .translationX(menu.width.toFloat())
                .setDuration(300)
                .withEndAction {
                    menu.visibility = View.GONE
                }
                .start()
        } else {
            menu.visibility = View.VISIBLE
            menu.animate()
                .translationX(600f)
                .setDuration(300)
                .start()
        }
        isMenuVisible = !isMenuVisible
    }
}
