package com.battleship

import android.util.Log
import com.battleship.controller.firebase.FirebaseController
import com.battleship.model.Game
import com.battleship.model.GameListObject
import com.battleship.model.Player
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.runBlocking

object AndroidFirebase : FirebaseController {
    private val db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private var activeListener: ListenerRegistration? = null
        set(value) {
            field?.remove()
            field = value
        }

    init {
        // Sign in as an anonymous user to authorize, don't know if runBlocking actually works
        runBlocking {
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Login", "signInAnonymously successful")
                    } else {
                        // TODO: Handle exception, when this occurs you cannot access firebase
                        Log.w("Login", "signInAnonymously failed", task.exception)
                    }
                }
        }
    }

    /**
     * Start new game
     * @param userId the id of the user setting up the game
     * @param userName the name of the user setting up the game
     */
    override fun createGame(userId: String, userName: String) {
        // Set up game data
        val data = mutableMapOf<String, Any>()
        data["player1Id"] = userId
        data["player1Name"] = userName
        data["player2Id"] = ""
        data["player2Name"] = ""
        data["winner"] = ""
        data["moves"] = mutableListOf<Map<String, Any>>()
        data["treasures"] = mutableMapOf<String, List<Map<String, Any>>>()

        db.collection("games").add(data)
            .addOnSuccessListener { documentReference ->
                val gameId = documentReference.id
                Log.d("createGame", "created game with id=$gameId")
                GSM.activeGame = Game(gameId)
                val player1 = Player(userId, userName)
                GSM.activeGame!!.setPlayers(player1, Player())
            }
            .addOnFailureListener { exception ->
                Log.w("createGame", exception)
                // TODO: Add exception handling
            }
    }

    /**
     * Add a player to a specific game
     * @param gameId the id of the game document
     * @param player2Id the id of the player that should be added
     * @param player2Name the name of the player that should be added
     */
    override fun joinGame(gameId: String, player2Id: String, player2Name: String) {
        val docRef = db.collection("games").document(gameId)
        var player1 = Player()
        var player2 = Player()
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            // Get player1
            val player1Id: String = snapshot.getString("player1Id") as String
            val player1Name: String = snapshot.getString("player1Name") as String
            player1 = Player(player1Id, player1Name)

            // Set player2
            transaction.update(docRef, "player2Id", player2Id)
            transaction.update(docRef, "player2Name", player2Name)
            player2 = Player(player2Id, player2Name)

        }.addOnSuccessListener {
            // Creates a new game and registers player1 and player2
            GSM.activeGame = Game(gameId)
            GSM.activeGame!!.setPlayers(player1, player2)
            Log.d("joinGame", "Joined "  + player1.playerName +"'s game successfully!")
        }.addOnFailureListener { e ->
            // TODO: Add exception handling, could not find games
            Log.w("joinGame", "Transaction Joined game failed!", e)
        }
    }

    override fun addPendingGamesListener() {
        val pendingGames = ArrayList<GameListObject>()
        activeListener =
            db.collection("games").whereEqualTo("player2Name", "").whereEqualTo("player2Id", "")
                .addSnapshotListener { documents, e ->
                    if (e != null) {
                        Log.w("addPendingGamesListener", "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    Log.d("addPendingGamesListener", "Found documents containing pending games")
                    for (doc in documents!!) {
                        val player1Id: String = doc.getString("player1Id") as String
                        val player1Name: String = doc.getString("player1Name") as String
                        val gameId = doc.id
                        if (player1Id != "") {
                            pendingGames.add(
                                GameListObject(
                                    gameId,
                                    player1Id,
                                    player1Name
                                )
                            )
                        }
                    }
                    println("Pending games: $pendingGames")
                    GSM.pendingGames = pendingGames
                }
    }

    /**
     * Registers the treasures on the board for a given user
     * @param gameId the id of the game document
     * @param userId the id of the user owning the treasure
     * @param treasures list containing the treasures that should be added, each described using a map
     */
    override fun registerTreasures(
        gameId: String,
        userId: String,
        treasures: List<Map<String, Any>>
    ) {
        db.collection("games").document(gameId).get()
            .addOnSuccessListener { document ->
                val dbTreasures =
                    document.get("treasures") as MutableMap<String, List<Map<String, Any>>>
                dbTreasures[userId] = treasures
                db.collection("games").document(gameId).update("treasures", dbTreasures)
                    .addOnSuccessListener {
                        Log.d("registerTreasures", "treasures registered")
                    }
                    .addOnFailureListener { exception ->
                        Log.w("registerTreasures", exception)
                        // TODO: Add exception handling
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("registerTreasures", exception)
                // TODO: Add exception handling
            }
    }

    /**
     * Registers the move
     * @param gameId the id of the game document
     * @param x x coordinate of move
     * @param y y coordinate of
     * @param playerId player making the move
     */
    override fun makeMove(gameId: String, x: Int, y: Int, playerId: String, weapon: String) {
        // Get the document for the game using the id of the document
        db.collection("games").document(gameId).get()
            .addOnSuccessListener { document ->
                // Saving existing moves
                var moves = mutableListOf<Map<String, Any>>()
                if (document.get("moves") != null) {
                    moves = document.get("moves") as MutableList<Map<String, Any>>
                }
                // Making a map for the new move
                val data = mutableMapOf<String, Any>()
                data["x"] = x
                data["y"] = y
                data["playerId"] = playerId
                data["weapon"] = weapon
                // Add the move to the list of existing moves
                moves.add(data)
                // Push the list of moves to the database
                db.collection("games").document(gameId).update("moves", moves)
                    .addOnSuccessListener {
                        Log.d("makeMove", "move made successfully")
                    }
                    .addOnFailureListener { exception ->
                        Log.w("makeMove", exception)
                        // TODO: Add exception handling, could not push data to db
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("makeMove", exception)
                // TODO: Add exception handling, could not find game document
            }
    }

    /**
     * Set the winner of the game
     * @param userId the id of the winner
     * @param gameId the id of the game document
     */
    override fun setWinner(userId: String, gameId: String) {
        db.collection("games").document(gameId).update("winner", userId)
            .addOnSuccessListener {
                Log.d("setWinner", "success")
                // TODO: Call function that handles what should happen after a player won
            }
            .addOnFailureListener { exception ->
                Log.w("setWinner", exception)
                // TODO: Add exception handling
            }
    }

    /**
     * Function adding listener to a specific game
     * @param gameId the id of the game document
     * @param playerId the id of the player
     */
    override fun addGameListener(gameId: String, playerId: String) {
        val docRef = db.collection("games").document(gameId)
        activeListener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("addGameListener", "Listener failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("addGameListener", "Game data: ${snapshot.data}")
                val opponent = snapshot.data?.get("player2Id")
                // If no opponent has joined yet
                if (opponent == "") {
                    Log.d("addGameListener", "Opponent not joined yet")
                }
                // If there is an opponent in the game
                else {
                    if (GSM.activeGame!!.opponent.playerId == "") {
                        Log.d("addGameListener", "Opponent joined and registered")
                        val player2Id = snapshot.data?.get("player2Id") as String
                        val player2Name = snapshot.data?.get("player2Name") as String
                        GSM.activeGame!!.opponent.playerId = player2Id
                        GSM.activeGame!!.opponent.playerName = player2Name
                        GSM.activeGame!!.setGameReadyIfReady()
                    } else {
                        // Get the field containing the treasures in the database
                        var treasures: MutableMap<String, List<Map<String, Any>>>
                        if (snapshot.data?.get("treasures") != null) {
                            treasures =
                                snapshot.data?.get("treasures") as MutableMap<String, List<Map<String, Any>>>

                            // If there is not enough treasures registered
                            if (treasures.size < 2 && GSM.activeGame!!.isPlayersRegistered()) {
                                Log.d("addGameListener", "opponent's treasures registered")
                                val OplayerId = GSM.activeGame!!.opponent.playerId
                                if (OplayerId in treasures) {
                                    treasures[OplayerId]?.let {
                                        GSM.activeGame!!.opponent.board.setTreasuresList(it)
                                    }
                                    GSM.activeGame!!.setGameReadyIfReady()
                                }
                            }
                            // If there is enough treasures registered in firebase but not in opponents board
                            else if (treasures.size == 2 && !GSM.activeGame!!.isTreasuresRegistered()) {
                                val OplayerId = GSM.activeGame!!.opponent.playerId
                                if (OplayerId in treasures) {
                                    treasures[OplayerId]?.let {
                                        GSM.activeGame!!.opponent.board.setTreasuresList(it)
                                    }
                                    GSM.activeGame!!.setGameReadyIfReady()
                                }
                            } else {
                                addPlayListener(gameId)
                            }
                        }
                    }
                }
            } else
                Log.d("addGameListener", "no data")
            // TODO: Error handling when the game object is found, but there is no data in it
        }
    }

    override fun addPlayListener(gameId: String) {
        val docRef = db.collection("games").document(gameId)
        activeListener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("addPlayListener", "Listener failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("addPlayListener", "Game data: ${snapshot.data}")

                var moves = mutableListOf<Map<String, Any>>()
                if (snapshot.data?.get("moves") != null) {
                    moves =
                        snapshot.data?.get("moves") as MutableList<Map<String, Any>>
                }

                val winner = snapshot.data?.get("winner")
                // If a winner has been set
                if (winner != "") {
                    Log.d("addPlayListener", "The winner is $winner")
                    // TODO: Call function that should be called when a winner is registered
                    GSM.activeGame!!.winner = winner as String // or something
                }
                // If there is no winner, continue game
                else {
                    // If no moves has been made yet
                    if (moves.size == 0) {
                        Log.d("addPlayListener", "No moves made yet")
                        GSM.activeGame!!.setGameReadyIfReady()
                    } else {
                        // Get the last move
                        val lastMove = moves.get(moves.size - 1)
                        val game = GSM.activeGame!!
                        if (lastMove["playerId"]!!.equals(game.opponent.playerId)) {
                            println("----------------------OPPONENT LAST MOVE----------------------- " + lastMove)
                            GSM.activeGame!!.registerMove(lastMove)
                        } else if (lastMove["playerId"]!!.equals(game.player.playerId)) {
                            println("----------------------PLAYER LAST MOVE----------------------- " + lastMove)
                        }
                    }
                }
            } else
                Log.d("addPlayListener", "no data")
            // TODO: Error handling when the game object is found, but there is no data in it
        }
    }
}
