package app.fyp.ui

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import app.fyp.R
import app.fyp.State
import app.fyp.hideKeyboard
import app.fyp.snack
import app.fyp.vms.VM
import kotlinx.android.synthetic.main.layout_add_friend.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class AddFriendActivity: AppCompatActivity() {

    private val vm by viewModel<VM>()
    private val dialog by lazy { LoadingDialog(this, "adding friend...") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_friend)

        supportActionBar?.title = "Add a new Friend"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        subscribe()
        btnAddFriend.setOnClickListener { addFriend() }
    }

    private fun addFriend() {
        val email = edtAddFriendEmail.text.trim().toString().toLowerCase(Locale.getDefault())
        val message = edtAddFriendMessage.text.trim().toString()

        hideKeyboard()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            snack("invalid friend's email provided")
            return
        }
        if (message == "") {
            snack("empty message")
            return
        }
        vm.addFriend(message, email)
    }

    private fun subscribe() {
        vm.addFriendLiveData().observe(this, androidx.lifecycle.Observer {
            when(it.state) {
                State.SUCCESS -> snack("friend added successfully")
                State.LOADING ->  dialog.show()
                State.ERROR -> snack(it.message ?: "failed to add friend at this time. please retry")
            }
            if (it.state != State.LOADING)
                dialog.dismiss()
        })
    }
}