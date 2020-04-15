package com.battleship.desktop

import com.badlogic.gdx.math.Vector2
import com.battleship.GSM
import com.battleship.controller.firebase.FirebaseController
import com.battleship.model.Game
import com.battleship.model.GameListObject
import com.battleship.model.Player
import com.battleship.model.treasures.Boot
import com.battleship.model.treasures.GoldCoin
import com.battleship.model.treasures.Treasure
import com.battleship.model.treasures.TreasureChest
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.EventListener
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreException
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.database.annotations.Nullable
import java.io.FileInputStream

/**
 * Class used to setup the database connection
 * declared as object to make it a singleton
 */
object DesktopFirebase : FirebaseController {
    // The URL of the firebase project
    private const val firebaseUrl = "https://battleshipz.firebaseio.com"

    // Protected variable used by the other controllers to access database
    private val db: Firestore

    // Set up database connection
    init {
        // Get the firebase apps running
        val firebaseApps = FirebaseApp.getApps()

        // If no firebase apps is running, set it up
        if (firebaseApps.size == 0) {
            // Read the account details from file
            val serviceAccount = FileInputStream("./adminsdk.json")
            // Get the credentials from the account details
            val credentials = GoogleCredentials.fromStream(serviceAccount)
            // Set options for connection
            val options = FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setDatabaseUrl(firebaseUrl)
                .build()
            FirebaseApp.initializeApp(options)
        }

        // Initialize database connection using the firebase app
        db = FirestoreClient.getFirestore()
    }

    /**
     * Get all the players registered in the database
     * @return a map containing user id and username
     */
    override fun getPlayers() {
        val query = db.collection("users").get()
        val querySnapshot = query.get()
        val documents = querySnapshot.documents
        val playerMap = mutableMapOf<String, String?>()

        for (document in documents) {
            val id = document.id
            val name = document.getString("username")
            playerMap[id] = name
        }
        // TODO: Call function that saves the playerMap
    }

    /**
     * Register new player in the db
     * @param username the username wanted
     * @return id of player
     */
    override fun addPlayer(username: String) {
        // Add the data to a Map for pushing to db
        val data = mutableMapOf<String, Any>()
        data["username"] = username

        // Push to db
        val addRes = db.collection("users").add(data)
        GSM.userId = addRes.get().id
    }

    /**
     * Start new game
     * @param userId the id of the user setting up the game
     */
    override fun createGame(userId: String) {
        // Set up game data
        val data = mutableMapOf<String, Any>()
        data["player1"] = userId
        data["player2"] = ""
        data["winner"] = ""
        data["moves"] = mutableListOf<Map<String, Any>>()
        data["treasures"] = mutableMapOf<String, List<Map<String, Any>>>()

        val res = db.collection("games").add(data)
        val gameId = res.get().id
        setGame(gameId)
    }

    /**
     * Function getting all games where there is currently only one player
     */
    override fun getPendingGames() {
        GSM.pendingGames = ArrayList<GameListObject>()
        val gameQuery = db.collection("games").whereEqualTo("player2", "").get()
        val gameQuerySnapshot = gameQuery.get()
        val gameDocuments = gameQuerySnapshot.documents
        // For each game fitting the criteria, get the id and username of opponent
        for (document in gameDocuments) {
            val gameId = document.id
            val playerId = document.getString("player1")

            // Find the username of the player in the game to display
            val playerQuery = playerId?.let { db.collection("users").document(playerId).get() }
            val playerQuerySnapshot = playerQuery?.get()
            val username = playerQuerySnapshot?.getString("username")
            if (username != null) {
                GSM.pendingGames.add(GameListObject(gameId, playerId, username))
            }
        }
        // TODO: Call function that saves the "games" list of pending games
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
    override fun joinGame(gameId: String, userId: String) {
        // Add the data to the game document
        db.collection("games").document(gameId).update("player2", userId)
        setGame(gameId)
    }

    /**
     * Registers the treasures on the board for a given user
     * @param gameId the id of the game document
     * @param userId the id of the user owning the treasures
     * @param treasures list containing the treasures that should be added, each described using a map
     */
    override fun registerTreasures(
        gameId: String,
        userId: String,
        treasures: List<Map<String, Any>>
    ) {
        val query = db.collection("games").document(gameId).get()
        val game = query.get()
        if (game.exists()) {
            val dbTreasures = game.get("treasures") as MutableMap<String, List<Map<String, Any>>>
            dbTreasures[userId] = treasures
            db.collection("games").document(gameId).update("treasures", dbTreasures)
        } else {
            // TODO: Add exception handling
            println("Something went wrong when registering treasures")
        }
    }

    /**
     * Get the treasures in a game
     * @param gameId the id of the game
     * @return a map containing a list of treasures per user
     * @return a Game object containing game and player
     */
    override fun setGame(gameId: String) {
        GSM.activeGame = Game(gameId)
        val query = db.collection("games").document(gameId).get()
        val game = query.get()

        if (game.exists()) {
            val player1Id: String = game.get("player1") as String
            val player1: Player = getUser(player1Id)

            val player2Id: String = game.get("player2") as String
            val player2: Player = if (player2Id != "") getUser(player2Id) else Player()

            GSM.activeGame?.setPlayers(player1, player2)
            println("setGame: ${GSM.activeGame}")
        } else {
            throw error("Something went wrong when setting the Game")
        }
    }

    /**
     * Get the ships in a game
     * @param gameId the id of the game
     * @return a Game object containing game and player
     */
    override fun getOpponent(gameId: String) {
        val query = db.collection("games").document(gameId).get()
        val game = query.get()

        if (game.exists()) {
            val player2Id: String = game.get("player2") as String

            GSM.activeGame!!.opponent.playerId = player2Id
            GSM.activeGame!!.opponent.playerName = db.collection("users").document(player2Id).get().get().get("username").toString()
            println("opponent name: " + GSM.activeGame!!.opponent.playerName)
        } else {
            throw error("Something went wrong when fetching the Game")
        }
    }

    /**
     * get the treasures from firebase and stores them in a game
     * @param gameId the id of the game
     * @return a map containing a list of treasures per user
     */
    override fun getTreasures(gameId: String) {
        val query = db.collection("games").document(gameId).get()
        val game = query.get()
        if (game.exists()) {
            val treasures = game.get("treasures") as MutableMap<String, List<Map<String, Any>>>
            // val treasureO = treasures[GSM.activeGame!!.opponent.playerId]
            if (treasures != null) {
                // GSM.activeGame!!.setTreasures(treasures)
                if (GSM.activeGame!!.me.playerId in treasures) {
                    treasures[GSM.activeGame!!.me.playerId]?.let { GSM.activeGame!!.me.board.setTreasuresList(it) }
                }
                if (GSM.activeGame!!.opponent.playerId in treasures){
                    treasures[GSM.activeGame!!.opponent.playerId]?.let {
                        GSM.activeGame!!.opponent.board.setTreasuresList(it)
                    }
                    GSM.activeGame!!.isGameReady()
                }
            }
        } else {
            // TODO: Add error handling
            throw error("Something went wrong when fetching treasures")
        }
    }
    private fun List<Map<String, Any>>.treasureToList() : ArrayList<Treasure> {
        var treasures = ArrayList<Treasure>()
        lateinit var newTreasure: Treasure
        lateinit var position: Vector2
        var rotate = true
        for (treasure in this) {
            print("${treasure["type"]} , ${treasure["rotate"]}, ${treasure["x"]} ${treasure["y"]}")
            position = Vector2((treasure["x"] as Number).toFloat(), (treasure["y"] as Number).toFloat())
            rotate = if (treasure.containsKey("rotate")) treasure["rotate"] as Boolean else false

            when (treasure["type"]) {
                "Gold coin" ->
                    newTreasure = GoldCoin(position, rotate)
                "Treasure chest" ->
                    newTreasure = TreasureChest(position, rotate)
                "Boot" ->
                    newTreasure = Boot(position, rotate)
                "Old stinking boot" ->
                    newTreasure = Boot(position, rotate)
            }
            treasures.add(newTreasure)
        }
        println("- new treasure: $treasures")
        return treasures
    }

    /**
     * Registers the move
     * @param gameId the id of the game document
     * @param x x coordinate of move
     * @param y y coordinate of
     * @param playerId player making the move
     */
    override fun makeMove(gameId: String, x: Int, y: Int, playerId: String, weapon: String) {
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
            // TODO: Call function that handles what should happen after move is made
        } else {
            // TODO: Add exception handling
            println("Something went wrong when making move")
        }
    }

    /**
     * Set the winner of the game
     * @param userId the id of the winner
     * @param gameId the id of the game document
     */
    override fun setWinner(userId: String, gameId: String) {
        db.collection("games").document(gameId).update("winner", userId)
        // TODO: Call function that should handle what happens when a winner is set
    }

    /**
     * Function adding listener to a specific game
     * TODO: Replace println with functionality connected to the cases
     * TODO: Add exception handling
     * @param gameId the id of the game document
     * @param playerId the id of the player
     */
    override fun addGameListener(gameId: String, playerId: String) {
        val docRef = db.collection("games").document(gameId)
        docRef.addSnapshotListener(object : EventListener<DocumentSnapshot?> {
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
                    val opponent = snapshot.data?.get("player2")
                    // If no opponent has joined yet
                    if (opponent == "") {
                        println("Opponent not joined yet")
                    }
                    // If there is an opponent in the game
                    else {
                        // println("opponent id " + GSM.activeGame!!.opponent.playerId)
                        if (GSM.activeGame!!.opponent.playerId == "") {
                            getOpponent(gameId)
                        } else {
                            // Get the field containing the treasures in the database
                            val treasures = snapshot.data?.get("treasures") as MutableMap<String, List<Map<String, Any>>>
                            // If there is not enough treasures registered
                            if (treasures.size <= 2 && GSM.activeGame!!.playersRegistered()) {
                                println("addGameListener:"+ "Treasures not registered")
                                getTreasures(gameId)
                            } else {
                                // Get the list of moves
                                val moves = snapshot.data?.get("moves") as MutableList<Map<String, Any>>

                                val winner = snapshot.data?.get("winner")
                                // If a winner has been set
                                if (winner != "") {
                                    println("The winner is $winner")
                                    GSM.activeGame!!.winner = winner as String
                                }
                                // If there is no winner, continue game
                                else {
                                    addMoveListener(gameId, playerId)
                                    // remove this listener ? and only listen for moves
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

    fun addMoveListener(gameId: String, playerId: String) {
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
                        GSM.activeGame!!.winner = winner as String
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
                            val game = GSM.activeGame!!
                            if (lastMove["playerId"]!!.equals(game.opponent.playerId)) {
                                println("Motstander hadde siste trekk - din tur")
                                GSM.activeGame!!.flipPlayer()
                            } else if (lastMove["playerId"]!!.equals(game.me.playerId)) {
                                println("Du hadde siste trekk, vent")
                                GSM.activeGame!!.flipPlayer()
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