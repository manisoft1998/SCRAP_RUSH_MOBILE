package com.manisoft.scraprushapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.libraries.places.api.Places
import com.manisoft.scraprushapp.mvvm.di.apiModule
import com.manisoft.scraprushapp.mvvm.di.repositoryModule
import com.manisoft.scraprushapp.mvvm.di.retrofitModule
import com.manisoft.scraprushapp.mvvm.di.viewModelModule
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.ModelPreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KeyValues.init(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            loadKoinModules(listOf(repositoryModule, viewModelModule, retrofitModule, apiModule))
        }

        ModelPreferenceManager.init(this@MyApp)

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext,"AIzaSyCA9ZR1itYfZLbC0_GrmNhiOLgHA5UeRnI")
        }
    }
}