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
import com.example.assignment_1booktracker.ui.screens.AddPointScreen
import com.example.assignment_1booktracker.ui.screens.BookDetailsScreen
import com.example.assignment_1booktracker.ui.screens.EditPointScreen
import com.example.assignment_1booktracker.ui.screens.LibraryScreen
import com.example.assignment_1booktracker.ui.screens.RecDetailsScreen
import com.example.assignment_1booktracker.ui.screens.RecommendationsScreen

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
        //Library page
        composable(routes.Library.name) {
            LibraryScreen(navController)
        }
        //Add Book Page
        composable(routes.AddBook.name) {
            AddBookScreen(navController)
        }
        //The details page of the book, passing the bookId parameter
        composable(
            route = "${routes.BookDetails.name}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            BookDetailsScreen(
                navController,
                bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
            )
        }
        //critical point page and pass the bookId parameter
        composable(
            route = "${routes.AddPoint.name}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
            AddPointScreen(navController, bookId)
        }
        composable(
            route = "${routes.EditPoint.name}/{bookId}/{pointId}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.IntType },
                navArgument("pointId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
            val pointId = backStackEntry.arguments?.getInt("pointId") ?: 0
            EditPointScreen(navController, bookId, pointId)
        }
        //Recommendation Page
        composable(routes.Recommendations.name) {
            RecommendationsScreen(navController)
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
