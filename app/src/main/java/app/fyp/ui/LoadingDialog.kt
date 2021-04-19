package app.fyp.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import app.fyp.R

class LoadingDialog(private val context: Context, private var message: String = "") {

    private val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null, false)
    private val loadingTv = dialogView.findViewById<TextView>(R.id.tvLoadingDialog)
    private val dialog = AlertDialog.Builder(context)
        .setView(dialogView)
        .setCancelable(false)
        .create()

    fun show() {
        if (message == "") message = "Loading..."
        loadingTv.text = message
        dialog.show()
    }

    fun setMessage(message: String){ loadingTv.text = message }

    fun dismiss() = dialog.dismiss()
}