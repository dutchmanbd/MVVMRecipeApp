package com.ticonsys.mvvmrecipeapp.internal

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

fun AppCompatActivity.findNavController(@IdRes resId: Int) = (supportFragmentManager.findFragmentById(resId) as NavHostFragment).navController