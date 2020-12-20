package com.ticonsys.mvvmrecipeapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ticonsys.mvvmrecipeapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

//    private val navController by lazy {
//        findNavController(binding.navHostMain.id)
//    }
//    private val viewModel by lazy {
//        ViewModelProvider(
//            navController.getViewModelStoreOwner(R.id.nav_graph_main),
//            defaultViewModelProviderFactory
//        ).get(MainViewModel::class.java)
//    }

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        viewModel.searchRecipes(1, "chicken")
    }
}