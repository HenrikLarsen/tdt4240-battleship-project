package com.battleship.controller.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2
import com.battleship.utility.RectangleUtil

class ClickHandler(val position: Vector2, val size: Vector2, val onClick: () -> Boolean) : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (RectangleUtil.fromVectors(position, size).contains(screenX.toFloat(), Gdx.graphics.height - screenY.toFloat())) {
            return onClick()
        }
        return false
    }
}
