package com.easycook.doughcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.easycook.doughcalculator.models.Recipe
import com.easycook.doughcalculator.navigation.CalculatorComponent
import com.easycook.doughcalculator.ui.components.IngredientCard
import com.easycook.doughcalculator.ui.components.RecipeDropZone
import com.easycook.doughcalculator.utils.WebStorage
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.navigator.Navigator
import org.w3c.dom.window.Window
import org.w3c.dom.window.window
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass

@Composable
fun WebCalculatorScreen(
    component: CalculatorComponent,
    modifier: Modifier = Modifier
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var recipeName by remember { mutableStateOf("") }
    var isOnline by remember { mutableStateOf(true) }
    var showDropZone by remember { mutableStateOf(false) }

    // Загрузка последнего рецепта при старте
    LaunchedEffect(Unit) {
        WebStorage.loadLastRecipe()?.let { recipe ->
            component.loadRecipe(recipe)
        }
    }

    // Автосохранение при изменении рецепта
    LaunchedEffect(component.currentRecipe.value) {
        component.currentRecipe.value?.let { recipe ->
            WebStorage.saveLastRecipe(recipe)
        }
    }

    // Отслеживание состояния сети
    LaunchedEffect(Unit) {
        val updateOnlineStatus = { status: Boolean ->
            isOnline = status
            if (status) {
                document.body?.removeClass("offline")
            } else {
                document.body?.addClass("offline")
            }
        }

        window.addEventListener("online", { updateOnlineStatus(true) })
        window.addEventListener("offline", { updateOnlineStatus(false) })
        
        updateOnlineStatus(window.navigator.onLine)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Калькулятор теста",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Row {
                IconButton(
                    onClick = { showDropZone = !showDropZone },
                    modifier = Modifier.tooltip("Импорт рецепта")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Импорт рецепта"
                    )
                }

                IconButton(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier.tooltip("Сохранить рецепт")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Сохранить рецепт"
                    )
                }

                // Кнопка "Поделиться" активна только если есть сохраненный рецепт
                component.currentRecipe.value?.let { recipe ->
                    IconButton(
                        onClick = {
                            shareRecipe(recipe)
                        },
                        modifier = Modifier.tooltip("Поделиться рецептом")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Поделиться рецептом"
                        )
                    }
                }
            }
        }

        // Зона для импорта рецептов
        if (showDropZone) {
            RecipeDropZone(
                onRecipeImported = { recipe ->
                    component.loadRecipe(recipe)
                    showDropZone = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        // Индикатор офлайн-режима
        if (!isOnline) {
            Snackbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Вы работаете в офлайн-режиме")
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(component.ingredients.value) { ingredient ->
                IngredientCard(
                    ingredient = ingredient,
                    onValueChange = { newValue ->
                        component.updateIngredient(ingredient.type, newValue)
                    }
                )
            }
        }

        // Результаты
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Результаты:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text("Общий вес: ${String.format("%.1f", component.totalWeight.value)} г")
                Text("Гидратация: ${String.format("%.1f", component.hydration.value)}%")
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Сохранить рецепт") },
            text = {
                OutlinedTextField(
                    value = recipeName,
                    onValueChange = { recipeName = it },
                    label = { Text("Название рецепта") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (recipeName.isNotBlank()) {
                            component.onSaveRecipe(recipeName)
                            showSaveDialog = false
                            recipeName = ""
                        }
                    }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

private fun shareRecipe(recipe: Recipe) {
    val shareData = object {
        val title = recipe.name
        val text = """
            Рецепт: ${recipe.name}
            Мука: ${recipe.flour} г
            Вода: ${recipe.water} г
            Соль: ${recipe.salt} г
            Дрожжи: ${recipe.yeast} г
            Общий вес: ${recipe.totalWeight} г
            Гидратация: ${recipe.hydration}%
        """.trimIndent()
        val url = window.location.href
    }

    if (window.navigator.share != null) {
        window.navigator.share(shareData)
    } else {
        // Fallback для браузеров без поддержки Share API
        val textArea = document.createElement("textarea")
        textArea.value = shareData.text
        document.body?.appendChild(textArea)
        textArea.select()
        document.execCommand("copy")
        document.body?.removeChild(textArea)
        
        // Показываем уведомление
        val notification = document.createElement("div")
        notification.addClass("share-notification")
        notification.textContent = "Рецепт скопирован в буфер обмена"
        document.body?.appendChild(notification)
        
        window.setTimeout({
            document.body?.removeChild(notification)
        }, 2000)
    }
} 