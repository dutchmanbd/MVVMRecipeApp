package com.ticonsys.mvvmrecipeapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.ticonsys.mvvmrecipeapp.R
import com.ticonsys.mvvmrecipeapp.RecipeApp
import com.ticonsys.mvvmrecipeapp.data.domain.model.Recipe
import com.ticonsys.mvvmrecipeapp.ui.activities.MainViewModel
import com.ticonsys.mvvmrecipeapp.ui.activities.PAGE_SIZE
import com.ticonsys.mvvmrecipeapp.ui.components.*
import com.ticonsys.mvvmrecipeapp.ui.components.util.GenericDialogInfo
import com.ticonsys.mvvmrecipeapp.ui.components.util.SnackbarController
import com.ticonsys.mvvmrecipeapp.ui.fragments.recipe_list.FoodCategory
import com.ticonsys.mvvmrecipeapp.ui.fragments.recipe_list.RecipeListEvent.*
import com.ticonsys.mvvmrecipeapp.ui.fragments.recipe_list.getAllFoodCategories
import com.ticonsys.mvvmrecipeapp.ui.theme.AppTheme
import com.ticonsys.mvvmrecipeapp.ui.theme.Black5
import com.ticonsys.mvvmrecipeapp.ui.theme.Grey1
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeListFragment : Fragment() {


    @Inject
    lateinit var application: RecipeApp

    @Inject
    lateinit var snackBarController: SnackbarController

    private val viewModel by activityViewModels<MainViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val displayProgressBar = viewModel.loading.value
                val selectedCategory = viewModel.selectedCategory.value
                val categories = getAllFoodCategories()
                val recipes = viewModel.recipes.value
                val query = viewModel.query.value
                val page = viewModel.page.value

                // For error dialog
                val errorTitle = stringResource(id = R.string.error)

                val okActionLabel = stringResource(id = R.string.ok)
                val genericDialogInfo = viewModel.genericDialogInfo.value
                val snackBarActionLabel = stringResource(id = R.string.dismiss)

                AppTheme(
                    darkTheme = !application.isLight,
                    progressBarIsDisplayed = displayProgressBar
                ) {
                    val scaffoldState = rememberScaffoldState()
                    Scaffold(
                        topBar = {
                            SearchAppBar(
                                query = query,
                                onQueryChanged = viewModel::onQueryChanged,
                                onExecuteSearch = {
                                    viewModel.onTriggerEvent(NewSearchEvent())
                                },
                                categories = categories,
                                selectedCategory = selectedCategory,
                                onSelectedCategoryChanged = viewModel::onSelectedCategoryChanged,
                                scrollPosition = viewModel.categoryScrollPosition,
                                onChangeScrollPosition = viewModel::onChangeCategoryScrollPosition,
                                onToggleTheme = application::toggleLightTheme,
                                onError = {
                                    // Can use a snackbar or dialog here. Your choice.
                                    snackBarController.handleSnackbarError(
                                        scaffoldState = scaffoldState,
                                        message = it,
                                        actionLabel = snackBarActionLabel
                                    )
                                })
                        },
                        scaffoldState = scaffoldState,
                        snackbarHost = {
                            scaffoldState.snackbarHostState
                        }
                    ) {
                        Column(
                            modifier = Modifier.background(color = if (application.isLight) Grey1 else Black5)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (displayProgressBar && recipes.isEmpty()) {
                                    Column {
                                        HorizontalDottedProgressBar()
                                        LoadingRecipeListShimmer(200)
                                    }
                                } else RecipeList(
                                    recipes = recipes,
                                    page = page,
                                    onNextPage = {
                                        viewModel.onTriggerEvent(NextPageEvent())
                                    },
                                    onSelectRecipe = {
                                        findNavController().navigate(
                                            R.id.viewRecipe, bundleOf(
                                                "recipe_id" to it
                                            )
                                        )
                                    },
                                    onError = {
                                        snackBarController.handleSnackbarError(
                                            scaffoldState = scaffoldState,
                                            message = it,
                                            actionLabel = snackBarActionLabel
                                        )
                                    },
                                    onChangeScrollPosition = viewModel::onChangeRecipeScrollPosition
                                )
                                ErrorSnackBar(
                                    snackBarHostState = scaffoldState.snackbarHostState,
                                    onDismiss = {
                                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                    },
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )
                                genericDialogInfo?.let { dialogInfo ->
                                    GenericDialogInfo(
                                        onDismiss = dialogInfo.onDismiss,
                                        title = dialogInfo.title,
                                        description = dialogInfo.description,
                                        positiveBtnTxt = dialogInfo.positiveBtnTxt,
                                        onPositiveAction = dialogInfo.onPositiveAction,
                                        negatveBtnTxt = dialogInfo.negatveBtnTxt,
                                        onNegativeAction = dialogInfo.onNegativeAction
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }


    @ExperimentalCoroutinesApi
    @Composable
    fun RecipeList(
        recipes: List<Recipe>,
        page: Int,
        onNextPage: () -> Unit,
        isLoading: Boolean = false,
        onSelectRecipe: (Int) -> Unit,
        onError: (String) -> Unit,
        onChangeScrollPosition: (Int) -> Unit,
    ) {
        LazyColumn()
        {
            itemsIndexed(
                items = recipes
            ) { index, recipe ->
                onChangeScrollPosition(index)
                Log.d("RecipeListFragment", "RecipeList: index: ${index}")
                if ((index + 1) >= (page * PAGE_SIZE) && !isLoading) {
                    onNextPage()
                }
                RecipeCard(
                    recipe = recipe,
                    onClick = {
                        recipe.id?.let {
                            onSelectRecipe(it)
                        } ?: onError("Error. There's something wrong with that recipe.")
                    }
                )
            }
        }
    }


    @Composable
    fun SearchAppBar(
        query: String,
        onQueryChanged: (String) -> Unit,
        onExecuteSearch: () -> Unit,
        categories: List<FoodCategory>,
        selectedCategory: FoodCategory?,
        onSelectedCategoryChanged: (String) -> Unit,
        scrollPosition: Float,
        onChangeScrollPosition: (Float) -> Unit,
        onToggleTheme: () -> Unit,
        onError: (String) -> Unit,
    ) {
        Surface(
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colors.secondary,
            elevation = 8.dp,
            content = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .padding(8.dp),
                            value = query,
                            onValueChange = {
                                onQueryChanged(it)
                            },
                            label = {
                                Text(text = "Search")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done,
                            ),
                            leadingIcon = { Icon(Icons.Filled.Search) },
                            onImeActionPerformed = { action, softKeyboardController ->
                                if (action == ImeAction.Done) {
                                    onExecuteSearch()
                                    softKeyboardController?.hideSoftwareKeyboard()
                                }
                            },
                            textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
                            backgroundColor = MaterialTheme.colors.surface
                        )
                        ConstraintLayout(
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            val (menu) = createRefs()
                            IconButton(
                                modifier = Modifier
                                    .constrainAs(menu) {
                                        end.linkTo(parent.end)
                                        linkTo(top = parent.top, bottom = parent.bottom)
                                    },
                                onClick = onToggleTheme,
                            ) {
                                Icon(Icons.Filled.MoreVert)
                            }
                        }
                    }
                    val scrollState = rememberScrollState()
                    ScrollableRow(
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 8.dp),
                        scrollState = scrollState,
                    ) {

                        // restore scroll position after rotation
                        scrollState.scrollTo(scrollPosition)

                        // display FoodChips
                        for (category in categories) {
                            FoodCategoryChip(
                                category = category.value,
                                isSelected = selectedCategory == category,
                                onSelectedCategoryChanged = {
                                    onChangeScrollPosition(scrollState.value)
                                    onSelectedCategoryChanged(it)
                                },
                                onExecuteSearch = onExecuteSearch,
                                onError = onError,
                            )
                        }
                    }
                }
            }
        )
    }

}