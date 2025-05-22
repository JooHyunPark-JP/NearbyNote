package com.example.nearbynote.nearbyNoteNav

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nearbynote.R

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.Main,
        Screen.WriteNoteScreen,
        Screen.Main3,
        Screen.Main4,
    )
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        is Screen.Main -> Icon(
                            painterResource(id = R.drawable.ic_placeholder_icon),
                            contentDescription = null
                        )

                        is Screen.WriteNoteScreen -> Icon(
                            painterResource(id = R.drawable.ic_placeholder_icon),
                            contentDescription = null
                        )

                        is Screen.Main3 -> Icon(
                            painterResource(id = R.drawable.ic_placeholder_icon),
                            contentDescription = null
                        )

                        is Screen.Main4 -> Icon(
                            painterResource(id = R.drawable.ic_placeholder_icon),
                            contentDescription = null
                        )

                        else -> {
                            Icon(
                                painterResource(id = R.drawable.ic_placeholder_icon),
                                contentDescription = null
                            )
                        }
                    }
                },

                label = { Text(text = screen.route.replaceFirstChar { it.uppercase() }) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }
}