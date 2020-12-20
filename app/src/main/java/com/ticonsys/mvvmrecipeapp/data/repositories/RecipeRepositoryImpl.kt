package com.ticonsys.mvvmrecipeapp.data.repositories

import com.ticonsys.mvvmrecipeapp.data.domain.model.Recipe
import com.ticonsys.mvvmrecipeapp.data.network.model.RecipeDto
import com.ticonsys.mvvmrecipeapp.data.network.model.RecipeDtoMapper
import com.ticonsys.mvvmrecipeapp.data.network.retrofit.RecipeService
import java.io.IOException
import java.net.SocketTimeoutException

class RecipeRepositoryImpl(
    private val apiService: RecipeService,
    private val mapper: RecipeDtoMapper,
) : RecipeRepository {
    override suspend fun search(token: String, page: Int, query: String): List<Recipe> {
        val response = try {
            apiService.search(token, page, query)?.results
        } catch (e: IOException){
            emptyList()
        } catch (e: SocketTimeoutException){
            emptyList()
        } catch (e: Exception){
            emptyList()
        }
        return mapper.toDomainList(
            response ?: emptyList()
        )
    }

    override suspend fun get(token: String, id: Int): Recipe {
        val response = try {
            apiService.get(token, id)
        } catch (e: IOException){
            RecipeDto()
        } catch (e: SocketTimeoutException){
            RecipeDto()
        } catch (e: Exception){
            RecipeDto()
        }
        return mapper.mapToDomainModel(
            response ?: RecipeDto()
        )
    }
}