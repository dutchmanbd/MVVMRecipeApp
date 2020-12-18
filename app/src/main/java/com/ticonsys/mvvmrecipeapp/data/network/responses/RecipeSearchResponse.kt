package com.ticonsys.mvvmrecipeapp.data.network.responses

import com.google.gson.annotations.SerializedName
import com.ticonsys.mvvmrecipeapp.data.network.model.RecipeDto

data class RecipeSearchResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("results")
    val results: List<RecipeDto>
)