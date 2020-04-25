package com.battleship.controller.state

import com.battleship.BattleshipGame
import com.battleship.GameStateManager
import com.battleship.controller.firebase.FirebaseController
import com.battleship.model.ui.GuiObject
import com.battleship.model.ui.Text
import com.battleship.utility.Font
import com.battleship.utility.GUI
import com.battleship.view.BasicView
import com.battleship.view.View

/**
 * State handling all logic related to the settings menu
 */
class SettingsState(controller: FirebaseController) : GuiState(controller) {
    override var view: View = BasicView()
    private var musicButton: GuiObject = GUI.menuButton(
        25f,
        62.5f,
        "Music off",
        onClick = {
            if (BattleshipGame.music?.isPlaying == true)
                BattleshipGame.music?.pause()
            else
                BattleshipGame.music?.play()
        }
    )
    private var soundButton: GuiObject = GUI.menuButton(
        25f,
        43.75f,
        "Sound Effects off",
        onClick = {
            BattleshipGame.soundOn = !BattleshipGame.soundOn
        }
    )
    override val guiObjects: List<GuiObject> = listOf(
        GUI.header("Settings"),
        musicButton,
        soundButton,
        GUI.menuButton(
            25f,
            25f,
            "Usage guide",
            onClick = { GameStateManager.set(UsageGuideState(controller, false)) }
        ),

        GUI.text(
            3f,
            5f,
            94f,
            10f,
            "Made by group 12",
            Font.MEDIUM_BLACK
        ),
        GUI.text(
            3f,
            2f,
            94f,
            10f,
            "v1.0.0",
            Font.MEDIUM_BLACK
        ),
        GUI.backButton { GameStateManager.set(MainMenuState(controller)) }
    )

    override fun create() {
        super.create()
        updateButtons()
    }

    /**
     * Update button text based on music status
     */
    private fun updateButtons() {
        if (BattleshipGame.music?.isPlaying == true)
            musicButton.set(Text("Music off", Font.MEDIUM_BLACK))
        else
            musicButton.set(Text("Music on", Font.MEDIUM_BLACK))
        if (BattleshipGame.soundOn)
            soundButton.set(Text("Sound off", Font.MEDIUM_BLACK))
        else
            soundButton.set(Text("Sound on", Font.MEDIUM_BLACK))
    }

    override fun update(dt: Float) {
        updateButtons()
    }

    override fun render() {
        view.render(*guiObjects.toTypedArray())
    }
}
