package com.ticonsys.mvvmrecipeapp.data.repositories

import com.ticonsys.mvvmrecipeapp.data.domain.model.Recipe
import com.ticonsys.mvvmrecipeapp.data.network.model.RecipeDto
import com.ticonsys.mvvmrecipeapp.data.network.model.RecipeDtoMapper
import com.ticonsys.mvvmrecipeapp.data.network.retrofit.RecipeService

class RecipeRepositoryImpl(
    private val apiService: RecipeService,
    private val mapper: RecipeDtoMapper,
) : RecipeRepository {
    override suspend fun search(token: String, page: Int, query: String): List<Recipe> {
        return mapper.toDomainList(
            apiService.search(token, page, query)?.results ?: emptyList()
        )
    }

    override suspend fun get(token: String, id: Int): Recipe {
        return mapper.mapToDomainModel(
            apiService.get(token, id) ?: RecipeDto()
        )
    }
}