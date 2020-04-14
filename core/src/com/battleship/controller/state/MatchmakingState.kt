package com.battleship.controller.state

import com.battleship.GSM
import com.battleship.model.GameListObject
import com.battleship.controller.firebase.FirebaseController
import com.battleship.model.ui.GuiObject
import com.battleship.model.ui.Text
import com.battleship.utility.GUI
import com.battleship.view.BasicView
import com.battleship.view.View

class MatchmakingState(private val controller : FirebaseController) : GuiState(controller) {
    override var view: View = BasicView()
    private val itemsPerPage = 3

    private val playerButtons: Array<GuiObject> = arrayOf(*(0 until itemsPerPage).map { a: Int -> joinUserButton(a) }.toTypedArray())

    private var page: Int = 0

    private val nextPageButton = GUI.textButton(
        78f,
        2f,
        20f,
        10f,
        "->"
    ) {
        page++
        updateButtons()
    }

    private val previousPageButton = GUI.textButton(
        2f,
        2f,
        20f,
        10f,
        "<-"
    ) {
        page--
        updateButtons()
    }.hide()

    private val createGameButton = GUI.textButton(
            80f,
            80f,
            20f,
            20f,
            "+"
    ) {
        controller.createGame(GSM.userId)
        //if (gameId.isNotEmpty()) controller.setGame(GSM.activeGame.gameId)
        GSM.set(PreGameState(controller))
    }

    override val guiObjects: List<GuiObject> = listOf(
        GUI.header("Matchmaking"),
        nextPageButton,
        previousPageButton,
        *playerButtons,
        GUI.backButton { GSM.set(MainMenuState(controller)) },
        GUI.header("Usage guide"),
        nextPageButton,
        previousPageButton,
        *playerButtons,
        GUI.backButton { GSM.set(MainMenuState(controller)) },
            createGameButton
    )
    var games = emptyList<GameListObject>()

    override fun create() {
        super.create()
        controller.getPendingGames()
        retrieve(this::updateButtons)
    }

    private fun retrieve(callback: () -> Unit) {
        //users = gameController.getPendingGames()
        //userList = users.toList().map { a -> a.second }

        games = GSM.pendingGames
        // userList = users.toList().map { a -> a.second }
        callback()
    }

    private fun updateButtons() {
        val index = page * itemsPerPage
        playerButtons.forEachIndexed { i, guiObject ->
            val j = index + i
            val button = playerButtons[i]
            if (j < games.size) {
                button.set(Text(games.get(j).playerName))
                button.show()
            } else {
                button.hide()
            }
        }
        if (index + itemsPerPage < games.size)
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
            10f,
            70f - index * 9f,
            80f,
            7f,
            "Loading"
        ) {
            val gameId = games.get((page * itemsPerPage) + index).gameId
            print("My id:  "+GSM.userId)
            controller.joinGame(
                    gameId,
                    GSM.userId)
        }
    }

    override fun update(dt: Float) {
        // TODO
        /*if (GSM.activeGame){
            controller.setGame(gameId)
            GSM.set(PreGameState())
        }*/
    }

    override fun render() {
        this.view.render(*guiObjects.toTypedArray())
    }
}
