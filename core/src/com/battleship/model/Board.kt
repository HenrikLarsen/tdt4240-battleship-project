package com.battleship.model

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.battleship.model.ships.MediumShip
import com.battleship.model.ships.Ship
import com.battleship.model.ships.SmallShip
import kotlin.random.Random
import com.battleship.model.weapons.RadarWeapon
import com.battleship.model.weapons.Weapon

class Board(val size: Int) : GameObject() {
    private var ships: ArrayList<Ship> = ArrayList()
    private var board = Array(size) { Array(size) { Tile.UNGUESSED } }
    private val tileRenderer: ShapeRenderer = ShapeRenderer()
    var padding: Int = 1

    fun addSmallShip(x: Int, y: Int) {
        // TODO add check
        val ship: SmallShip = SmallShip(Vector2(x.toFloat(), y.toFloat()), false)
        ships.add(ship)
    }

    fun addMediumShip(x: Int, y: Int) {
        // TODO add check
        val ship: MediumShip = MediumShip(Vector2(x.toFloat(), y.toFloat()), false)
        ships.add(ship)
    }

    /*
     * creates ships and places them
     */
    fun randomPlacement(shipNumber: Int) {
        var ship: Ship
        for (i in 0..shipNumber) {
            do {
                ship = MediumShip(Vector2(Random.nextInt(0, size).toFloat(), Random.nextInt(0, size).toFloat()), Random.nextBoolean())
                println("shipposition: (" + ship.position.x + ", " + ship.position.y + ")")
            } while (!validateShipPosition(ship))
            ships.add(ship)
        }
    }

    /*
     * This for placing a defined list of ships
     */
    fun randomPlacement(ships: ArrayList<Ship>) {
        for (ship in ships) {
            do {
                ship.position.set(Random.nextInt(0, size).toFloat(), Random.nextInt(0, size).toFloat())
                // println("ship position: (" + ship.position.x + ", " + ship.position.y + ")")
            } while (!validateShipPosition(ship))
            ships.add(ship)
        }
    }

    fun validateShipPosition(ship: Ship): Boolean {
        for (tile in ship.getTiles()) {
            if (tile.x >= size || tile.y >= size) return false
            for (placedShip in ships) {
                if (placedShip.getTiles().contains(tile)) {
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
        for (array in board) {
            for (value in array) {
                tileRenderer.begin(ShapeRenderer.ShapeType.Filled)

                when (value) {
                    Tile.HIT -> tileRenderer.color = Color.GREEN
                    Tile.UNGUESSED -> tileRenderer.color = Color.BLUE
                    Tile.MISS -> tileRenderer.color = Color.RED
                    Tile.NEAR -> tileRenderer.color = Color.YELLOW
                }

                tileRenderer.rect(x, y, tileSize, tileSize)
                tileRenderer.end()
                x += tileSize + padding
            }
            y += tileSize + padding
            x = position.x
        }

        for (ship in ships) {
            ship.draw(batch, position, Vector2(tileSize, tileSize))
        }
    }

    fun shootTiles(boardTouchPos: Vector2, weapon: Weapon) {
        var x = boardTouchPos.x.toInt()
        var y = boardTouchPos.y.toInt()
        // Loops through the weapons radius
        for (x in x - weapon.radius until x + weapon.radius + 1 step 1) {
            for (y in y - weapon.radius until y + weapon.radius + 1 step 1) {
                // Checks if inside board
                if (x >= 0 && x < size && y >= 0 && y < size) {
                    updateTile(Vector2(x.toFloat(), y.toFloat()), weapon)
                }
            }
        }
    }

    // TODO needs cleanup
    fun updateTile(pos: Vector2, weapon: Weapon) {
        var shipPos = Vector2(pos.y, pos.x)

        val boardTile = getTile(pos)
        if (boardTile == Tile.MISS || boardTile == Tile.HIT) {
            println("Guessed, pick new")
            return
        }

        var hit = Tile.MISS
        var hittedShip = getShipByPosition(shipPos)
        if (hittedShip != null) {
            println("Hitted")
            hit = Tile.HIT
            hittedShip?.takeDamage(weapon.damage)
            if (hittedShip!!.sunk()) {
                println(hittedShip?.name + " Sunk")
                ships.remove(hittedShip)
            }
        } else {
            println("Missed")
        }

        // TODO implement
        if (weapon is RadarWeapon) {
            hit = Tile.NEAR
        }

        board[pos.x.toInt()][pos.y.toInt()] = hit
    }

    fun getTile(pos: Vector2): Board.Tile {
        return board[pos.x.toInt()][pos.y.toInt()]
    }

    fun getShipByPosition(pos: Vector2): Ship? {
        for (ship in ships) {
            if (ship.hit(pos)) {
                return ship
            }
        }
        return null
    }

    fun getAllShipHealth(): Int {
        var health = 0
        for (ship in ships) {
            health += ship.health
        }
        return health
    }

    enum class Tile {
        HIT, MISS, UNGUESSED, NEAR
    }
}
