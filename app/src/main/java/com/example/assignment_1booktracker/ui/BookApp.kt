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
        // 图书馆页面
        composable(routes.Library.name) {
            LibraryScreen(navController)
        }
        // 添加书籍页面
        composable(routes.AddBook.name) {
            AddBookScreen(navController)
        }
        // 书籍详情页面，传递 bookId 参数
        composable(
            route = "${routes.BookDetails.name}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            BookDetailsScreen(
                navController,
                bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
            )
        }
        // 添加关键点页面，传递 bookId 参数
        composable(
            route = "${routes.AddPoint.name}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
            AddPointScreen(navController, bookId)
        }
        // 编辑关键点页面，传递 bookId 与 pointId 参数
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
        // 推荐页面
        composable(routes.Recommendations.name) {
            RecommendationsScreen(navController)
        }
        // 推荐详情页面，传递 recId 参数
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
