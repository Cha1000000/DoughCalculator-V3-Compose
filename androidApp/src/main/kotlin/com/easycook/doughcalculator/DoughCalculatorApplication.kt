package com.easycook.doughcalculator

import android.app.Application
import com.easycook.doughcalculator.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DoughCalculatorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@DoughCalculatorApplication)
            modules(appModule())
        }
    }
} 