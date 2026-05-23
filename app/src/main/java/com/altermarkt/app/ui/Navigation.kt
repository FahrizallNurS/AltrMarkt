package com.altermarkt.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalContext
import com.altermarkt.app.data.local.AppDatabase
import com.altermarkt.app.data.repository.HomeProductRepository
import com.altermarkt.app.data.repository.SaveRepo
import com.altermarkt.app.ui.auth.LoginScreen
import com.altermarkt.app.ui.auth.RegisterScreen
import com.altermarkt.app.ui.screen.DetailScreen
import com.altermarkt.app.ui.screen.HomeScreen
import com.altermarkt.app.ui.viewmodel.DetailViewModel
import com.altermarkt.app.ui.viewmodel.HomeViewModel
import com.altermarkt.app.ui.viewmodel.SavedVM
import com.altermarkt.app.ui.viewmodel.SavedVMFactory

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
    const val EDIT     = "edit/{productId}"

    fun detail(productId: String) = "detail/$productId"
    fun edit(productId: String) = "edit/$productId"
}

@Composable
fun AlterMarketNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    db: AppDatabase
) {
    val startDestination = if (isLoggedIn) Routes.HOME else Routes.LOGIN

    val context = LocalContext.current
    val repo    = HomeProductRepository(db)

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        // ── AUTH ──────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }

        // ── HOME ──────────────────────────────────
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = HomeViewModel(repo, db),
                onProductClick = { id -> navController.navigate(Routes.detail(id)) },
                onSearchClick = { navController.navigate(Routes.SEARCH) }
            )
        }

        // ── SEARCH ────────────────────────────────
        composable(Routes.SEARCH) { }

        // ── DETAIL ────────────────────────────────
        composable(Routes.DETAIL) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val viewModel: DetailViewModel = viewModel(
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

        // ── POSTING ───────────────────────────────
        composable(Routes.POSTING) {
            PostingScreen()
        }

        // ── SAVED ─────────────────────────────────
        composable(Routes.SAVED) {
            val database = AppDatabase.getInstance(context)
            val dao = database.savedProductDao()
            val saveRepo = SaveRepo(dao)
            val savedViewModel: SavedVM = viewModel(
                factory = SavedVMFactory(saveRepo)
            )
            SavedScreen(viewModel = savedViewModel)
        }

        // ── PROFILE ───────────────────────────────
        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onEditProductClick = { productId ->
                    navController.navigate(Routes.edit(productId))
                }
            )
        }

        // ── KELOLA ────────────────────────────────
        composable(Routes.KELOLA) { }


        // --- TAMBAHKAN BLOK INI ---
        composable(Routes.EDIT) { backStackEntry ->
            // Tangkap ID produk yang dikirim dari halaman profil
            val productId = backStackEntry.arguments?.getString("productId") ?: ""

            // Panggil file EditScreen milik Anda di sini
            EditScreen(
                productId = productId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}