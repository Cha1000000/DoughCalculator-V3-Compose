package com.easycook.doughcalculator.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.easycook.doughcalculator.navigation.RecipeDetailsComponent
import com.easycook.doughcalculator.navigation.IngredientItem

@Composable
fun RecipeDetailsScreen(
    component: RecipeDetailsComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            state.error != null -> {
                Text(
                    text = "Ошибка: ${state.error}",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = { component.onBackClicked() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Назад")
                }
            }
            else -> {
                Text(
                    text = state.name,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text("Описание: ${state.description}")
                Text("Гидратация: ${state.hydration}%")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Ингредиенты:")
                state.ingredients.forEach { ingredient ->
                    RecipeDetailRow(ingredient.type, "${ingredient.amount} ${ingredient.unit}")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row {
                    Button(
                        onClick = { component.onBackClicked() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Назад")
                    }
                    
                    Spacer(modifier = Modifier.weight(0.5f))
                    
                    Button(
                        onClick = { component.onEditClicked() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Редактировать")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { component.onCopyToCalculatorClicked() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Копировать в калькулятор")
                }
            }
        }
    }
}

@Composable
fun RecipeDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.weight(1f)
        )
        Text(text = value)
    }
} 