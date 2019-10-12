package com.example.devapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.list)

        val db = FirebaseFirestore.getInstance()

        db.collection("messages").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("print", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val results = snapshot.toObjects(ChatBlock::class.java).sortedBy { chatBlock -> chatBlock.timestamp}
                recyclerView.adapter = ChatAdapter(this, results)
                Log.d("print", "Current data: ${snapshot.documents}")
            } else {
                Log.d("print", "Current data: null")
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false)

        val button = findViewById<Button>(R.id.send)
        button.setOnClickListener {
            val inputKeyEditText = findViewById<EditText>(R.id.inputKey)
            val inputKeyText = inputKeyEditText.text.toString()

            var isFailed = false

            if (inputKeyText.isEmpty()) {
                inputKeyEditText.error = "Please input anything."
                isFailed = true
            }

            if (isFailed) {
                return@setOnClickListener
            }

            inputKeyEditText.text.clear()

            val dataToSave = hashMapOf(
                "message" to inputKeyText,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("messages").document()
                .set(dataToSave, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("print", "Document successfully written!") }
                .addOnFailureListener { e -> Log.w("print", "Error writing document", e) }
        }
    }
}

data class ChatBlock(
    val message: String = "",
    val timestamp: Date = Date()
)

class ChatAdapter(private val context: Context, private var items: List<ChatBlock>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemMessage: TextView = view.findViewById(R.id.message)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = items[position]
        holder.itemMessage.text = item.message
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
        ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_block, parent, false))
}
