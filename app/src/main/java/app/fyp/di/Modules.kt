package app.fyp.di

import android.content.Context
import app.fyp.Session
import app.fyp.vms.VM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val PREF_NAME = "fyp_sessions"

val appModules = module {
    single{ FirebaseDatabase.getInstance() }
    factory { FirebaseAuth.getInstance() }
    factory { Session(androidContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)) }
    viewModel { VM(get(),
        get<FirebaseDatabase>().getReference("conversations"),
        get<FirebaseDatabase>().getReference("users"), get<FirebaseDatabase>().getReference("messages")) }
}