package com.ticonsys.mvvmrecipeapp.ui.activities

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ticonsys.mvvmrecipeapp.data.domain.model.Recipe
import com.ticonsys.mvvmrecipeapp.data.repositories.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Named

class MainViewModel @ViewModelInject constructor(
    private val recipeRepository: RecipeRepository,
    private @Named("auth_token") val token: String
): ViewModel() {


    val recipes: MutableState<List<Recipe>> = mutableStateOf(emptyList())
    val recipe: MutableState<Recipe> = mutableStateOf(Recipe())

    fun searchRecipes(
        page: Int,
        query: String
    ) = viewModelScope.launch {

        val recipeList = recipeRepository.search(
            token,
            page, query
        )
        Log.d("RecipeListFragment", "searchRecipes: $recipeList")
        recipes.value = recipeList
    }

    fun getRecipe(recipeId: Int) = viewModelScope.launch {
        recipe.value = recipeRepository.get(token, recipeId)
    }

}