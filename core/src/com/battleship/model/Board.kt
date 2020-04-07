package com.battleship.model

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.battleship.model.equipment.Equipment
import com.battleship.model.treasures.Boot
import com.battleship.model.treasures.GoldCoin
import com.battleship.model.treasures.Treasure
import com.battleship.model.treasures.Treasure.TreasureType
import com.battleship.model.treasures.TreasureChest
import kotlin.random.Random

class Board(val size: Int) : GameObject() {
    private var treasures: ArrayList<Treasure> = ArrayList()
    private var board = Array(size) { Array(size) { Tile.PREGAME } }
    private val tileRenderer: ShapeRenderer = ShapeRenderer()
    var padding: Int = 0

    // Change all tiles to unopened state
    fun setTilesUnopened() {
        board = Array(size) { Array(size) { Tile.UNOPENED } }
    }

    fun createAndPlaceTreasures(quantity: Int, type: TreasureType, revealed: Boolean) {
        var treasure: Treasure
        for (i in 0 until quantity) {
            do {
                val x = Random.nextInt(0, size).toFloat()
                val y = Random.nextInt(0, size).toFloat()

                treasure = when (type) {
                    TreasureType.TREASURECHEST -> TreasureChest(Vector2(x, y), Random.nextBoolean())
                    TreasureType.GOLDCOIN -> GoldCoin(Vector2(x, y), Random.nextBoolean())
                    TreasureType.BOOT -> Boot(Vector2(x, y), Random.nextBoolean())
                }
            } while (!validateTreasurePosition(treasure))

            treasure.revealed = revealed
            treasures.add(treasure)
        }
    }

    private fun validateTreasurePosition(treasure: Treasure?): Boolean {
        if (treasure == null) return false

        for (tile in treasure.getTreasureTiles()) {
            // Tile outside board
            if (tile.x >= size || tile.y >= size) return false

            // Another tile already in this place
            for (placedShip in treasures) {
                if (placedShip.getTreasureTiles().contains(tile)) {
                    return false
                }
            }
        }
        return true
    }

    override fun draw(batch: SpriteBatch, position: Vector2, dimension: Vector2) {
        var x = position.x
        var y = position.y
        val tileSize = dimension.x / size

        // Draw board
        for (array in board) {
            for (value in array) {

                if (value == Tile.PREGAME) {
                    tileRenderer.begin(ShapeRenderer.ShapeType.Line)
                    tileRenderer.color = Color.BLACK
                    tileRenderer.rect(x, y, tileSize, tileSize)
                    //  tileRenderer.rectLine(x, y,x + size, y + size, 10f)
                    tileRenderer.end()
                } else {
                    tileRenderer.begin(ShapeRenderer.ShapeType.Filled)
                    when (value) {
                        Tile.HIT -> tileRenderer.color = Color.GREEN
                        Tile.MISS -> tileRenderer.color = Color.RED
                        Tile.NEAR -> tileRenderer.color = Color.YELLOW
                        Tile.UNOPENED -> tileRenderer.color = Color.BLACK
                    }
                    tileRenderer.rect(x, y, tileSize, tileSize)
                    tileRenderer.end()

                    tileRenderer.begin(ShapeRenderer.ShapeType.Line)
                    tileRenderer.color = Color.WHITE
                    tileRenderer.rect(x, y, tileSize, tileSize)
                    tileRenderer.end()
                }

                x += tileSize + padding
            }
            y += tileSize + padding
            x = position.x
        }

        // Draw treasures
        for (treasure in treasures) {
            treasure.draw(batch, position, Vector2(tileSize, tileSize))
        }
    }

    fun shootTiles(boardTouchPos: Vector2, equipment: Equipment): Boolean {
        equipment.use()
        val xSearchMin = boardTouchPos.x.toInt() - equipment.searchRadius
        val xSearchMax = boardTouchPos.x.toInt() + equipment.searchRadius + 1
        val ySearchMin = boardTouchPos.y.toInt() - equipment.searchRadius
        val ySearchMax = boardTouchPos.y.toInt() + equipment.searchRadius + 1

        // Loops through the equipments search radius
        var valid = false
        for (x in xSearchMin until xSearchMax) {
            for (y in ySearchMin until ySearchMax) {
                // Check if inside board
                if (x in 0 until size && y in 0 until size) {
                    if (updateTile(Vector2(x.toFloat(), y.toFloat()))) {
                        valid = true
                    }
                }
            }
        }
        return valid
    }

    private fun updateTile(pos: Vector2): Boolean {
        val treasurePos = Vector2(pos.y, pos.x) // Flip position

        val boardTile = getTile(pos)
        if (boardTile == Tile.MISS || boardTile == Tile.HIT) {
            return false
        }

        var hit = Tile.MISS
        val treasure = getTreasureByPosition(treasurePos)
        if (treasure != null) {
            hit = Tile.HIT
            treasure.takeDamage()
        }

        board[pos.x.toInt()][pos.y.toInt()] = hit
        return true
    }

    private fun getTile(pos: Vector2): Board.Tile {
        return board[pos.x.toInt()][pos.y.toInt()]
    }

    private fun getTreasureByPosition(pos: Vector2): Treasure? {
        for (treasure in treasures) {
            if (treasure.hit(pos)) {
                return treasure
            }
        }
        return null
    }

    fun getAllTreasureHealth(): Int {
        var health = 0
        for (ship in treasures) {
            health += ship.health
        }
        return health
    }

    enum class Tile {
        HIT, MISS, NEAR, UNOPENED, PREGAME
    }
}
