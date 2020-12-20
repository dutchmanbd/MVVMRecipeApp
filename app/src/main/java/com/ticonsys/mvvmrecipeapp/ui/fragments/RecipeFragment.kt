package com.ticonsys.mvvmrecipeapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.ticonsys.mvvmrecipeapp.ui.activities.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeFragment : Fragment(){

    private val args by navArgs<RecipeFragmentArgs>()

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                viewModel.getRecipe(args.recipeId)
                val recipe = viewModel.recipe.value
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recipe",
                        style = TextStyle(
                            fontSize = TextUnit.Sp(22)
                        )
                    )
                    Spacer(modifier = Modifier.padding(top = 24.dp))
                    Text(
                        text = recipe.title ?: "",
                        style = TextStyle(
                            fontSize = TextUnit.Sp(16)
                        )
                    )
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text(
                        text = recipe.description ?: "",
                        style = TextStyle(
                            fontSize = TextUnit.Sp(12)
                        )
                    )
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
    }
}