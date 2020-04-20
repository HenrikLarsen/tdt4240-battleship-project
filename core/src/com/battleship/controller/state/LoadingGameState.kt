package com.battleship.controller.state

import com.battleship.GSM
import com.battleship.controller.firebase.FirebaseController
import com.battleship.model.ui.GuiObject
import com.battleship.model.ui.Text
import com.battleship.utility.Font
import com.battleship.utility.GUI
import com.battleship.view.PlayView
import com.battleship.view.View

class LoadingGameState(private var controller: FirebaseController) : GuiState(controller) {
    override var view: View = PlayView()
    var showDialog: Boolean = false
    var opponentLeftRenders: Int = 0

    private val header: GuiObject = GUI.header("Waiting for opponent to join...")

    private fun leaveGame() {
        controller.leaveGame(GSM.activeGame!!.gameId, GSM.userId) {
            GSM.resetGame()
            controller.addPendingGamesListener { pendingGames ->
                GSM.pendingGames = pendingGames
            }
            GSM.set(MainMenuState(controller))
        }
    }

    private val opponentLeftDialog = GUI.dialog(
        "${GSM.activeGame!!.opponent.playerName} left the game before registering treasures.",
        listOf(Pair("Find new game", {
            showDialog = false
            leaveGame()
        }))
    )

    override val guiObjects: List<GuiObject> = listOf(
        header,
        GUI.backButton { leaveGame() },
        *opponentLeftDialog
    )

    override fun render() {
        this.view.render(*guiObjects.toTypedArray())

    }

    override fun update(dt: Float) {
        if (GSM.activeGame!!.opponentLeft) {
            if (opponentLeftRenders < 2) opponentLeftRenders++
            if (opponentLeftRenders == 1) showDialog = true // First opponent left render
        }
        if (showDialog) opponentLeftDialog.forEach { guiObject -> guiObject.show() }
        else opponentLeftDialog.forEach { guiObject -> guiObject.hide() }

        // Game is ready!
        if (GSM.activeGame!!.gameReady) GSM.set(PlayState(controller))

        updateHeaderText()
    }

    private fun updateHeaderText() {
        if (GSM.activeGame!!.opponent.playerId == "")
            header.set(Text("Waiting for opponent to join...", font = Font.MEDIUM_WHITE))
        else if (GSM.activeGame!!.opponent.board.treasures.isEmpty())
            header.set(Text("Waiting for ${GSM.activeGame!!.opponent.playerName} to register treasures...", font = Font.MEDIUM_WHITE))
    }

    override fun dispose() {
        super.dispose()
        // controller.detachGameListener(GSM.activeGame!!.gameId)
    }
}
