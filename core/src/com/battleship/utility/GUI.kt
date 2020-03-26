package com.battleship.utility

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.battleship.GameStateManager
import com.battleship.controller.state.MainMenuState
import com.battleship.model.ui.Background
import com.battleship.model.ui.Border
import com.battleship.model.ui.BottomBorder
import com.battleship.model.ui.GuiObject
import com.battleship.model.ui.Image
import com.battleship.model.ui.ImageButtonText
import com.battleship.model.ui.Text

object GUI {

    fun text(
        posX: Float,
        posY: Float,
        sizeX: Float,
        sizeY: Float,
        text: String,
        font: BitmapFont = Font.SMALL_BLACK
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
        font: BitmapFont = Font.SMALL_BLACK,
        color: TextureRegion = Palette.LIGHT_GREY,
        borderColor: TextureRegion = Palette.BLACK,
        onClick: () -> Unit
    ): GuiObject {
        return GuiObject(posX, posY, sizeX, sizeY)
            .with(Background(color))
            .with(Border(borderColor))
            .with(Text(text, font))
            .onClick(onClick)
    }

    fun menuButton(
        posX: Float,
        posY: Float,
        text: String,
        onClick: () -> Unit
    ): GuiObject {
        return GuiObject(posX, posY, 340f, 100f)
            .with(Background(Palette.LIGHT_GREY))
            .with(Border(Palette.BLACK))
            .with(Text(text, Font.MEDIUM_BLACK))
            .onClick(onClick)
    }

    fun header(
        text: String
    ): GuiObject {
        return GuiObject(0f, Gdx.graphics.height.toFloat() - 96f, Gdx.graphics.width.toFloat(), 96f)
            .with(Background(Palette.DARK_GREY))
            .with(BottomBorder(Palette.BLACK))
            .with(Text(text, Font.MEDIUM_WHITE))
    }

    fun backButton (
        onClick: () -> Unit
    ) : GuiObject {
        return GuiObject(10f, Gdx.graphics.height - 80f, 64f, 64f)
            .with(Image("icons/arrow_back_white.png"))
            .onClick(onClick)
    }

    fun image(
        posX: Float,
        posY: Float,
        sizeX: Float,
        sizeY: Float,
        texturePath: String
    ): GuiObject {
        return GuiObject(posX, posY, sizeX, sizeY)
            .with(Image(texturePath))
    }

    fun imageButton(
        posX: Float,
        posY: Float,
        sizeX: Float,
        sizeY: Float,
        texturePath: String,
        onClick: () -> Unit
    ): GuiObject {
        return GuiObject(posX, posY, sizeX, sizeY)
            .with(Image(texturePath))
            .onClick(onClick)
    }
    fun imageAndTextButton(
        posX: Float,
        posY: Float,
        sizeX: Float,
        sizeY: Float,
        text: String,
        texturePath: String,
        onClick: () -> Unit
    ): GuiObject {
        return GuiObject(posX, posY, sizeX, sizeY)
            .with(Image(texturePath))
            .with(ImageButtonText(text, Font.MEDIUM_BLACK))
            .onClick(onClick)
    }
}
