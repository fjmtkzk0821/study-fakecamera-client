package com.kazuki.fakecameraclient.util

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.kazuki.fakecameraclient.NavigationDirections
import com.kazuki.fakecameraclient.data.NavigationCommand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class NavigationManager {
    var commands = MutableStateFlow(NavigationDirections.Empty)

    fun navigate(direction: NavigationCommand) {
        commands.value = direction
    }
}

object NavCommands {
    object Camera {
        private val KEY_IP = "ip"
        val route = "camera?$KEY_IP={$KEY_IP}"
        val args = listOf<NamedNavArgument>(
            navArgument(KEY_IP) {
                type = NavType.StringType
                nullable = true
            }
        )

        fun get(ip: String? = null) = object : NavigationCommand {
            override val args = Camera.args
            override val route = "camera?$KEY_IP=$ip"
        }
    }

}