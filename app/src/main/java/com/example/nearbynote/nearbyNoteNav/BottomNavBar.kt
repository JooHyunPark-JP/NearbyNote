package com.example.nearbynote.nearbyNoteNav

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nearbynote.R

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.Main,
        Screen.MapboxScreen,
        Screen.SavedAddressScreen,
        Screen.PermissionStatusScreen
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
                            painterResource(id = R.drawable.ic_list_check_icon),
                            modifier = Modifier.size(28.dp),
                            contentDescription = null
                        )

                        is Screen.MapboxScreen -> Icon(
                            painterResource(id = R.drawable.ic_map_search),
                            contentDescription = null
                        )

                        is Screen.SavedAddressScreen -> Icon(
                            painterResource(id = R.drawable.ic_favorite_addresses),
                            contentDescription = null
                        )

                        is Screen.PermissionStatusScreen -> Icon(
                            painterResource(id = R.drawable.ic_settings),
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

                label = { Text(text = screen.label) },
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