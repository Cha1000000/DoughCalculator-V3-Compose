package com.easycook.doughcalculator

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface DemoScreen {
    data object Screen1 : DemoScreen
    data class Screen2(val parameter: String) : DemoScreen
    data class Screen3(val parameter: String) : DemoScreen
}

class DemoNavigationComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val _currentScreen = MutableStateFlow<DemoScreen>(DemoScreen.Screen1)
    val currentScreen: StateFlow<DemoScreen> = _currentScreen.asStateFlow()
    
    private val navigationStack = mutableListOf<DemoScreen>()
    
    init {
        navigationStack.add(DemoScreen.Screen1)
    }
    
    fun navigateToScreen2() {
        val newScreen = DemoScreen.Screen2("Параметр по умолчанию")
        _currentScreen.value = newScreen
        navigationStack.add(newScreen)
    }
    
    fun navigateToScreen3(parameter: String) {
        val newScreen = DemoScreen.Screen3(parameter)
        _currentScreen.value = newScreen
        navigationStack.add(newScreen)
    }
    
    fun navigateBack() {
        if (navigationStack.size > 1) {
            navigationStack.removeAt(navigationStack.lastIndex)
            _currentScreen.value = navigationStack.last()
        }
    }
} 