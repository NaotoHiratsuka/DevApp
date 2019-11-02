package com.example.devapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.Serializable

class RoomActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, TitleActivity::class.java)
        startActivity(intent)
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.room_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)  {
            R.id.add -> {
                val intent = Intent(this, RoomCreateActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Rooms"

        val recyclerView = findViewById<RecyclerView>(R.id.list)

        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("rooms")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("print", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val results = snapshot.toObjects(RoomBlock::class.java)
                recyclerView.adapter = RoomAdapter(this, results,
                    object : RoomAdapter.ClickListener {
                    override fun onClick(view: View, data: RoomBlock) {
                        val intent = Intent(this@RoomActivity, ChatActivity::class.java)
                        intent.putExtra("RoomBlock", data)
                        startActivity(intent)
                    }
                })
                Log.d("print", "Current data: ${snapshot.documents}")
            } else {
                Log.d("print", "Current data: null")
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false)
    }
}

data class RoomBlock (
    val name: String = "",
    val messagePath: String = ""
): Serializable

class RoomAdapter(private val context: Context, private val items: List<RoomBlock>,
                  private val listener: ClickListener) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.name)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemView.setOnClickListener {
            listener.onClick(it, items[position])
        }
    }

    interface ClickListener {
        fun onClick(view: View, data: RoomBlock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder =
        RoomViewHolder(LayoutInflater.from(context).inflate(R.layout.room_block, parent,
            false))
}