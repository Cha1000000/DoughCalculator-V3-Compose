package com.easycook.doughcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import com.easycook.doughcalculator.models.Recipe
import com.easycook.doughcalculator.navigation.CalculatorComponent
import com.easycook.doughcalculator.ui.components.ExportDialog
import com.easycook.doughcalculator.ui.components.IngredientCard

@Composable
fun DesktopCalculatorScreen(
    component: CalculatorComponent,
    modifier: Modifier = Modifier
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var recipeName by remember { mutableStateOf("") }

    // Обработка горячих клавиш
    Box(
        modifier = modifier
            .fillMaxSize()
            .onKeyEvent { keyEvent ->
                when {
                    keyEvent.isCtrlPressed && keyEvent.key == Key.S -> {
                        showSaveDialog = true
                        true
                    }
                    keyEvent.isCtrlPressed && keyEvent.key == Key.R && component.currentRecipe.value != null -> {
                        showExportDialog = true
                        true
                    }
                    else -> false
                }
            }
    ) {
        Column(
            modifier = Modifier
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
                        onClick = { showSaveDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Сохранить рецепт"
                        )
                    }

                    // Кнопка "Экспорт" активна только если есть сохраненный рецепт
                    component.currentRecipe.value?.let { recipe ->
                        IconButton(
                            onClick = { showExportDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Экспорт рецепта"
                            )
                        }
                    }
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

    if (showExportDialog && component.currentRecipe.value != null) {
        ExportDialog(
            recipe = component.currentRecipe.value!!,
            onDismiss = { showExportDialog = false }
        )
    }
} 