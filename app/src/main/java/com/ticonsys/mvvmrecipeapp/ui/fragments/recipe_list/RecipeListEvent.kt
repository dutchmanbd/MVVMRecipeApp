package com.ticonsys.mvvmrecipeapp.ui.fragments.recipe_list

sealed class RecipeListEvent {

    class NewSearchEvent: RecipeListEvent()

    class NextPageEvent: RecipeListEvent()

    // restore after process death
    class RestoreStateEvent: RecipeListEvent()
}