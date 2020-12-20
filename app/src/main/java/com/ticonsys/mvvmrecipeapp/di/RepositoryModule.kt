package com.ticonsys.mvvmrecipeapp.di

import com.ticonsys.mvvmrecipeapp.data.network.model.RecipeDtoMapper
import com.ticonsys.mvvmrecipeapp.data.network.retrofit.RecipeService
import com.ticonsys.mvvmrecipeapp.data.repositories.RecipeRepository
import com.ticonsys.mvvmrecipeapp.data.repositories.RecipeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRecipeRepository(
        apiService: RecipeService,
        mapper: RecipeDtoMapper
    ): RecipeRepository = RecipeRepositoryImpl(apiService, mapper)

}