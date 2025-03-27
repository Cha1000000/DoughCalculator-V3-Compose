package com.easycook.doughcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.easycook.doughcalculator.navigation.RootComponent
import com.easycook.doughcalculator.ui.RootContent
import com.easycook.doughcalculator.ui.theme.DoughCalculatorTheme

class MainActivity : ComponentActivity() {
    private val lifecycleRegistry = LifecycleRegistry()
    private lateinit var rootComponent: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Создаем RootComponent с жизненным циклом активности
        rootComponent = RootComponent(DefaultComponentContext(lifecycleRegistry))
        lifecycleRegistry.resume()
        
        setContent {
            DoughCalculatorTheme {
                RootContent(component = rootComponent)
            }
        }
    }
    
    override fun onDestroy() {
        // Уничтожаем жизненный цикл при уничтожении активности
        lifecycleRegistry.destroy()
        super.onDestroy()
    }
} 