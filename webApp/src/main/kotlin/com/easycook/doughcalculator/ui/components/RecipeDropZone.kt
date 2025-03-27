package com.easycook.doughcalculator.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.easycook.doughcalculator.models.Recipe

@Composable
fun RecipeDropZone(
    onRecipeImported: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val dropZone = document.getElementById("recipe-drop-zone")
        
        val handleDragEnter = { e: dynamic ->
            e.preventDefault()
            isDragging = true
        }
        
        val handleDragLeave = { e: dynamic ->
            e.preventDefault()
            isDragging = false
        }
        
        val handleDragOver = { e: dynamic ->
            e.preventDefault()
        }
        
        val handleDrop = { e: dynamic ->
            e.preventDefault()
            isDragging = false
            
            val file = e.dataTransfer.files[0]
            if (file != null) {
                val reader = window.FileReader()
                reader.onload = {
                    try {
                        val content = reader.result as String
                        val recipe = Json.decodeFromString<Recipe>(content)
                        onRecipeImported(recipe)
                    } catch (e: Exception) {
                        errorMessage = "Ошибка при импорте рецепта: ${e.message}"
                    }
                }
                reader.readAsText(file)
            }
        }

        dropZone?.addEventListener("dragenter", handleDragEnter)
        dropZone?.addEventListener("dragleave", handleDragLeave)
        dropZone?.addEventListener("dragover", handleDragOver)
        dropZone?.addEventListener("drop", handleDrop)

        return@LaunchedEffect {
            dropZone?.removeEventListener("dragenter", handleDragEnter)
            dropZone?.removeEventListener("dragleave", handleDragLeave)
            dropZone?.removeEventListener("dragover", handleDragOver)
            dropZone?.removeEventListener("drop", handleDrop)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(
                width = 2.dp,
                color = if (isDragging) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Перетащите файл рецепта сюда",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "или",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Button(
                onClick = {
                    val input = document.createElement("input")
                    input.type = "file"
                    input.accept = ".json"
                    input.onchange = {
                        val file = input.files[0]
                        if (file != null) {
                            val reader = window.FileReader()
                            reader.onload = {
                                try {
                                    val content = reader.result as String
                                    val recipe = Json.decodeFromString<Recipe>(content)
                                    onRecipeImported(recipe)
                                } catch (e: Exception) {
                                    errorMessage = "Ошибка при импорте рецепта: ${e.message}"
                                }
                            }
                            reader.readAsText(file)
                        }
                    }
                    input.click()
                }
            ) {
                Text("Выберите файл")
            }
        }
    }

    errorMessage?.let { message ->
        Snackbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(message)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { errorMessage = null }) {
                Text("OK")
            }
        }
    }
} 