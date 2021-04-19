package app.fyp

import android.util.Log
import com.google.gson.Gson

object L {
    const val TAG = "InstantM"

    fun fine(message: String?) {
        Log.d(TAG, message + "")
    }

    fun error(throwable: Throwable) {
        Log.d(TAG, "ERROR", throwable)
    }

    fun json(o: Any) {
        val s = Gson().toJson(o)
        fine(s)
    }
}