package com.ticonsys.mvvmrecipeapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.ui.tooling.preview.Preview
import com.bumptech.glide.Glide
import com.ticonsys.mvvmrecipeapp.R
import com.ticonsys.mvvmrecipeapp.ui.activities.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : Fragment() {


    //    private val viewModel by navGraphViewModels<MainViewModel>(R.id.nav_graph_main)
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recipe List",
                        style = TextStyle(
                            fontSize = TextUnit.Sp(22)
                        )
                    )
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    setRecipeList()
                }
            }
        }
    }


    @Composable
    private fun setRecipeList() {
        ScrollableColumn() {
            val recipes = viewModel.recipes.value
            for (recipe in recipes) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    Text(
                        text = recipe.title ?: "",
                        style = TextStyle(
                            fontSize = TextUnit.Sp(16)
                        )
                    )
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Button(
                        onClick = {

                            val recipeId = recipe.id ?: return@Button

                            findNavController().navigate(
                                R.id.viewRecipe, bundleOf(
                                    "recipe_id" to recipeId
                                )
                            )
                        },
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    ) {
                        Text(text = "View")
                    }
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
    }
}