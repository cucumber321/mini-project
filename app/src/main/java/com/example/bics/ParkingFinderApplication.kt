package com.example.bics

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras

class BicsApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

fun CreationExtras.bicsApplication(): BicsApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as BicsApplication)