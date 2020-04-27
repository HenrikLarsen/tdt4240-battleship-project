package com.battleship.controller

import com.battleship.controller.state.State
import com.battleship.model.Game
import com.battleship.model.PendingGame
import java.util.Stack
import java.util.UUID

/**
 * GameStateManager is a Singleton that manages the stack of states
 * that the game goes through.
 */
object GameStateManager {
    var username = ""
    val userId: String = UUID.randomUUID().toString()
    var activeGame: Game? = null
    var pendingGames = ArrayList<PendingGame>()

    private val states: Stack<State> = Stack()

    /**
     * Pushes an item onto the top of the stack.
     *
     * @param state: State to push
     */
    fun push(state: State) {
        if (states.size > 0)
            states.peek().pause()
        states.push(state)
        create()
    }

    /**
     * Set the replaces the currant state with a new state in the stack
     *
     * @param state: State - new active state
     */
    fun set(state: State) {
        states.pop().dispose()
        states.push(state)
        create()
    }

    /**
     * Calls the uppermost state in the stack's create method on state initialisation.
     */
    private fun create() {
        states.peek().create()
    }

    /**
     * Called from [BattleshipGame], and calls the uppermost state in the stack' update method.
     * Updates as often as the game renders itself.
     *
     * @param dt: Float - delta time since last call
     */
    fun update(dt: Float) {
        states.peek().update(dt)
    }

    /**
     * Called from [BattleshipGame], and calls the uppermost state in the stack' render method.
     * Runs as often as the game renders itself.
     */
    fun render() {
        states.peek().render()
    }

    /**
     * Sets nullable variable activeGame to null
     */
    fun resetGame() {
        activeGame = null
    }
}
typealias GSM = GameStateManager
