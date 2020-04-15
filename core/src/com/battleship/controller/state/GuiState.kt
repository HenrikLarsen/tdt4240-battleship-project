package com.battleship.controller.state

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.battleship.controller.firebase.FirebaseController
import com.battleship.model.ui.GuiObject

/**
 * Abstract state class declaring functions and variables for GUI states
 */
abstract class GuiState(controller: FirebaseController) : State(controller) {
    abstract val guiObjects: List<GuiObject>

    override fun create() {
        Gdx.input.inputProcessor =
            InputMultiplexer(*guiObjects.filter { it.hasListener }.map { it.listener }
                .toTypedArray())
    }

    override fun pause() {
        Gdx.input.inputProcessor = InputMultiplexer()
        super.pause()
    }

    override fun resume() {
        Gdx.input.inputProcessor =
            InputMultiplexer(*guiObjects.filter { it.hasListener }.map { it.listener }
                .toTypedArray())
        super.resume()
    }

    override fun dispose() {
        super.dispose()
        Gdx.input.inputProcessor = null
    }
}
