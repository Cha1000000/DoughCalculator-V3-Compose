package com.easycook.doughcalculator.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easycook.doughcalculator.R
import com.easycook.doughcalculator.RecipeViewModel
import com.easycook.doughcalculator.common.CALCULATION_SCREEN
import com.easycook.doughcalculator.common.ShowConfirmDialog
import com.easycook.doughcalculator.database.DoughRecipeEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    navController: NavHostController,
    viewModel: RecipeViewModel
) {
    val recipes = viewModel.recipes.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(50.dp),
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        text = stringResource(id = R.string.screen_title_open_recipe),
                        textAlign = TextAlign.Center,
                        style = typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.primary,
                )
            )
        },
        floatingActionButton = { AddRecipeButton(navController, viewModel) }
    ) { paddingValues ->
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
            ) {
                itemsIndexed(
                    items = recipes.value,
                    // Provide a unique key based on the item content
                    key = { _, item -> item.hashCode() }
                ) { _, item ->
                    RecipeItem(item, navController, viewModel)
                }
            }
        }
    }

}

@Composable
fun RecipeItem(
    item: DoughRecipeEntity,
    navController: NavHostController,
    viewModel: RecipeViewModel
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
            viewModel.refreshSavedRecipeOriginalState()
            viewModel.resetIngredientTableRows()
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
            ShowConfirmDialog(
                title = stringResource(R.string.delete_confirm_title),
                message = stringResource(R.string.recipe_delete_confirm_message, item.title),
                dialogState = openDialog,
            ) {
                viewModel.deleteRecipe(item)
                openDialog.value = false
            }
        }
    }
}

@Composable
fun AddRecipeButton(navController: NavHostController, viewModel: RecipeViewModel) {
    FloatingActionButton(
        modifier = Modifier.padding(vertical = 8.dp),
        onClick = {
            viewModel.resetRecipe()
            viewModel.resetIngredientTableRows()
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