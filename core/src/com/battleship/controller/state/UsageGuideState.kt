package com.battleship.controller.state

import com.badlogic.gdx.Gdx
import com.battleship.GameStateManager
import com.battleship.model.ui.GuiObject
import com.battleship.model.ui.Image
import com.battleship.model.ui.Text
import com.battleship.utility.Font
import com.battleship.utility.GUI
import com.battleship.utility.Palette
import com.battleship.view.BasicView
import com.battleship.view.View

/**
 * State handling all logic related to the help menu
 */
class UsageGuideState : GuiState() {
    override var view: View = BasicView()
    private var pageIndex: Int = 0

    // TODO: Update this with real description when game is more completed
    private val descriptions: List<String> = listOf(
        "First page, bla bla bla",
        "Second page, bla bla bla",
        "Third page, bla bla bla",
        "Fourth page, bla bla bla"
    )

    // TODO: Update this with real screenshots from the game when it is more completed
    private val imagePaths: List<String> = listOf(
        "helpGuide/page1.png",
        "helpGuide/page2.png",
        "helpGuide/page3.png",
        "helpGuide/page4.png"
    )

    private var currentDescription: GuiObject = GUI.text(
        20f,
        170f,
        Gdx.graphics.width - 40f,
        90f,
        descriptions[0],
        Font.MEDIUM_BLACK
    )
    private var currentImage: GuiObject = GUI.image(
        120f,
        260f,
        400f,
        400f,
        imagePaths[0]
    )

    private val nextPageButton = GUI.textButton(
        Gdx.graphics.width - 230f,
        30f,
        180f,
        80f,
        "Next"
    ) {
        pageIndex++
        updateButtons()
    }

    private val previousPageButton = GUI.textButton(
        30f,
        30f,
        180f,
        80f,
        "Previous"
    ) {
        pageIndex--
        updateButtons()
    }.hide()

    override val guiObjects: List<GuiObject> = listOf(
        GUI.header("Usage guide"),
        nextPageButton,
        previousPageButton,
        currentDescription,
        currentImage,
        GUI.backButton { GameStateManager.set(SettingsState()) }
    )

    override fun create() {
        super.create()
        updateButtons()
    }

    /**
     * Decide which buttons to show and update image and text
     */
    private fun updateButtons() {
        if (pageIndex < imagePaths.size - 1) {
            nextPageButton.show()
        } else {
            nextPageButton.hide()
        }

        if (pageIndex > 0)
            previousPageButton.show()
        else
            previousPageButton.hide()

        currentDescription.set(Text(descriptions[pageIndex]))
        currentImage.set(Image(imagePaths[pageIndex]))
    }

    override fun update(dt: Float) {
    }

    override fun render() {
        view.render(*guiObjects.toTypedArray())
    }
}
