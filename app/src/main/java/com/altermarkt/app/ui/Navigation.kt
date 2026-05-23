package com.altermarkt.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.altermarkt.app.data.local.AppDatabase
import com.altermarkt.app.data.repository.HomeProductRepository
import com.altermarkt.app.ui.auth.LoginScreen
import com.altermarkt.app.ui.auth.RegisterScreen
import com.altermarkt.app.ui.screen.DetailScreen
import com.altermarkt.app.ui.screen.HomeScreen
import com.altermarkt.app.ui.viewmodel.DetailViewModel
import com.altermarkt.app.ui.viewmodel.HomeViewModel

object Routes {
    const val LOGIN    = "login"
    const val REGISTER = "register"
    const val HOME     = "home"
    const val SEARCH   = "search"
    const val POSTING  = "posting"
    const val SAVED    = "saved"
    const val PROFILE  = "profile"
    const val KELOLA   = "kelola"
    const val DETAIL   = "detail/{productId}"

    fun detail(productId: String) = "detail/$productId"
}

@Composable
fun AlterMarketNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    db: AppDatabase
) {
    val startDestination = if (isLoggedIn) Routes.HOME else Routes.LOGIN
    val repo = HomeProductRepository(db)

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = HomeViewModel(repo, db),
                onProductClick = { id -> navController.navigate(Routes.detail(id))},
                onSearchClick = { navController.navigate(Routes.SEARCH)}
            )
        }
        composable(Routes.SEARCH) { }
        composable(Routes.DETAIL) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "1"
            val viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return DetailViewModel(repo) as T
                    }
                }
            )
            DetailScreen(
                productId = productId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.POSTING) { }
        composable(Routes.SAVED) { }
        composable(Routes.PROFILE) { }
        composable(Routes.KELOLA) { }
    }
}