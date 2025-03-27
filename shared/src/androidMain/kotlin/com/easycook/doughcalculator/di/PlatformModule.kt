package com.easycook.doughcalculator.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.easycook.doughcalculator.db.DoughCalculatorDatabase
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = DoughCalculatorDatabase.Schema,
            context = get<Context>(),
            name = "dough_calculator.db"
        )
    }

    single {
        DoughCalculatorDatabase(get())
    }

    single<Settings> {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences("dough_calculator_prefs", Context.MODE_PRIVATE)
        )
    }

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
} 