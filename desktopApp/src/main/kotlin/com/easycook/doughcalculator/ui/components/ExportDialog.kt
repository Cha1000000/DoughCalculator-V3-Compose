package com.easycook.doughcalculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.easycook.doughcalculator.models.Recipe
import com.easycook.doughcalculator.utils.RecipeExporter
import java.io.File

@Composable
fun ExportDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFormat by remember { mutableStateOf("csv") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Экспорт рецепта",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Выбор формата
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selectedFormat == "csv",
                        onClick = { selectedFormat = "csv" }
                    )
                    Text("CSV")

                    RadioButton(
                        selected = selectedFormat == "json",
                        onClick = { selectedFormat = "json" }
                    )
                    Text("JSON")
                }

                // Сообщение об ошибке
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Кнопки
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }

                    Button(
                        onClick = {
                            try {
                                val file = File("${recipe.name}.${selectedFormat}")
                                when (selectedFormat) {
                                    "csv" -> RecipeExporter.exportToCsv(recipe, file)
                                    "json" -> RecipeExporter.exportToJson(recipe, file)
                                }
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Ошибка при экспорте: ${e.message}"
                            }
                        }
                    ) {
                        Text("Экспорт")
                    }
                }
            }
        }
    }
} 