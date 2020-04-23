package com.battleship.controller.state

import com.badlogic.gdx.Gdx
import com.battleship.GameStateManager
import com.battleship.controller.firebase.FirebaseController
import com.battleship.model.ui.Border
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
class UsageGuideState(private val controller: FirebaseController,
                      private val firstimeOpen: Boolean = false) : GuiState(controller) {

    override var view: View = BasicView()
    private var pageIndex: Int = 0

    private val descriptions: List<String> = listOf(
        "Choose a username which other opponents can see.",
        "Select opponent to play against or create a new game.",
        "Place your treasures by dragging them to their desired position.",
        "Wait for your opponent to get ready.",
        "Play the game!\nChoose equipments and try to locate all of your opponent's treasures."
    )

    private val imagePaths: List<String> = listOf(
        "images/usageGuide/firstPage.png",
        "images/usageGuide/secondPage.png",
        "images/usageGuide/thirdPage.png",
        "images/usageGuide/fourthPage.png",
        "images/usageGuide/fifthPage.png"
    )

    private var currentImage: GuiObject =
        GUI.image(
            15f,
            21f,
            70f,
            65f,
            texturePath = imagePaths[0]
    )

    private var currentDescription: GuiObject = GUI.textBox(
        5f,
        11f,
        90f,
        8f,
        descriptions[0],
        color = Palette.DARK_GREY
    )

    private val nextPageButton = GUI.textButton(
        70f,
        2f,
        25f,
        7f,
        "Next"
    ) {
        if (pageIndex == imagePaths.size - 1) {
            GameStateManager.set(MainMenuState(controller))
        } else {
            pageIndex++
            updateButtons()
        }
    }

    private val previousPageButton = GUI.textButton(
        5f,
        2f,
        25f,
        7f,
        "Previous"
    ) {
        pageIndex--
        updateButtons()
    }.hide()

    private val backButton: GuiObject =
        GUI.backButton { GameStateManager.set(SettingsState(controller)) }

    override val guiObjects: List<GuiObject> = listOf(
        if(firstimeOpen) GUI.header("Welcome!") else GUI.header("Usage guide") ,
        nextPageButton,
        previousPageButton,
        currentDescription,
        currentImage,
        backButton
    )

    override fun create() {
        super.create()
        if (firstimeOpen) backButton.hide()
        updateButtons()
    }

    /**
     * Decide which buttons to show and update image and text
     */
    private fun updateButtons() {
        if (pageIndex == imagePaths.size - 1)
            nextPageButton.set(Text("Finish", Font.MEDIUM_BLACK))
        else
            nextPageButton.set(Text("Next", Font.MEDIUM_BLACK))
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
