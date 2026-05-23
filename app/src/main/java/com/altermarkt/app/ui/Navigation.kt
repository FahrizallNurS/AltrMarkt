package com.altermarkt.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext // Tambahan Import
import androidx.lifecycle.viewmodel.compose.viewModel // Tambahan Import
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.altermarkt.app.data.local.AppDatabase // Tambahan Import
import com.altermarkt.app.data.repository.SaveRepo // Tambahan Import
import com.altermarkt.app.ui.auth.LoginScreen
import com.altermarkt.app.ui.auth.RegisterScreen
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

    fun detail(productId: String) = "detail/$productId"
}

@Composable
fun AlterMarketNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    val startDestination = if (isLoggedIn) Routes.HOME else Routes.LOGIN

    val context = LocalContext.current

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
        composable(Routes.HOME) { }
        composable(Routes.SEARCH) { }
        composable(Routes.DETAIL) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
        }
        composable(Routes.POSTING) {
            PostingScreen() // <-- Tambahkan baris ini!
        }

        // --- INTEGRASI HALAMAN TERSIMPAN (SAVED) ---
        composable(Routes.SAVED) {
            // 1. Inisialisasi Database, Dao, dan Repo
            val database = AppDatabase.getInstance(context)
            val dao = database.savedProductDao()
            val repo = SaveRepo(dao)

            // 2. Buat ViewModel
            val savedViewModel: SavedVM = viewModel(factory = SavedVMFactory(repo))

            // 3. Panggil Halaman Anda
            SavedScreen(viewModel = savedViewModel)
        }

        composable(Routes.PROFILE) {
            ProfileScreen()
        }
        composable(Routes.KELOLA) { }
    }
}