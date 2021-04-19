package app.fyp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import app.fyp.*
import app.fyp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.layout_register.*
import org.koin.android.ext.android.inject
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private val fireAuth by inject<FirebaseAuth>()
    private val fireDatabase by inject<FirebaseDatabase>()
    private val dialog by lazy { LoadingDialog(this, "registration in progress...") }
    private val session by inject<Session>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_register)

        actionBar?.title = "Register"
        actionBar?.setDisplayHomeAsUpEnabled(true)

        btnRegisterRegister.setOnClickListener {
            register()
        }

        btnSignInRegister.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun register() {
        val email = edtEmailRegister.text.toString().toLowerCase(Locale.getDefault()).trim()
        val firstName = edtFirstNameRegister.text.toString().trim()
        val lastName = edtLastNameRegister.text.toString().trim()
        val password = edtPasswordRegister.text.toString().trim()

        hideKeyboard()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            snack("invalid email address")
            return
        }
        if (firstName == "") {
            snack("no value supplied for 'first name'")
            return
        }
        if (lastName == "") {
            snack("no value supplied for 'last name'")
            return
        }

        if (password.length < 5) {
            snack("invalid or weak password. password length must be at least 5")
            return
        }
        dialog.show()

        fireAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            dialog.dismiss()
            if (task.isSuccessful) {
                val createdUserId = task.result?.user?.uid ?: UUID.randomUUID().toString()
                val newUser = User(createdUserId, firstName, lastName, email)
                fireDatabase.getReference("users").child(Useful.cleanEmail(email)).setValue(newUser)
                session.putUser(newUser)
                val homePageIntent = Intent(this, HomeActivity::class.java)
                    .apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                startActivity(homePageIntent)
            } else {
                val errorMessage = when(task.exception) {
                    is FirebaseAuthUserCollisionException -> "The email address is already in use by another account"
                    else -> task.exception?.localizedMessage ?: "failed to register user at this time. please retry"
                }
                snack(errorMessage)
            }
        }
    }
}