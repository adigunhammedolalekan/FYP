package app.fyp

import android.app.Application
import app.fyp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FYP: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FYP)
            modules(appModules)
        }
    }
}