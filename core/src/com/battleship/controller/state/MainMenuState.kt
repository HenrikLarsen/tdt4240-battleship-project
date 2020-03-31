package com.battleship.controller.state

import com.badlogic.gdx.Gdx
import com.battleship.GSM
import com.battleship.model.ui.GuiObject
import com.battleship.utility.Font
import com.battleship.utility.GUI
import com.battleship.view.BasicView
import com.battleship.view.View

/**
 * State handling all logic related to the main menu
 */
class MainMenuState : GuiState() {

    private val menuList = listOf(
            Pair("Play") { GSM.set(PreGameState()) },
            Pair("Settings") { GSM.set(SettingsState()) },
            Pair("Matchmaking") { GSM.set(MatchmakingState()) }
    )

    override val guiObjects: List<GuiObject> = menuList
        .mapIndexed { i, (name, func) ->
            GUI.menuButton(
                23.4375f,
                100f - (56.25f + 18.75f * i),
                name,
                onClick = func
            )
        }

    private val title: GuiObject = GUI.text(
        11f,
        74f,
        78f,
        12.5f,
        "Treasure hunt",
        font = Font.XXL_BLACK

    )
    private val skull: GuiObject = GUI.image(
        44.375f,
        66.25f,
        11.25f,
        9f,
        "images/skull_and_crossbones.png"
    )

    override var view: View = BasicView()

    override fun update(dt: Float) {}

    override fun render() {
        view.render(title, skull, *guiObjects.toTypedArray())
    }
}
