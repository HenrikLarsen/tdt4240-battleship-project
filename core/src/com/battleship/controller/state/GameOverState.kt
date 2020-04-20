package com.battleship.controller.state

import com.battleship.GSM
import com.battleship.controller.firebase.FirebaseController
import com.battleship.model.ui.GuiObject
import com.battleship.utility.GUI
import com.battleship.view.BasicView
import com.battleship.view.View

/**
 * State handling all logic related to the game over screen
 */
class GameOverState(private val controller: FirebaseController) : GuiState(controller) {
    private val menuList = listOf(
        Pair("Main Menu") { GSM.set(MainMenuState(controller)) },
        Pair("Play Again") { GSM.set(PreGameState(controller)) }
    )

    override val guiObjects: List<GuiObject> = listOf(
        GUI.menuButton(
            25f,
            32f,
            "Back to main menu",
            onClick = { GSM.set(MainMenuState(controller)) }
        ),
        GUI.menuButton(
            25f,
            54f,
            "Play again",
            onClick = { GSM.set(MatchmakingState(controller)) }
        ),
        GUI.header(
            GSM.activeGame!!.winner + " won!"
        )
    )

    override var view: View = BasicView()

    override fun update(dt: Float) {}

    override fun render() {
        view.render(*guiObjects.toTypedArray())
    }
}
