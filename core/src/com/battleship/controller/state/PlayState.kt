package com.battleship.controller.state

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.battleship.GameStateManager
import com.battleship.model.Player
import com.battleship.model.ui.Border
import com.battleship.model.ui.GuiObject
import com.battleship.model.ui.Text
import com.battleship.utility.CoordinateUtil.toCoordinate
import com.battleship.utility.Font
import com.battleship.utility.GUI
import com.battleship.utility.GdxGraphicsUtil.boardPosition
import com.battleship.utility.GdxGraphicsUtil.boardWidth
import com.battleship.utility.GdxGraphicsUtil.gameInfoPosition
import com.battleship.utility.GdxGraphicsUtil.gameInfoSize
import com.battleship.utility.GdxGraphicsUtil.weaponsetPosition
import com.battleship.utility.GdxGraphicsUtil.weaponsetSize
import com.battleship.utility.Palette
import com.battleship.view.PlayView
import com.battleship.view.View

class PlayState : GuiState() {
    override var view: View = PlayView()
    var boardSize = 10
    var player: Player = Player(boardSize)
    var opponent: Player = Player(boardSize)
    var playerBoard: Boolean = false
    var playerTurn: Boolean = true

    private var equipmentButtons: Array<GuiObject> =
        arrayOf(*(0 until player.equipmentSet.equipments.size).map { a: Int ->
            joinWeaponButton(
                a,
                Gdx.graphics.weaponsetPosition(),
                Gdx.graphics.weaponsetSize()
            )
        }.toTypedArray())

    // var gameInfo = GameInfo(player)

    private val testText = GUI.text(
        Gdx.graphics.gameInfoPosition().x,
        Gdx.graphics.gameInfoPosition().y,
        Gdx.graphics.gameInfoSize().x,
        Gdx.graphics.gameInfoSize().y,
        "Your turn"
    )

    private val switchBoardButton = GUI.textButton(
        Gdx.graphics.width - Gdx.graphics.gameInfoSize().x / 4,
        Gdx.graphics.gameInfoPosition().y - 40,
        Gdx.graphics.gameInfoSize().x / 4,
        Gdx.graphics.gameInfoSize().y,
        "Switch board",
        font = Font.TINY_BLACK,
        color = Palette.GREY,
        borderColor = Palette.LIGHT_GREY,
        onClick = {
            playerBoard = !playerBoard
        }
    )

    override val guiObjects: List<GuiObject> = listOf(
        testText, switchBoardButton, *equipmentButtons
    )

    override fun create() {
        super.create()
        player.board.createAndPlaceTreasurechests(4, true)
        player.board.createAndPlaceGoldcoins(2, true)
        opponent.board.createAndPlaceTreasurechests(4, false)
        opponent.board.createAndPlaceGoldcoins(2, false)
        //player.equipmentSet.equipments.add(Shovel())
        //layer.equipmentSet.equipments.add(BigEquipment())
        //player.equipmentSet.equipments.add(MetalDetector())
        //player.equipmentSet.setEquipmentActive(player.equipmentSet.equipments.first())
    }

    override fun render() {
        if (playerBoard) {
            this.view.render(
                *guiObjects.toTypedArray(),
                player.board
            )
        } else {
            this.view.render(
                *guiObjects.toTypedArray(),
                opponent.board
            )
        }
    }

    override fun update(dt: Float) {
        handleInput()
        updateUIElements()
        player.updateHealth()
        opponent.updateHealth()
        if (player.health == 0) {
            println("Player2 won!")
            GameStateManager.set(MainMenuState())
        } else if (opponent.health == 0) {
            println("Player won!")
            GameStateManager.set(MainMenuState())
        }
    }

    fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchPos =
                Vector2(Gdx.input.x.toFloat(), Gdx.graphics.height - Gdx.input.y.toFloat())
            val boardWidth = Gdx.graphics.boardWidth()
            val boardPos = Gdx.graphics.boardPosition()
            val boardBounds = Rectangle(boardPos.x, boardPos.y, boardWidth, boardWidth)
            if (boardBounds.contains(touchPos)) {
                val boardTouchPos = touchPos.toCoordinate(boardPos, boardWidth, boardSize)
                if (playerTurn && !playerBoard) {
                    if (player.equipmentSet.activeEquipment!!.hasMoreUses()) {
                        opponent.board.shootTiles(
                            boardTouchPos,
                            player.equipmentSet.activeEquipment!!
                        )
                        playerTurn = !playerTurn
                    } else {
                        println(player.equipmentSet.activeEquipment!!.name + " has no more uses")
                    }
                }
                // TODO remove else if
                else if (!playerTurn && playerBoard) {
                    if (opponent.equipmentSet.activeEquipment!!.hasMoreUses()) {
                        player.board.shootTiles(
                            boardTouchPos,
                            opponent.equipmentSet.activeEquipment!!
                        )
                        playerTurn = !playerTurn
                    } else {
                        println(opponent.equipmentSet.activeEquipment!!.name + " has no more uses")
                    }
                }
            }
        }
    }

    fun updateUIElements() {
        equipmentButtons.forEachIndexed { i, _ ->
            val button = equipmentButtons[i]
            val equipment = player.equipmentSet.equipments[i]

            // Updates border of equipement buttons
            button.set(Text(equipment.name + " " + equipment.uses, Font.TINY_BLACK))
            val active = equipment.active
            if (active) {
                button.set(Border(Palette.GREEN))
            } else {
                button.set(Border(Palette.LIGHT_GREY))
            }
            // Hides and shows equipmentbuttons
            if (playerBoard) {
                equipmentButtons[i].hide()
            } else {
                equipmentButtons[i].show()
            }
            // Updates text
            if (playerTurn) {
                testText.set(Text("Your turn"))
            } else {
                testText.set(Text("Opponent's turn"))
            }
        }
        //  Updates border of switchboardbutton
        if(playerTurn && playerBoard){
            switchBoardButton.set(Border(Palette.GREEN))
        }else if(!playerTurn && !playerBoard){
            switchBoardButton.set(Border(Palette.GREEN))
        }else if(playerTurn && !playerBoard){
            switchBoardButton.set(Border(Palette.LIGHT_GREY))
        }else if(!playerTurn && playerBoard){
            switchBoardButton.set(Border(Palette.LIGHT_GREY))
        }
    }

    private fun joinWeaponButton(
        index: Int,
        position: Vector2,
        dimension: Vector2
    ): GuiObject {
        val equipment = player.equipmentSet.equipments[index]

        var borderColor = Palette.LIGHT_GREY
        if (equipment.active) {
            borderColor = Palette.GREEN
        }

        return GUI.textButton(
            position.x + dimension.x / player.equipmentSet.equipments.size * index + index * 2,
            position.y,
            dimension.x / player.equipmentSet.equipments.size,
            dimension.y,
            equipment.name + " " + equipment.uses,
            font = Font.TINY_BLACK,
            color = Palette.GREY,
            borderColor = borderColor,
            onClick = {
                player.equipmentSet.setEquipmentActive(equipment)
                borderColor = Palette.GREEN
            }
        )
    }
}
