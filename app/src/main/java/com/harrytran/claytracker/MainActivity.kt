package com.harrytran.claytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.harrytran.claytracker.ui.screens.*
import com.harrytran.claytracker.ui.viewmodels.*
import com.harrytran.claytracker.ui.theme.UofACapstoneTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harrytran.claytracker.ui.viewmodels.ViewModelFactory
import com.harrytran.claytracker.ui.viewmodels.SkeetViewModelFactory
import com.harrytran.claytracker.ui.viewmodels.TrapViewModelFactory
import com.harrytran.claytracker.ui.viewmodels.SportingClaysViewModelFactory
import com.harrytran.claytracker.ui.viewmodels.FreeShootViewModelFactory
import com.harrytran.claytracker.ui.viewmodels.MainViewModel
import com.harrytran.claytracker.ui.viewmodels.SessionDetailsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as ClayShootingApplication
            val viewModelFactory = ViewModelFactory(app.repository)
            UofACapstoneTheme {
                MainApp(viewModelFactory)
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Launch : Screen("launch")
    object Stats : Screen("stats")
    object NewSession : Screen("new_session")
    object NewSessionDetails : Screen("session_details/{sport}") {
        fun createRoute(sport: String) = "session_details/$sport"
    }
    object Skeet : Screen("skeet_screen/{sessionId}") {
        fun createRoute(sessionId: Long) = "skeet_screen/$sessionId"
    }
    object Trap : Screen("trap_screen/{sessionId}") {
        fun createRoute(sessionId: Long) = "trap_screen/$sessionId"
    }
    object SportingClays : Screen("sporting_clays_screen/{sessionId}") {
        fun createRoute(sessionId: Long) = "sporting_clays_screen/$sessionId"
    }
    object FreeShoot : Screen("free_shoot_screen/{sessionId}") {
        fun createRoute(sessionId: Long) = "free_shoot_screen/$sessionId"
    }
    object SessionList : Screen("session_list")
    object SessionDetails : Screen("session_details_view/{sessionId}") {
        fun createRoute(sessionId: Long) = "session_details_view/$sessionId"
    }
}

@Composable
fun MainApp(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = (context.applicationContext as ClayShootingApplication).repository
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)
    val latestSession by mainViewModel.inProgressSession.collectAsState(initial = null)

    val onAddOrContinueClick = {
        val route = mainViewModel.getRouteForSession(latestSession)
        navController.navigate(route)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Launch.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Launch.route) {
            LaunchScreen(onTimeout = {
                navController.navigate(Screen.Stats.route) {
                    popUpTo(Screen.Launch.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Stats.route) {
            StatsScreen(
                onStatsClick = { /* Already here */ },
                onAddClick = onAddOrContinueClick,
                onSessionsClick = { navController.navigate(Screen.SessionList.route) },
                viewModel = viewModel(factory = viewModelFactory),
                mainViewModel = mainViewModel
            )
        }
        composable(Screen.SessionList.route) {
            SessionListScreen(
                onStatsClick = { navController.navigate(Screen.Stats.route) },
                onAddClick = onAddOrContinueClick,
                onSessionsClick = { /* Already here */ },
                onSessionClick = { sessionId ->
                    navController.navigate(Screen.SessionDetails.createRoute(sessionId))
                },
                viewModel = viewModel(factory = viewModelFactory),
                mainViewModel = mainViewModel
            )
        }
        composable(
            route = Screen.SessionDetails.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            SessionDetailsScreen(
                viewModel = viewModel(factory = SessionDetailsViewModelFactory(repository, sessionId)),
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.NewSession.route) {
            NewSessionScreen(
                onSportSelected = { sport ->
                    navController.navigate(Screen.NewSessionDetails.createRoute(sport))
                },
                onBack = { navController.popBackStack() },
                onStatsClick = { navController.navigate(Screen.Stats.route) },
                onSessionsClick = { navController.navigate(Screen.SessionList.route) },
                onAddClick = onAddOrContinueClick,
                viewModel = viewModel(factory = viewModelFactory),
                mainViewModel = mainViewModel
            )
        }
        composable(
            route = Screen.NewSessionDetails.route,
            arguments = listOf(navArgument("sport") { type = NavType.StringType })
        ) { backStackEntry ->
            val sport = backStackEntry.arguments?.getString("sport") ?: ""
            val viewModel: NewSessionDetailsViewModel = viewModel(factory = viewModelFactory)
            NewSessionDetailsScreen(
                sport = sport,
                onStartSession = { selectedSport, sessionId ->
                    val route = viewModel.getRouteForSession(selectedSport, sessionId)
                    navController.navigate(route)
                },
                onBack = { navController.popBackStack() },
                onStatsClick = { navController.navigate(Screen.Stats.route) },
                onSessionsClick = { navController.navigate(Screen.SessionList.route) },
                onAddClick = onAddOrContinueClick,
                viewModel = viewModel,
                mainViewModel = mainViewModel
            )
        }
        composable(
            route = Screen.Skeet.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            NewSkeetSessionScreen(
                viewModel = viewModel(factory = SkeetViewModelFactory(
                    repository,
                    sessionId
                )),
                onBack = {
                    navController.navigate(Screen.Stats.route) {
                        popUpTo(Screen.Stats.route) { inclusive = true }
                    }
                },
                onSessionFinished = { sessionId ->
                    navController.navigate(Screen.SessionDetails.createRoute(sessionId)) {
                        popUpTo(Screen.Stats.route) { inclusive = false }
                    }
                }
            )
        }
        composable(
            route = Screen.Trap.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            NewTrapSessionScreen(
                viewModel = viewModel(factory = TrapViewModelFactory(
                    repository,
                    sessionId
                )),
                onBack = {
                    navController.navigate(Screen.Stats.route) {
                        popUpTo(Screen.Stats.route) { inclusive = true }
                    }
                },
                onSessionFinished = { sessionId ->
                    navController.navigate(Screen.SessionDetails.createRoute(sessionId)) {
                        popUpTo(Screen.Stats.route) { inclusive = false }
                    }
                }
            )
        }
        composable(
            route = Screen.SportingClays.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            NewSportingClaysSessionScreen(
                viewModel = viewModel(factory = SportingClaysViewModelFactory(
                    repository,
                    sessionId
                )),
                onBack = {
                    navController.navigate(Screen.Stats.route) {
                        popUpTo(Screen.Stats.route) { inclusive = true }
                    }
                },
                onSessionFinished = { sessionId ->
                    navController.navigate(Screen.SessionDetails.createRoute(sessionId)) {
                        popUpTo(Screen.Stats.route) { inclusive = false }
                    }
                }
            )
        }
        composable(
            route = Screen.FreeShoot.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            NewFreeShootScreen(
                viewModel = viewModel(factory = FreeShootViewModelFactory(
                    repository,
                    sessionId
                )),
                onBack = {
                    navController.navigate(Screen.Stats.route) {
                        popUpTo(Screen.Stats.route) { inclusive = true }
                    }
                },
                onSessionFinished = { sessionId ->
                    navController.navigate(Screen.SessionDetails.createRoute(sessionId)) {
                        popUpTo(Screen.Stats.route) { inclusive = false }
                    }
                }
            )
        }
    }
}
