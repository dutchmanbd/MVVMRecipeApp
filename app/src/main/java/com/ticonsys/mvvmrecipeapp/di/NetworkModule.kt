package com.ticonsys.mvvmrecipeapp.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ticonsys.mvvmrecipeapp.data.network.model.RecipeDtoMapper
import com.ticonsys.mvvmrecipeapp.data.network.retrofit.RecipeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRecipeMapper() = RecipeDtoMapper()

    @Singleton
    @Provides
    fun provideGson() = GsonBuilder().create()


    @Singleton
    @Provides
    fun provideOkHttpClient() =
        OkHttpClient.Builder()
            .build()

    @Singleton
    @Provides
    fun provideRecipeService(
        gson: Gson,
        okHttpClient: OkHttpClient
    ) = Retrofit.Builder()
        .baseUrl("https://food2fork.ca/api/recipe/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(RecipeService::class.java)


    /**
     * I might include proper authentication later on food2fork.ca
     * For now just hard code a token.
     */
    @Singleton
    @Provides
    @Named("auth_token")
    fun provideAuthToken(): String{
        return "Token 9c8b06d329136da358c2d00e76946b0111ce2c48"
    }
}