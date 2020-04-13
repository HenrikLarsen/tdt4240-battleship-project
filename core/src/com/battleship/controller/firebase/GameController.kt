package com.battleship.controller.firebase

import com.battleship.GSM
import com.battleship.model.Game
import com.battleship.model.GameListObject
import com.battleship.model.Player
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.EventListener
import com.google.cloud.firestore.FirestoreException
import com.google.cloud.firestore.ListenerRegistration
import com.google.firebase.database.annotations.Nullable

/**
 * Controller handling all database activity concerned with game flow
 */
@Suppress("UNCHECKED_CAST")
class GameController : FirebaseController() {
    var registration: ListenerRegistration? = null
    /**
     * Start new game
     * @param userId the id of the user setting up the game
     */
    fun createGame(userId: String): String {
        // Set up game data
        val data = mutableMapOf<String, Any>()
        data["player1"] = userId
        data["player2"] = ""
        data["winner"] = ""
        data["moves"] = mutableListOf<Map<String, Any>>()
        data["treasures"] = mutableMapOf<String, List<Map<String, Any>>>()

        val res = db.collection("games").add(data)

        // Return the id of the game
        return res.get().id
    }

    /**
     * Function getting all games where there is currently only one player
     */
    fun getPendingGames(): ArrayList<GameListObject> {
        val gameQuery = db.collection("games").whereEqualTo("player2", "").get()
        val gameQuerySnapshot = gameQuery.get()
        val gameDocuments = gameQuerySnapshot.documents
        val games = ArrayList<GameListObject>()
        // For each game fitting the criteria, get the id and username of opponent
        for (document in gameDocuments) {
            val id = document.id
            val playerId = document.getString("player1")

            // Find the username of the player in the game to display
            val playerQuery = playerId?.let { db.collection("users").document(playerId).get() }
            val playerQuerySnapshot = playerQuery?.get()
            val playerName = playerQuerySnapshot?.getString("username")
            if (playerName != null) {
                games.add(GameListObject(id, playerId, playerName))
            }
        }
        return games
    }

    /**
     * Gets username from a registered player
     * @param userId the id of the user that should be added
     * @return a player object
     */
    private fun getUser(userId: String): Player {
        val docRef = db.collection("users").document(userId).get()
        val user = docRef.get()

        if (user.exists()) {
            val username: String = user.get("username") as String
            if (username != "") {
                return Player(userId, username)
            }
            return Player(userId, "Unknown")
        } else {
            // Add error handling
            throw error("Something went wrong when getting user")
        }
    }

    /**
     * Add userId to a specific game
     * @param gameId the id of the game document
     * @param userId the id of the user that should be added
     */
    fun joinGame(gameId: String, userId: String): Boolean {
        // Add the data to the game document
        db.collection("games").document(gameId).update("player2", userId)
        // TODO sjekke om suksess og returnere true
        val game = db.collection("games").document(gameId).get()
        if (game.get().get("player2") == userId) return true
        return false
    }

    /**
     * Registers the ships on the board for a given user
     * @param gameId the id of the game document
     * @param userId the id of the user owning the ships
     * @param ships list containing the ships that should be added, each described using a map
     */
    fun registerTreasures(gameId: String, userId: String, treasures: List<Map<String, Any>>) {
        val query = db.collection("games").document(gameId).get()
        val game = query.get()

        if (game.exists()) {
            val dbTreasures = game.get("treasures") as MutableMap<String, List<Map<String, Any>>>
            dbTreasures[userId] = treasures
            db.collection("games").document(gameId).update("treasures", dbTreasures)
        } else {
            // Add error handling
            throw error("Something went wrong when registering ships")
        }
    }

    /**
     * Get the ships in a game
     * @param gameId the id of the game
     * @return a Game object containing game and player
     */
    fun setGame(gameId: String) {
        val query = db?.collection("games")?.document(gameId!!).get()
        val game = query.get()

        if (game.exists()) {
            // val treasures = getTreasures(gameId)

            val player1Id: String = game.get("player1") as String
            val player1: Player = getUser(player1Id)

            val player2Id: String = game.get("player2") as String

            val player2: Player = if (player2Id != "") {
                getUser(player2Id)
            } else Player(player2Id, "Unknown")
            GSM.activeGame = Game(gameId, player1, player2)

            // if (player1.playerId in treasures) treasures[player1Id]?.let { player1.board.setTreasuresList(it) }
            // if (player2.playerId in treasures){ treasures[player2Id]?.let { player2.board.setTreasuresList(it) }}

            println("player1: ${player1}, player2: ${player2}")

            setTreasures(gameId)
        } else {
            throw error("Something went wrong when fetching the Game")
        }
    }

    /**
     * Get the ships in a game
     * @param gameId the id of the game
     * @return a Game object containing game and player
     */
    fun setOpponent(gameId: String) {
        val query = db.collection("games").document(gameId).get()
        val game = query.get()

        if (game.exists()) {
            val player2Id: String = game.get("player2") as String

            GSM.activeGame.opponent.playerId = player2Id
            GSM.activeGame.opponent.playerName = db.collection("users").document(player2Id).get().get().get("username").toString()
            println("name " + GSM.activeGame.opponent.playerName)
            setTreasures(gameId)
        } else {
            throw error("Something went wrong when fetching the Game")
        }
    }

    /**
     * Get the treasures in a game
     * @param gameId the id of the game
     * @return a map containing a list of treasures per user
     */
    /* private fun getTreasures(gameId: String): MutableMap<String, List<Map<String, Any>>> {
        val query = db.collection("games").document(gameId).get()
        val game = query.get()
        if (game.exists()) {
            return game.get("treasures") as MutableMap<String, List<Map<String, Any>>>
        } else {
            throw error("Something went wrong when fetching treasures")
        }
    }*/

    /**
     * set the treasures in a game
     * @param gameId the id of the game
     * @return a map containing a list of treasures per user
     */
    fun setTreasures(gameId: String) {
        println("settreasures is called()")
        val query = db.collection("games").document(gameId).get()
        val game = query.get()
        if (game.exists()) {
            val treasures = game.get("treasures") as MutableMap<String, List<Map<String, Any>>>
            println("all treasures: $treasures")
            GSM.activeGame.setTreasures(treasures)
        } else {
            throw error("Something went wrong when setting treasures")
        }
    }

    /**
     * Registers the move
     * @param gameId the id of the game document
     * @param x x coordinate of move
     * @param y y coordinate of
     * @param playerId player making the move
     */
    fun makeMove(gameId: String, x: Float, y: Float, playerId: String, weapon: String) {
        val query = db.collection("games").document(gameId).get()
        val game = query.get()
        if (game.exists()) {
            val moves = game.get("moves") as MutableList<Map<String, Any>>
            val data = mutableMapOf<String, Any>()
            data["x"] = x
            data["y"] = y
            data["playerId"] = playerId
            data["weapon"] = weapon
            moves.add(data)
            db.collection("games").document(gameId).update("moves", moves)
        } else {
            // Add error handling
            println("Something went wrong when making move")
        }
    }

    /**
     * Set the winner of the game
     * @param userId the id of the winner
     * @param gameId the id of the game document
     */
    fun setWinner(userId: String, gameId: String) {
        db.collection("games").document(gameId).update("winner", userId)
    }

    fun detachGameListener(gameId: String) {
        println("gamelistener removed")
        registration?.remove()
    }

    /**
     * Function adding listener to a specific game
     * TODO: Replace println with functionality connected to the cases
     * TODO: Add exception handling
     * @param gameId the id of the game document
     * @param playerId the id of the player
     */
    fun addGameListener(gameId: String) {

        val query = db.collection("games").document(gameId)
        registration = query.addSnapshotListener(object : EventListener<DocumentSnapshot?> {
            override fun onEvent(
                    @Nullable snapshot: DocumentSnapshot?,
                    @Nullable e: FirestoreException?
            ) {
                if (e != null) {
                    System.err.println("Listen failed: $e")
                    return
                }
                println("Listening ...")
                if (snapshot != null && snapshot.exists()) {
                    val player2 = snapshot.data?.get("player2")
                    // If no opponent has joined yet
                    if (player2 == "") {
                        println("Opponent not joined yet: ${player2}")
                    }
                    // If there is an opponent in the game
                    else {
                        println("opponent id " + GSM.activeGame.opponent.playerId)
                        if (GSM.activeGame.opponent.playerId == "") {
                            setOpponent(gameId)
                        } else {
                            // Get the field containing the treasures in the database
                            val treasures = snapshot.data?.get("treasures") as MutableMap<String, List<Map<String, Any>>>
                            // If there is not enough treasures registered
                            if (treasures.size <= 2 && GSM.activeGame.playersRegistered()) {
                                setTreasures(gameId)
                            } else {
                                // Get the list of moves
                                val moves = snapshot.data?.get("moves") as MutableList<Map<String, Any>>

                                val winner = snapshot.data?.get("winner")
                                // If a winner has been set
                                if (winner != "") {
                                    println("The winner is $winner")
                                    GSM.activeGame.winner = winner as String
                                }
                                // If there is no winner, continue game
                                else {
                                    addMoveListener(gameId)
                                }
                            }
                        }
                    }
                }
                // If no data is found
                else {
                    print("Current data: null")
                }
            }
        })
    }

    fun addMoveListener(gameId: String) {
        val query = db.collection("games").document(gameId)
        val moveListener = query.addSnapshotListener(object : EventListener<DocumentSnapshot?> {
            override fun onEvent(
                    @Nullable snapshot: DocumentSnapshot?,
                    @Nullable e: FirestoreException?
            ) {
                if (e != null) {
                    System.err.println("Listen failed: $e")
                    return
                }
                println("   MOVE LISTENER:")
                if (snapshot != null && snapshot.exists()) {
                    // Get the list of moves
                    val moves = snapshot.data?.get("moves") as MutableList<Map<String, Any>>

                    val winner = snapshot.data?.get("winner")
                    // If a winner has been set
                    if (winner != "") {
                        println("The winner is $winner")
                        GSM.activeGame.winner = winner as String
                    }
                    // If there is no winner, continue game
                    else {
                        // If no moves has been made yet
                        if (moves.size == 0) {
                            println("No moves made yet")
                        } else {
                            // Get the last move
                            val lastMove = moves.get(moves.size - 1)
                            // If the last move is performed by opponent
                            val game = GSM.activeGame
                            if (lastMove["playerId"]!!.equals(game.opponent.playerId)) {
                                println("Motstander hadde siste trekk - din tur")
                                GSM.activeGame.flipPlayer()
                            } else if (lastMove["playerId"]!!.equals(game.me.playerId)) {
                                println("Du hadde siste trekk, vent")
                                GSM.activeGame.flipPlayer()
                            }
                        }
                    }
                }
                // If no data is found
                else {
                    print("Current data: null")
                }
            }
        })
    }
}