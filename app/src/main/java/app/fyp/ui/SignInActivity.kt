package app.fyp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import app.fyp.*
import app.fyp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.layout_sign_in.*
import org.koin.android.ext.android.inject

class SignInActivity: AppCompatActivity() {

    private val fireAuth by inject<FirebaseAuth>()
    private val fireDatabase by inject<FirebaseDatabase>()
    private val dialog by lazy { LoadingDialog(this, "signing in...") }
    private val session by inject<Session>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_sign_in)

        actionBar?.title = "Sign In"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        btnLoginSignIn.setOnClickListener {
            signIn()
        }
        btnRegisterSignIn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun signIn() {
        val email = edtEmailSignIn.text.toString().trim()
        val password = edtPasswordSignIn.text.toString().trim()

        hideKeyboard()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            snack("invalid email address")
            return
        }

        if (password.length < 5) {
            snack("invalid password. password length must be at least 5")
            return
        }
        dialog.show()
        fireAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fireDatabase.getReference("users").child(Useful.cleanEmail(email)).get().addOnCompleteListener {
                    dialog.dismiss()
                    if (it.isSuccessful) {
                        val user = it.result?.getValue(User::class.java) ?: User()
                        session.putUser(user)
                        val homePageIntent = Intent(this, HomeActivity::class.java)
                            .apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        startActivity(homePageIntent)
                    }else {
                        showDialog("Login Error", "failed to sign in user. account details not found")
                    }
                }
            }else {
                dialog.dismiss()
                val errorMessage = when(task.exception) {
                    is FirebaseAuthInvalidCredentialsException -> " The password is invalid or the user does not have a password."
                    else -> "failed to sign in at this time. please retry later"
                }
                snack(errorMessage)
            }
        }
    }
}