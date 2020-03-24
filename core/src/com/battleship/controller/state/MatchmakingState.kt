package com.battleship.controller.state

import com.badlogic.gdx.Gdx
import com.battleship.GameStateManager
import com.battleship.controller.firebase.GameController
import com.battleship.model.Game
import com.battleship.model.ui.GuiObject
import com.battleship.model.ui.Text
import com.battleship.utility.Font
import com.battleship.utility.GUI
import com.battleship.utility.Palette
import com.battleship.view.BasicView
import com.battleship.view.View

class MatchmakingState : GuiState() {
    override var view: View = BasicView()

    private val gameController = GameController()

    private val playerButtons: Array<GuiObject> = arrayOf(*(0..4).map { a: Int -> joinUserButton(a) }.toTypedArray())

    private var page: Int = 0

    private val nextPageButton = GUI.textButton(
            Gdx.graphics.width - 150f,
            30f,
            100f,
            90f,
            "->"
    ) {
        page++
        updateButtons()
    }

    private val previousPageButton = GUI.textButton(
            30f,
            30f,
            100f,
            70f,
            "<-"
    ) {
        page--
        updateButtons()
    }.hide()

    override val guiObjects: List<GuiObject> = listOf(
            GUI.text(
                    20f,
                    Gdx.graphics.height - 220f,
                    Gdx.graphics.width - 40f,
                    90f,
                    "Matchmaking",
                    Font.LARGE_WHITE
            ),
            nextPageButton,
            previousPageButton,
            *playerButtons,
            GUI.backButton
    )
    var games = emptyList<Game>()

    override fun create() {
        super.create()
        games = gameController.getPendingGames()
        updateButtons()
    }

    private fun updateButtons() {
        val index = page * 5
        playerButtons.forEachIndexed { i, _ ->
            val j = index + i
            val button = playerButtons[i]
            if (j < games.size) {
                button.set(Text(games.get(j).player1.playerName))
                button.show()
            } else {
                button.hide()
            }
        }
        if (index + 5 < games.size)
            nextPageButton.show()
        else
            nextPageButton.hide()

        if (index > 0)
            previousPageButton.show()
        else
            previousPageButton.hide()
    }

    private fun joinUserButton(index: Int): GuiObject {
        return GUI.textButton(
                50f,
                Gdx.graphics.height - 300f - index * 75f,
                Gdx.graphics.width - 100f,
                55f,
                "Loading",
                font = Font.TINY_WHITE,
                borderColor = Palette.RED
        ) {
            val gameId = games.get((page * 5) + index).gameId
            val successful = gameController.joinGame(
                    gameId,
                    GameStateManager.userId)
            if (successful) {
                GameStateManager.activeGame = gameController.getGame(gameId)
                GameStateManager.set(PreGameState())
            }
        }
    }

    override fun update(dt: Float) {
    }

    override fun render() {
        this.view.render(*guiObjects.toTypedArray())
    }
}