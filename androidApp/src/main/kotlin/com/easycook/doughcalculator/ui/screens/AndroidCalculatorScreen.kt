package com.easycook.doughcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.easycook.doughcalculator.models.IngredientUiState
import com.easycook.doughcalculator.presentation.CalculatorViewModel
import com.easycook.doughcalculator.ui.components.IngredientCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidCalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val ingredients by viewModel.ingredients.collectAsState()
    val totalWeight by viewModel.totalWeight.collectAsState()
    val hydration by viewModel.hydration.collectAsState()
    
    var showSaveDialog by remember { mutableStateOf(false) }
    var recipeName by remember { mutableStateOf("") }

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
            
            IconButton(onClick = { showSaveDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Сохранить рецепт"
                )
            }
        }

        // Список ингредиентов
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ingredients) { ingredient ->
                IngredientCard(
                    ingredient = ingredient,
                    onValueChange = { newValue ->
                        viewModel.updateIngredient(ingredient.type, newValue)
                    }
                )
            }
        }

        // Результаты
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(),
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

                Text("Общий вес: ${String.format("%.1f", totalWeight)} г")
                Text("Гидратация: ${String.format("%.1f", hydration)}%")
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
                            viewModel.saveCurrentRecipe(recipeName)
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