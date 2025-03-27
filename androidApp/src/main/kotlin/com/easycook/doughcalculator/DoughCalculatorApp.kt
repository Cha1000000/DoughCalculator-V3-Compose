package com.easycook.doughcalculator

import android.app.Application
import com.easycook.doughcalculator.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class DoughCalculatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        initKoin {
            androidLogger()
            androidContext(this@DoughCalculatorApp)
        }
    }
} 