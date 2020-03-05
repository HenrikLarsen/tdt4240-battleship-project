package com.battleship.model.ships

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

class MediumShip(position: Vector2) : Ship(position) {
    override var dimension: Vector2 = Vector2(1f, 3f)
    override var name: String = "MediumShip"
    override var health: Int = 3

    override fun draw(batch: SpriteBatch, boardPos: Vector2, tileSize: Float) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.setColor(Color.WHITE)

        val newX = boardPos.x + tileSize * position.x + position.x * padding
        val newY = boardPos.y + tileSize * position.y + position.y * padding
        shapeRenderer.rect(newX, newY, dimension.x * tileSize, dimension.y * tileSize)
        shapeRenderer.end()
    }
}