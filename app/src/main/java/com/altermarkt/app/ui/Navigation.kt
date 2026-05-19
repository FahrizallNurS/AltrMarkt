package com.altermarkt.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.altermarkt.app.ui.auth.LoginScreen
import com.altermarkt.app.ui.auth.RegisterScreen

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
        composable(Routes.POSTING) { }
        composable(Routes.SAVED) { }
        composable(Routes.PROFILE) { }
        composable(Routes.KELOLA) { }
    }
}