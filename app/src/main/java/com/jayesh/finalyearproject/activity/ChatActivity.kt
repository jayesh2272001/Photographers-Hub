package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.MessageAdapter
import com.jayesh.finalyearproject.model.Message
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendMessageBtn: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mdbRef: DatabaseReference
    private lateinit var receiverUid: String
    private lateinit var name: String
    private lateinit var profilePictureUrl: String
    private lateinit var tbChatActivity: Toolbar
    private lateinit var profileName: TextView
    private lateinit var profilePicture: CircleImageView

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //getting intent from message Photographers Description activity
        receiverUid = intent.getStringExtra("uid").toString()
        name = intent.getStringExtra("name").toString()
        profilePictureUrl = intent.getStringExtra("profileImage").toString()
        Log.i("ChatActivity", "uid:$receiverUid")
        Log.i("ChatActivity", "name:$name")

        //sender room
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        chatRecyclerView = findViewById(R.id.rvChatActivity)
        messageBox = findViewById(R.id.etMessageBox)
        sendMessageBtn = findViewById(R.id.ivSendMessage)
        tbChatActivity = findViewById(R.id.tbChatActivity)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        mdbRef = FirebaseDatabase.getInstance().reference
        profileName = findViewById(R.id.profileName)
        profilePicture = findViewById(R.id.profilePicture)

        setUpToolBar(tbChatActivity)
        profileName.text = name
        Glide.with(this).load(profilePictureUrl)
            .into(profilePicture).view

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        //logic fpr adding data to recycler view
        mdbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        //adding message to database
        sendMessageBtn.setOnClickListener {
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            mdbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mdbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.setText("")
        }
    }

    private fun setUpToolBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}