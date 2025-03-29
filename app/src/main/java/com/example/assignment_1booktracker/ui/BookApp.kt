package com.example.assignment_1booktracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment_1booktracker.routes.routes
import com.example.assignment_1booktracker.ui.screens.AddBookScreen
import com.example.assignment_1booktracker.ui.screens.BookDetailsScreen
import com.example.assignment_1booktracker.ui.screens.LibraryScreen
import com.example.assignment_1booktracker.ui.screens.RecDetailsScreen
import com.example.assignment_1booktracker.ui.screens.RecommendationsScreen

/**
 * 该文件只负责定义导航结构，不包含 UI 组件（如按钮、导航栏等）
 */
@Composable
fun BookAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = routes.Library.name,
        modifier = modifier
    ) {
        composable(routes.Library.name) { LibraryScreen(navController) }
        composable(routes.Recommendations.name) { RecommendationsScreen(navController) }
        composable(routes.AddBook.name) { AddBookScreen(navController) }
        composable(
            route = "${routes.BookDetails.name}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            BookDetailsScreen(
                navController,
                bookId = backStackEntry.arguments?.getInt("bookId")
            )
        }
        composable(
            route = "${routes.RecDetails.name}/{recId}",
            arguments = listOf(navArgument("recId") { type = NavType.IntType })
        ) { backStackEntry ->
            RecDetailsScreen(
                navController,
                recId = backStackEntry.arguments?.getInt("recId")
            )
        }
    }
}