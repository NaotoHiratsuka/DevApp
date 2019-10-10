package com.example.devapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MainActivity : AppCompatActivity() {

    lateinit var db: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance().document("NowIsHot/Member")

        val button = findViewById<Button>(R.id.save)

        button.setOnClickListener {
            val inputKeyEditText = findViewById<EditText>(R.id.inputKey)
            val inputValueEditText = findViewById<EditText>(R.id.inputValue)
            val inputKeyText = inputKeyEditText.text.toString()
            val inputValueText = inputValueEditText.text.toString()
            var isFailed = false
            if (inputKeyText.isEmpty()) {
                inputKeyEditText.error = "Please input anything."
                isFailed = true
            }
            if (inputValueText.isEmpty()) {
                inputValueEditText.error = "Please input anything."
                isFailed = true
            }

            if (isFailed) {
                return@setOnClickListener
            }
            val dataToSave = hashMapOf(
                inputKeyText to inputValueText
            )

            db
                .set(dataToSave, SetOptions.merge())
                .addOnSuccessListener { Log.d("print", "Document successfully written!") }
                .addOnFailureListener { e -> Log.w("print", "Error writing document", e) }
        }
    }
}
