package com.battleship.model

import com.battleship.model.equipment.EquipmentSet

class Player(var playerId: String = "", var playerName: String = "") {
    val boardSize: Int = 10
    var equipmentSet = EquipmentSet()
    var board: Board = Board(boardSize)
    var health: Int = board.getCombinedTreasureHealth()

    fun updateHealth() {
        health = board.getCombinedTreasureHealth()
    }

    override fun toString(): String {
        return "Player(Id='$playerId', Name='$playerName', board=$board)"
    }
}
