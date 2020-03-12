package com.battleship.controller.firebase
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Class used to setup the database connection
 */
abstract class FirebaseController {
    //The URL of the firebase project
    private val firebaseUrl = "https://battleshipz.firebaseio.com"
    //Protected variable used by the other controllers to access database
    protected val db: Firestore;
    //The Api key used for identifying the firebase project during authentication
    protected val API_KEY = "AIzaSyCyEdKGVFy96OPy_Kgxyb6-5_WB55K24M0"

    //Set up database connection
    init{
        //Get the firebase apps running
        val firebaseApps = FirebaseApp.getApps()

            //If no firebase apps is running, set it up
            if(firebaseApps.size == 0){
                //Read the account details from file
                val serviceAccount = FileInputStream("./core/src/com/battleship/controller/firebase/adminsdk.json")
                //Get the credentials from the account details
                val credentials = GoogleCredentials.fromStream(serviceAccount)
                //Set options for connection
                val options = FirebaseOptions.Builder()
                        .setCredentials(credentials)
                        .setDatabaseUrl(firebaseUrl)
                        .build()
                FirebaseApp.initializeApp(options)
            }

        //Initialize database connection using the firebase app
        db = FirestoreClient.getFirestore()
    }
}
