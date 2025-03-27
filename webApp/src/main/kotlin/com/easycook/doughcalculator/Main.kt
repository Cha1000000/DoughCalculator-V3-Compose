package com.easycook.doughcalculator

import com.easycook.doughcalculator.navigation.DefaultRootComponent
import com.easycook.doughcalculator.ui.screens.WebCalculatorScreen
import com.easycook.doughcalculator.ui.theme.DoughCalculatorTheme
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

fun main() {
    val rootComponent: DefaultRootComponent by inject()
    
    renderComposable(rootElementId = "root") {
        DoughCalculatorTheme {
            Div {
                WebCalculatorScreen(component = rootComponent.calculatorComponent)
            }
        }
    }

    // Регистрация Service Worker
    if ("serviceWorker" in window.navigator) {
        window.addEventListener("load") {
            window.navigator.serviceWorker.register("/service-worker.js")
                .then { registration ->
                    console.log("ServiceWorker registration successful")
                }
                .catch { err ->
                    console.log("ServiceWorker registration failed: ", err)
                }
        }
    }
} 