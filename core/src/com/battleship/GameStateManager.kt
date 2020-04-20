package com.battleship

import com.battleship.controller.state.State
import com.battleship.model.Game
import com.battleship.model.GameListObject
import java.util.Stack
import java.util.UUID

object GameStateManager {
    var username = ""
    var userId: String = UUID.randomUUID().toString() //
    var activeGame: Game? = null
    var pendingGames = ArrayList<GameListObject>()

    private val states: Stack<State> = Stack()

    fun push(state: State) {
        if (states.size > 0)
            states.peek().pause()
        states.push(state)
        create()
    }

    fun pop() {
        states.pop().dispose()
        if (states.size > 0)
            states.peek().resume()
    }

    fun set(state: State) {
        println("setting ${state.javaClass}")
        states.pop().dispose()
        states.push(state)
        create()
    }

    fun create() {
        print("User id $userId")
        states.peek().create()
    }

    fun update(dt: Float) {
        states.peek().update(dt)
    }

    fun render() {
        states.peek().render()
    }
    fun resetGame() {
        activeGame = null
    }
}
typealias GSM = GameStateManager
