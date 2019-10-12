package com.example.devapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}


val editText = findViewById<EditText>(R.id.edit_text) as EditText
val button = findViewById<Button>(R.id.save)
button.setOnClickListener {
    if(edit_text.text != null) {
        text_view.text = editText.text.toString()
    }
}