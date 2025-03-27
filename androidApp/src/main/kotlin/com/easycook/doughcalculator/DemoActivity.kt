package com.easycook.doughcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume

@OptIn(ExperimentalMaterial3Api::class)
class DemoActivity : ComponentActivity() {
    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var demoComponent: DemoNavigationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Создаем компонент с жизненным циклом активности
        lifecycleRegistry = LifecycleRegistry()
        demoComponent = DemoNavigationComponent(DefaultComponentContext(lifecycleRegistry))
        lifecycleRegistry.resume()
        
        setContent {
            MaterialTheme {
                val currentScreen by demoComponent.currentScreen.collectAsState()
                
                when (val screen = currentScreen) {
                    is DemoScreen.Screen1 -> Screen1UI(
                        onNext = demoComponent::navigateToScreen2
                    )
                    is DemoScreen.Screen2 -> Screen2UI(
                        parameter = screen.parameter,
                        onBack = demoComponent::navigateBack,
                        onNext = { demoComponent.navigateToScreen3(it) }
                    )
                    is DemoScreen.Screen3 -> Screen3UI(
                        parameter = screen.parameter,
                        onBack = demoComponent::navigateBack
                    )
                }
            }
        }
    }
    
    override fun onDestroy() {
        lifecycleRegistry.destroy()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen1UI(onNext: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Экран 1") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Это начальный экран")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNext) {
                Text("Перейти на экран 2")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen2UI(parameter: String, onBack: () -> Unit, onNext: (String) -> Unit) {
    var inputText by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Экран 2") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("Назад")
                    }
                }
            ) 
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Получен параметр: $parameter")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Введите что-нибудь") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onNext(inputText) }) {
                Text("Перейти на экран 3")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen3UI(parameter: String, onBack: () -> Unit) {
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Экран 3") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("Назад")
                    }
                }
            ) 
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Получен параметр: $parameter")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Вернуться назад")
            }
        }
    }
} 