package com.battleship.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.battleship.controller.BattleshipGame

/**
 * DesktopLauncher contains main method for launching the Game as a desktop application
 */
object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.height = 700
        config.width = 500
        LwjglApplication(BattleshipGame(DesktopFirebase), config)
    }
}
