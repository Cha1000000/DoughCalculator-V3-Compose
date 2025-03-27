package com.easycook.doughcalculator.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value

interface RootComponent {
    val childStack: Value<ChildStack<Config, Child>>
    
    fun onCalculatorTabClicked()
    fun onRecipeListTabClicked()
    fun onRecipeSelected(recipeId: Long)
    fun onBackClicked()
    
    sealed class Child {
        class Calculator(val component: CalculatorComponent) : Child()
        class RecipeList(val component: RecipeListComponent) : Child()
        class RecipeDetails(val component: RecipeDetailsComponent) : Child()
    }
}

class RootComponentImpl(
    private val componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : RootComponent, ComponentContext by componentContext {
    
    private val navigation = StackNavigation<Config>()
    
    override val childStack = componentContext.childStack(
        source = navigation,
        initialConfiguration = Config.Calculator,
        handleBackButton = true,
        childFactory = ::createChild
    )
    
    override fun onCalculatorTabClicked() {
        navigation.push(Config.Calculator)
    }
    
    override fun onRecipeListTabClicked() {
        navigation.push(Config.RecipeList)
    }
    
    override fun onRecipeSelected(recipeId: Long) {
        navigation.push(Config.RecipeDetails(recipeId))
    }
    
    override fun onBackClicked() {
        navigation.pop()
    }
    
    private fun createChild(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.Calculator -> RootComponent.Child.Calculator(
                componentFactory.createCalculatorComponent(componentContext)
            )
            is Config.RecipeList -> RootComponent.Child.RecipeList(
                componentFactory.createRecipeListComponent(
                    componentContext = componentContext,
                    onRecipeSelected = ::onRecipeSelected
                )
            )
            is Config.RecipeDetails -> RootComponent.Child.RecipeDetails(
                componentFactory.createRecipeDetailsComponent(
                    componentContext = componentContext,
                    recipeId = config.recipeId,
                    onBackClicked = ::onBackClicked
                )
            )
        }
} 