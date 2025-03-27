package com.easycook.doughcalculator.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.easycook.doughcalculator.db.DoughCalculatorDatabase
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<SqlDriver> {
        WebWorkerDriver(
            schema = DoughCalculatorDatabase.Schema,
            dbName = "dough_calculator.db"
        )
    }

    single {
        DoughCalculatorDatabase(get())
    }

    single<Settings> {
        StorageSettings(localStorage)
    }

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
} 