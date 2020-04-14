package com.battleship.controller.state

import com.battleship.GameStateManager
import com.battleship.model.ui.GuiObject
import com.battleship.utility.GUI
import com.battleship.view.BasicView
import com.battleship.view.View

/**
 * State handling all logic related to the game over screen
 */
class GameOverState(win: Boolean) : GuiState() {
    private val menuList = listOf(
        Pair("Main Menu") { GameStateManager.set(MainMenuState()) },
        Pair("Play Again") { GameStateManager.set(PreGameState()) }
    )
    var winString = ""
        init {
            if (win){
                winString = "You won the game!"
            }else{
                winString = "You lost the game..."
            }

    }
    override val guiObjects: List<GuiObject> = listOf(
        GUI.menuButton(
            23.4375f,
            25f,
            "Back to main menu",
            onClick = { GameStateManager.set(MainMenuState()) }
        ),
        GUI.menuButton(
            23.4375f,
            43.75f,
            "Play again",
            onClick = { GameStateManager.set(MatchmakingState()) }
        ),
        GUI.header(
            winString
        )
    )

    override var view: View = BasicView()

    override fun update(dt: Float) {}

    override fun render() {
        view.render(*guiObjects.toTypedArray())
    }
}
