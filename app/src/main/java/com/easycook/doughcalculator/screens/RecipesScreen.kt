package com.easycook.doughcalculator.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.AlertDialog
import androidx.compose.material3.FloatingActionButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.easycook.doughcalculator.R
import com.easycook.doughcalculator.RecipeViewModel
import com.easycook.doughcalculator.common.CALCULATION_SCREEN
import com.easycook.doughcalculator.database.DoughRecipeEntity
import com.easycook.doughcalculator.ui.theme.DoughCalculatorTheme

@Composable
fun RecipesScreen(
    navController: NavHostController,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val recipes = viewModel.recipes.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.screen_title_open_recipe),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                backgroundColor = colorScheme.primary,
            )
        },
        floatingActionButton = { AddRecipeButton(navController) }
    ) { paddingValues ->
        val state = rememberLazyListState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_notepad),
                contentDescription = "Background image",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, top = 18.dp, end = 12.dp, bottom = 20.dp),
                horizontalAlignment = CenterHorizontally,
                state = state,
            ) {
                itemsIndexed(
                    items = recipes.value,
                    // Provide a unique key based on the item content
                    key = { _, item -> item.hashCode() }
                ) { _, item ->
                    RecipeItem(item, navController)
                }
            }
        }
    }

}

@Composable
fun RecipeItem(
    item: DoughRecipeEntity,
    navController: NavHostController,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val cardBackground = colorScheme.background
    var isFavorite by remember { mutableStateOf(item.isFavorite) }
    val openDialog = rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            viewModel.recipeEntity = item
            navController.navigate(CALCULATION_SCREEN) {
                launchSingleTop = true
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackground)
                .padding(start = 4.dp, top = 6.dp, end = 4.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                isFavorite = !isFavorite
                item.isFavorite = isFavorite
                viewModel.updateRecipe(item)
            }) {
                Icon(
                    /*painter = if (isFavorite) painterResource(id = R.drawable.ic_favorite_filled)
                    else painterResource(id = R.drawable.ic_favorite_border),*/
                    imageVector = if (!isFavorite) Icons.Filled.FavoriteBorder else Icons.Filled.Favorite,
                    contentDescription = "IsFavourite",
                    tint = if (isFavorite) colorScheme.tertiary else colorScheme.primary,
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = item.title,
                fontSize = 20.sp,
                textAlign = TextAlign.Start
            )
            IconButton(onClick = { openDialog.value = true }) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = colorScheme.primary,
                )
            }
        }

        if (openDialog.value) {
            ShowConfirmDialog(openDialog, item.title) {
                viewModel.deleteRecipe(item)
                openDialog.value = false
            }
        }
    }
}

@Composable
fun ShowConfirmDialog(
    dialogState: MutableState<Boolean>,
    recipeTitle: String,
    onConfirm: () -> Unit
) {
    DoughCalculatorTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colorScheme.background
        ) {
            AlertDialog(
                onDismissRequest = { dialogState.value = false },
                title = {
                    Text(
                        color = colorScheme.tertiary,
                        text = stringResource(R.string.delete_confirm_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.recipe_delete_confirm_message, recipeTitle),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                confirmButton = {
                    TextButton(onClick = onConfirm) {
                        Text(text = stringResource(R.string.error_alert_ok_button_yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { dialogState.value = false }) {
                        Text(text = stringResource(R.string.error_alert_ok_button_cancel))
                    }
                },
            )
        }
    }
}

@Composable
fun AddRecipeButton(navController: NavHostController, viewModel: RecipeViewModel = hiltViewModel()) {
    FloatingActionButton(
        modifier = Modifier.padding(vertical = 8.dp),
        onClick = {
            viewModel.recipeEntity = null
            navController.navigate(CALCULATION_SCREEN) {
                launchSingleTop = true
            }
        },
        shape = CircleShape,
        containerColor = colorScheme.primary
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add new recipe",
        )
    }
}