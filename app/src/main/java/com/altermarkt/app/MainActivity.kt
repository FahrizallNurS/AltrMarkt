package com.altermarkt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.altermarkt.app.ui.AlterMarketNavHost
import com.altermarkt.app.ui.theme.AlterMarktTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlterMarktTheme {
                val navController = rememberNavController()

                AlterMarketNavHost(
                    navController = navController,
                    isLoggedIn    = false
                )
            }
        }
    }
}