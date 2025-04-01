package com.easycook.doughcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.easycook.doughcalculator.models.DoughRecipe
import com.easycook.doughcalculator.viewmodel.RecipeViewModel
import com.easycook.doughcalculator.resources.Strings
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeViewModel,
    onNavigateToCalculator: () -> Unit,
    onNavigateToRecipe: (DoughRecipe) -> Unit
) {
    val recipes by viewModel.recipes.collectAsState()
    var showStorageInfo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.Navigation.MY_RECIPES) },
                actions = {
                    IconButton(onClick = { showStorageInfo = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Информация о хранении"
                        )
                    }
                    IconButton(onClick = onNavigateToCalculator) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = Strings.Navigation.NEW_RECIPE
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onRecipeClick = { onNavigateToRecipe(recipe) },
                    onFavoriteClick = { 
                        viewModel.updateRecipe(recipe.copy(isFavorite = !recipe.isFavorite))
                    },
                    onDeleteClick = { viewModel.deleteRecipe(recipe.id ?: return@RecipeCard) }
                )
            }
        }
    }
    
    if (showStorageInfo) {
        StorageInfoDialog(onDismissRequest = { showStorageInfo = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeCard(
    recipe: DoughRecipe,
    onRecipeClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        onClick = onRecipeClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Избранное"
                )
            }
            
            IconButton(onClick = { showDeleteConfirmation = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить"
                )
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(Strings.AlertDialogs.CONFIRM) },
            text = { Text(Strings.AlertDialogs.DELETE_RECIPE_CONFIRMATION.format(recipe.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text(Strings.Buttons.DELETE)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(Strings.Buttons.CANCEL)
                }
            }
        )
    }
}

@Composable
private fun StorageInfoDialog(
    onDismissRequest: () -> Unit
) {
    // Используется для показа пути к файлу с рецептами
    val dataFile = File("recipes/recipes.json").absolutePath

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Информация о хранении") },
        text = {
            Column {
                Text("Ваши рецепты сохраняются между запусками приложения в файл:")
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                ) {
                    Text(
                        text = dataFile,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Вы можете скопировать этот файл для создания резервной копии рецептов.")
            }
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text(Strings.Buttons.UNDERSTAND)
            }
        }
    )
} 