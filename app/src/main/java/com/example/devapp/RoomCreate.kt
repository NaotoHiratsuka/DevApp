package com.example.devapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RoomCreateActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, RoomActivity::class.java)
        startActivity(intent)
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.room_create_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val db = FirebaseFirestore.getInstance()
        when(item?.itemId)  {
            R.id.confirm -> {
                val inputEditText = findViewById<EditText>(R.id.input)
                val inputText = inputEditText.text.toString()

                var isFailed = false

                if (inputText.isEmpty()) {
                    inputEditText.error = "Please input anything."
                    isFailed = true
                }

                if (isFailed) {
                    return false
                }

                val roomsRef = db.collection("rooms").document()
                val messageRef = roomsRef.collection("messages")
                val dataToSave = hashMapOf(
                    "name" to inputText,
                    "messagePath" to messageRef.path
                )

                roomsRef
                    .set(dataToSave, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("print", "Document successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "print",
                            "Error writing document", e
                        )
                    }
                val intent = Intent(this, RoomActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_create)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Create New Room"
    }
}