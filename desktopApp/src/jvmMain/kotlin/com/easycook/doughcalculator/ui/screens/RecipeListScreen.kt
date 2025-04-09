package com.easycook.doughcalculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.easycook.doughcalculator.models.DoughRecipe
import com.easycook.doughcalculator.resources.Strings
import com.easycook.doughcalculator.resources.StringsDesktop
import com.easycook.doughcalculator.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
                            contentDescription = StringsDesktop.IconsDescriptions.STORAGE_INFO
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                    contentDescription = StringsDesktop.IconsDescriptions.FAVORITE,
                    tint = if (recipe.isFavorite) colorScheme.primary else LocalContentColor.current,
                )
            }
            
            IconButton(onClick = { showDeleteConfirmation = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = StringsDesktop.IconsDescriptions.DELETE
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
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(StringsDesktop.StorageInfo.DIALOG_TITLE) },
        text = {
            Column {
                Text(StringsDesktop.StorageInfo.RECIPES_SAVED_TO_FILE)
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    color = colorScheme.surfaceVariant,
                    contentColor = colorScheme.onSurfaceVariant,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SelectionContainer(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = dataFile,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        
                        TextButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(dataFile.substringBeforeLast("/")))
                                isCopied = true
                                scope.launch {
                                    delay(1500)
                                    isCopied = false
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            enabled = !isCopied
                        ) {
                            Text(
                                text = if (isCopied) StringsDesktop.StorageInfo.PATH_COPIED else StringsDesktop.StorageInfo.COPY_PATH,
                                color = if (isCopied) colorScheme.primary else LocalContentColor.current
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(StringsDesktop.StorageInfo.BACKUP_INFO)
            }
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text(Strings.Buttons.UNDERSTAND)
            }
        }
    )
} 