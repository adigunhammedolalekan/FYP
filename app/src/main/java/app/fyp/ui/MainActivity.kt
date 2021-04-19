package app.fyp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import app.fyp.R
import app.fyp.Session
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val session by inject<Session>()
    private val firebaseAuth by inject<FirebaseAuth>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAuth()

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun checkAuth() {
        val currentUser = firebaseAuth.currentUser
        val cachedUser = session.currentUser()

        val hasSession = currentUser != null && currentUser.uid != "" && cachedUser.userId != ""
        if (hasSession) {
            mainContentLayout.visibility = View.GONE
            val homePageIntent = Intent(this, HomeActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            startActivity(homePageIntent)
        }else {
            mainContentLayout.visibility = View.VISIBLE
        }
    }
}