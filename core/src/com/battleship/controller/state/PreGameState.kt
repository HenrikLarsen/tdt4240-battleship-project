package com.battleship.controller.state

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.battleship.GameStateManager
import com.battleship.model.Player
import com.battleship.model.ui.Background
import com.battleship.model.ui.Border
import com.battleship.model.ui.GuiObject
import com.battleship.model.ui.Text
import com.battleship.utility.Font
import com.battleship.utility.GUI
import com.battleship.utility.GdxGraphicsUtil.boardPosition
import com.battleship.utility.GdxGraphicsUtil.boardRectangle
import com.battleship.utility.GdxGraphicsUtil.boardWidth
import com.battleship.utility.GdxGraphicsUtil.size
import com.battleship.utility.Palette
import com.battleship.view.PlayView
import com.battleship.view.View

class PreGameState : GuiState() {
    override var view: View = PlayView()
    private val boardSize = 10
    var player: Player = Player(boardSize)

    override fun create() {
        super.create()
        player.board.createAndPlaceTreasurechests(2, true)
        player.board.createAndPlaceGoldcoins(2, true)
    }

    private val readyButton = GuiObject(
        6f,
        3f,
        90f,
        10f
    )
        .with(Background(Palette.BLACK))
        .with(Border(Palette.WHITE, 10f, 10f, 10f, 10f))
        .with(Text("Start Game"))
        .onClick {
            println("Player are ready")
            // GameStateManager.gameController.registerShip(player.board.getships()) TODO: Create
            GameStateManager.set(PlayState())
        }

    override val guiObjects: List<GuiObject> = listOf(
        readyButton,
        GUI.header("Place treasures"),
        GUI.backButton { GameStateManager.set(MainMenuState()) }
    )

    override fun render() {
        this.view.render(*guiObjects.toTypedArray(), player.board)
    }

    override fun update(dt: Float) {
        handleInput()
    }

    fun handleInput() {
        // Drag ship
        if (Gdx.input.justTouched()) {
            val touchPos =
                Vector2(
                    Gdx.input.x.toFloat(),
                    Gdx.graphics.height - Gdx.input.y.toFloat()
                )
            val screenSize = Gdx.graphics.size()

            // Check if input is on the board
            if (Gdx.graphics.boardRectangle().contains(touchPos)) {
                val boardPos = Gdx.graphics.boardPosition()
                val boardWidth = Gdx.graphics.boardWidth()
            }
        }
    }
}
