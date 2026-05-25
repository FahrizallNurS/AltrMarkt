package com.altermarkt.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.currentBackStackEntryAsState

val DarkNavBg = Color(0xFF1A1A1A)
val NavSelected = Color(0xFF7C5CFC)
val NavUnselected = Color(0xFF666666)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = DarkNavBg,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavSelected,
                selectedTextColor = NavSelected,
                unselectedIconColor = NavUnselected,
                unselectedTextColor = NavUnselected,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = currentRoute == Routes.SEARCH,
            onClick = { navController.navigate(Routes.SEARCH) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavSelected,
                selectedTextColor = NavSelected,
                unselectedIconColor = NavUnselected,
                unselectedTextColor = NavUnselected,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.POSTING) },
            icon = {
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = NavSelected,
                    tonalElevation = 4.dp
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Posting",
                        tint = Color.White,
                        modifier = androidx.compose.ui.Modifier.padding(12.dp)
                    )
                }
            },
            label = { Text("Posting") },
            colors = NavigationBarItemDefaults.colors(
                unselectedTextColor = NavUnselected,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = currentRoute == Routes.SAVED,
            onClick = { navController.navigate(Routes.SAVED) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Bookmark, contentDescription = "Save") },
            label = { Text("Save") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavSelected,
                selectedTextColor = NavSelected,
                unselectedIconColor = NavUnselected,
                unselectedTextColor = NavUnselected,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = currentRoute == Routes.PROFILE,
            onClick = { navController.navigate(Routes.PROFILE) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavSelected,
                selectedTextColor = NavSelected,
                unselectedIconColor = NavUnselected,
                unselectedTextColor = NavUnselected,
                indicatorColor = Color.Transparent
            )
        )
    }
}