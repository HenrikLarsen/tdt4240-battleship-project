package com.battleship.model.treasures

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.battleship.BattleshipGame
import com.battleship.model.GameObject
abstract class Treasure(var position: Vector2) : GameObject() {
    abstract var dimension: Vector2
    abstract var name: String
    abstract var health: Int
    abstract var sprite: Sprite
    abstract var type: TreasureType
    abstract var sound: Sound
    private var padding = 3
    var revealed = false

    fun playSound(volume: Float) {
        if (BattleshipGame.soundOn) {
            sound.stop()
            sound.play(volume)
        }
    }

    enum class TreasureType {
        TREASURECHEST, GOLDCOIN, GOLDKEY
    }

    fun hit(coordinates: Vector2): Boolean {
        for (i in 1 until dimension.x.toInt() + 1) {
            val x = position.x + i - 1
            for (j in 1 until dimension.y.toInt() + 1) {
                val y = position.y + j - 1

                if (coordinates.epsilonEquals(x, y)) {
                    return true
                }
            }
        }
        return false
    }

    fun takeDamage() {
        health--
    }

    fun found(): Boolean {
        return health == 0
    }

    fun rotateTreasure() {
        val temp = dimension.x
        dimension.x = dimension.y
        dimension.y = temp
    }

    override fun draw(batch: SpriteBatch, boardPos: Vector2, dimension: Vector2) {
        if (found() || revealed) {

            val xPos = boardPos.x + dimension.x * position.x + padding
            val yPos = boardPos.y + dimension.y * position.y + padding
            val width = this.dimension.x * dimension.x - (padding * 2)
            val height = this.dimension.y * dimension.y - (padding * 2)

            val spriteTexture = sprite.texture
            spriteTexture.magFilter
            spriteTexture.setFilter(spriteTexture.minFilter, spriteTexture.magFilter)

            batch.begin()
            batch.draw(sprite.texture, xPos, yPos, width, height)
            batch.end()
        }
    }

    fun getTreasureTiles(): ArrayList<Vector2> {
        val tiles = ArrayList<Vector2>()
        for (i in 1 until dimension.x.toInt() + 1) {
            val x = position.x.toInt() + i - 1
            for (j in 1 until dimension.y.toInt() + 1) {
                val y = position.y.toInt() + j - 1
                tiles.add(Vector2(x.toFloat(), y.toFloat()))
            }
        }
        return tiles
    }
}
