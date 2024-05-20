package com.easycook.doughcalculator.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.easycook.doughcalculator.R
import com.easycook.doughcalculator.RecipeViewModel
import com.easycook.doughcalculator.common.CALCULATION_SCREEN
import com.easycook.doughcalculator.database.DoughRecipeEntity

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipesScreen(
    navController: NavHostController,
    //paddingValues: PaddingValues,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val recipes = viewModel.recipes.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.screen_title_open_recipe),
                        fontSize = 20.sp,
                        color = Color.White,
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
            )
        },
        //floatingActionButton = { AddRecipeButton(navController) }
    ) {
        val state = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start=12.dp, top=78.dp, end=12.dp, bottom=20.dp),
            //verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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

@Composable
fun RecipeItem(item: DoughRecipeEntity, navController: NavHostController, viewModel: RecipeViewModel = hiltViewModel()) {
    val cardBackground = MaterialTheme.colorScheme.background
    var isFavorite by remember { mutableStateOf(item.isFavorite) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { navController.navigate(CALCULATION_SCREEN) }
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
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = item.title,
                fontSize = 20.sp,
                textAlign = TextAlign.Start
            )
            IconButton(onClick = { viewModel.deleteRecipe(item) }) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}