package com.kazuki.fakecameraclient.data

import androidx.navigation.NamedNavArgument

interface NavigationCommand {
    val args: List<NamedNavArgument>
    val route: String
}