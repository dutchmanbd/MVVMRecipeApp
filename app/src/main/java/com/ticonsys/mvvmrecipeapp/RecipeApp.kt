package com.ticonsys.mvvmrecipeapp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RecipeApp : Application(){

    var isLight by mutableStateOf(true)

    fun toggleLightTheme(){
        isLight = !isLight
    }

}