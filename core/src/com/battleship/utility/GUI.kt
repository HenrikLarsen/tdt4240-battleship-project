package com.battleship.utility

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.battleship.GameStateManager
import com.battleship.controller.state.MainMenuState
import com.battleship.model.ui.Background
import com.battleship.model.ui.Border
import com.battleship.model.ui.GuiObject
import com.battleship.model.ui.Text

object GUI {

    val backButton = textButton(
        20f,
        Gdx.graphics.height - 110f,
        170f,
        90f,
        "back"
    ) {
        GameStateManager.set(MainMenuState())
    }

    fun text(
        posX: Float,
        posY: Float,
        sizeX: Float,
        sizeY: Float,
        text: String,
        font: BitmapFont = Font.SMALL_WHITE
    ): GuiObject {
        return GuiObject(posX, posY, sizeX, sizeY)
            .with(Text(text, font))
    }

    fun textBox(
        posX: Float,
        posY: Float,
        sizeX: Float,
        sizeY: Float,
        text: String,
        font: BitmapFont = Font.SMALL_WHITE,
        color: TextureRegion = Palette.BLACK,
        borderColor: TextureRegion = Palette.WHITE
    ): GuiObject {
        return GuiObject(posX, posY, sizeX, sizeY)
            .with(Background(color))
            .with(Border(borderColor))
            .with(Text(text, font))
    }

    fun button(
        posX: Float,
        posY: Float,
        sizeX: Float,
        sizeY: Float,
        color: TextureRegion = Palette.BLACK,
        borderColor: TextureRegion = Palette.WHITE,
        onClick: () -> Unit
    ): GuiObject {
        return GuiObject(posX, posY, sizeX, sizeY)
            .with(Background(color))
            .with(Border(borderColor))
            .onClick(onClick)
    }

    fun textButton(
        posX: Float,
        posY: Float,
        sizeX: Float,
        sizeY: Float,
        text: String,
        font: BitmapFont = Font.SMALL_WHITE,
        color: TextureRegion = Palette.BLACK,
        borderColor: TextureRegion = Palette.WHITE,
        onClick: () -> Unit
    ): GuiObject {
        return GuiObject(posX, posY, sizeX, sizeY)
            .with(Background(color))
            .with(Border(borderColor))
            .with(Text(text, font))
            .onClick(onClick)
    }
}
