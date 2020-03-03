package com.battleship.model.ships

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.battleship.model.GameObject

abstract class Ship : GameObject() {
    abstract var dimension: Vector2
    abstract var name: String
    abstract var health: Int
    abstract var position: Vector2

    //TODO reimplement
    /*
    fun hit(coordinates: Vector2): Boolean {
        val rect = Rectangle(position.x, position.y, dimension.x*tileSize, dimension.y*tileSize)
        if (rect.contains(coordinates)){
            return true
        }
        return false
    }
    */


    fun takeDamage(damage : Int){
        health -= damage
    }

    fun sunk(): Boolean {
        return health == 0
    }

    fun rotateShip(){
        val temp = dimension.x
        dimension.x = dimension.y
        dimension.y = temp
    }

    //abstract fun hit(coordinates: Vector2): Boolean
    //abstract fun sunk(): Boolean
    //abstract fun takeDamage(damage: Int)
}
