package app.fyp

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

fun Context.toast(m: String?) =
    Toast.makeText(this, m, Toast.LENGTH_LONG).apply {
        setGravity(Gravity.CENTER, 0, 0)
    }.show()

fun Context.showDialog(title: String, message: String) =
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        .create().show()

fun AppCompatActivity.hideKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var currentFocused = this.currentFocus
    if (currentFocused == null)
        currentFocused = View(this)
    imm.hideSoftInputFromWindow(currentFocused.windowToken, 0)
}

fun Activity.snack(message: String) =
    Snackbar.make(findViewById(android.R.id.content),
        message, Snackbar.LENGTH_LONG).show()