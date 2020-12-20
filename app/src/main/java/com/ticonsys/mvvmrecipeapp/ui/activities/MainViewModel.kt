package com.ticonsys.mvvmrecipeapp.ui.activities

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ticonsys.mvvmrecipeapp.data.domain.model.Recipe
import com.ticonsys.mvvmrecipeapp.data.repositories.RecipeRepository
import com.ticonsys.mvvmrecipeapp.ui.components.util.GenericDialogInfo
import com.ticonsys.mvvmrecipeapp.ui.fragments.recipe_list.FoodCategory
import com.ticonsys.mvvmrecipeapp.ui.fragments.recipe_list.RecipeListEvent
import com.ticonsys.mvvmrecipeapp.ui.fragments.recipe_list.RecipeListEvent.*
import com.ticonsys.mvvmrecipeapp.ui.fragments.recipe_list.getFoodCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Named

const val PAGE_SIZE = 30

const val STATE_KEY_PAGE = "recipe.state.page.key"
const val STATE_KEY_QUERY = "recipe.state.query.key"
const val STATE_KEY_LIST_POSITION = "recipe.state.query.list_position"
const val STATE_KEY_SELECTED_CATEGORY = "recipe.state.query.selected_category"

class MainViewModel @ViewModelInject constructor(
    private val repository: RecipeRepository,
    @Named("auth_token") private val token: String,
    @Assisted private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val recipes: MutableState<List<Recipe>> = mutableStateOf(emptyList())
    val recipe: MutableState<Recipe> = mutableStateOf(Recipe())

    val loading = mutableStateOf(false)

    // Pagination starts at '1' (-1 = exhausted?)
    val page = mutableStateOf(1)

    val query = mutableStateOf("")

    /**
     * Display a dialog for the user to see.
     * If GenericDialogInfo == null, do not a show dialog.
     */
    val genericDialogInfo: MutableState<GenericDialogInfo?> = mutableStateOf(null)

    val selectedCategory: MutableState<FoodCategory?> = mutableStateOf(null)

    var categoryScrollPosition: Float = 0f

    var recipeListScrollPosition = 0

    init {
        savedStateHandle.get<Int>(STATE_KEY_PAGE)?.let { p ->
            Log.d(TAG, "restoring page: ${p}")
            setPage(p)
        }
        savedStateHandle.get<String>(STATE_KEY_QUERY)?.let { q ->
            setQuery(q)
        }
        savedStateHandle.get<Int>(STATE_KEY_LIST_POSITION)?.let { p ->
            Log.d(TAG, "restoring scroll position: ${p}")
            setListScrollPosition(p)
        }
        savedStateHandle.get<FoodCategory>(STATE_KEY_SELECTED_CATEGORY)?.let { c ->
            setSelectedCategory(c)
        }

        // Were they doing something before the process died?
        if(recipeListScrollPosition != 0){
            onTriggerEvent(RestoreStateEvent())
        }
        else{
            onTriggerEvent(NewSearchEvent())
        }
    }

    fun onTriggerEvent(event: RecipeListEvent){
        viewModelScope.launch {
            try {
                when(event){
                    is NewSearchEvent -> {
                        newSearch()
                    }
                    is NextPageEvent -> {
                        nextPage()
                    }
                    is RestoreStateEvent -> {
                        restoreState()
                    }
                }
            }catch (e: Exception){
                Log.e(TAG, "launchJob: Exception: ${e}, ${e.cause}")
                e.printStackTrace()
            }
            finally {
                Log.d(TAG, "launchJob: finally called.")
            }
        }
    }

    private suspend fun restoreState(){
        loading.value = true
        // Must retrieve each page of results.
        val results: MutableList<Recipe> = mutableListOf()
        for(p in 1..page.value){
            Log.d(TAG, "restoreState: page: ${p}, query: ${query.value}")
            val result = repository.search(token = token, page = p, query = query.value )
            results.addAll(result)
            if(p == page.value){ // done
                recipes.value = results
                loading.value = false
            }
        }
    }

    private suspend fun newSearch(){
        loading.value = true

        // New search. Reset the state
        resetSearchState()

        // just to show pagination, api is fast
        delay(1000)

        val result = repository.search(token = token, page = page.value, query = query.value )
        recipes.value = result
        loading.value = false
    }

    private suspend fun nextPage(){
        // prevent duplicate event due to recompose happening to quickly
        if((recipeListScrollPosition + 1) >= (page.value * PAGE_SIZE) ){
            loading.value = true
            incrementPage()
            Log.d(TAG, "nextPage: triggered: ${page.value}")

            // just to show pagination, api is fast
            delay(1000)

            if(page.value > 1){
                val result = repository.search(token = token, page = page.value, query = query.value )
                Log.d(TAG, "search: appending")
                appendRecipes(result)
            }
            loading.value = false
        }
    }

    fun onChangeRecipeScrollPosition(position: Int){
        setListScrollPosition(position)
    }

    fun onChangeGenericDialogInfo(dialogInfo: GenericDialogInfo?){
        genericDialogInfo.value = dialogInfo
    }

    /**
     * Called when a new search is executed.
     */
    private fun resetSearchState(){
        recipes.value = listOf()
        setPage(1)
        setListScrollPosition(0)
        if(selectedCategory.value?.value != query.value) clearSelectedCategory()
    }

    /**
     * Append new recipes to the current list of recipes
     */
    private fun appendRecipes(recipes: List<Recipe>){
//        val current = this.recipes.value
//        val new = listOf(current, recipes).flatten()
//        this.recipes.value = new
        val current = ArrayList(this.recipes.value)
        current.addAll(recipes)
        this.recipes.value = current
    }

    private fun incrementPage(){
        setPage(page.value + 1)
    }

    /**
     * Keep track of what the user has searched
     */
    fun onQueryChanged(query: String){
        setQuery(query)
    }

    private fun clearSelectedCategory(){
        setSelectedCategory(null)
    }

    fun onSelectedCategoryChanged(category: String){
        val newCategory = getFoodCategory(category)
        setSelectedCategory(newCategory)
        onQueryChanged(category)
    }


    fun onChangeCategoryScrollPosition(position: Float){
        categoryScrollPosition = position
    }

    private fun setListScrollPosition(position: Int){
        recipeListScrollPosition = position
        savedStateHandle.set(STATE_KEY_LIST_POSITION, position)
    }

    private fun setPage(page: Int){
        this.page.value = page
        savedStateHandle.set(STATE_KEY_PAGE, page)
    }

    private fun setSelectedCategory(category: FoodCategory?){
        selectedCategory.value = category
        savedStateHandle.set(STATE_KEY_SELECTED_CATEGORY, category)
    }

    private fun setQuery(query: String){
        this.query.value = query
        savedStateHandle.set(STATE_KEY_QUERY, query)
    }

}