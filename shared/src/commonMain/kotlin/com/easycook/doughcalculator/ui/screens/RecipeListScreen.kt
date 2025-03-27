package com.easycook.doughcalculator.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.easycook.doughcalculator.navigation.RecipeItem
import com.easycook.doughcalculator.navigation.RecipeListComponent

@Composable
fun RecipeListScreen(
    component: RecipeListComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Мои рецепты")
        
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            state.error != null -> {
                Text(
                    text = "Ошибка: ${state.error}",
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            state.recipes.isEmpty() -> {
                Text(
                    text = "У вас пока нет сохраненных рецептов",
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            else -> {
                LazyColumn {
                    items(state.recipes) { recipe ->
                        RecipeListItem(recipe, component)
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeListItem(
    recipe: RecipeItem, 
    component: RecipeListComponent
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(recipe.name)
        Text("Гидратация: ${recipe.hydration}%")
        Text(recipe.description)
    }
} 