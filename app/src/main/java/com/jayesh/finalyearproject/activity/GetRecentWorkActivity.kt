package com.jayesh.finalyearproject.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.BookmarkRecyclerAdapter
import com.jayesh.finalyearproject.adapter.RecentWorkAdapter
import java.util.*

class GetRecentWorkActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var storageReference: StorageReference? = null
    lateinit var databaseReference: FirebaseDatabase
    private val pickImage = 2
    private val imageList = ArrayList<Uri>()
    private val urlList = ArrayList<String>()
    lateinit var rvRecentWork: RecyclerView
    lateinit var tvSkip: TextView
    lateinit var btnSubmit: Button
    lateinit var btnSelectImages: Button
    lateinit var rlProgressBar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_recent_work)

        rvRecentWork = findViewById(R.id.rvRecentWork)
        btnSubmit = findViewById(R.id.btnSubmitRecentWork)
        btnSelectImages = findViewById(R.id.btnSelectImages)
        tvSkip = findViewById(R.id.tvSkip)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        storage = FirebaseStorage.getInstance()
        databaseReference = FirebaseDatabase.getInstance()
        rlProgressBar = findViewById(R.id.rlProgressBar)
        storageReference = FirebaseStorage.getInstance().reference

        btnSelectImages.setOnClickListener {
            //we will pick images
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, pickImage)
        }

        tvSkip.setOnClickListener {
            //TODO:ask for confirmation from user
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnSubmit.setOnClickListener {
            rlProgressBar.visibility = View.VISIBLE

            if (imageList.isNotEmpty()) {
                val reference = storageReference?.child("myImages/")
                for (image in imageList) {
                    reference?.putFile(image)?.addOnCompleteListener {
                        reference.downloadUrl.addOnSuccessListener { task ->
                            urlList.add(task.toString())
                        }
                    }
//                    Log.i("Recent Work ", "Uploaded Images URL $urlList")
                }
                rlProgressBar.visibility = View.GONE
                if (urlList.isNotEmpty()) {
                    Log.i("Recent work", "Images url $urlList")
                } else {
                    Toast.makeText(this, "Please try again..", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImage) {
            if (resultCode == RESULT_OK) {
                if (data?.clipData != null) {
                    val count: Int = data.clipData!!.itemCount
                    var currentImageSelect = 0
                    while (currentImageSelect < count) {
                        val imageUri: Uri =
                            data.clipData?.getItemAt(currentImageSelect)?.uri!!
                        imageList.add(imageUri)
                        currentImageSelect += 1
                    }
                    Toast.makeText(
                        this,
                        "You Have Selected " + imageList.size.toString() + " Pictures",
                        Toast.LENGTH_SHORT
                    ).show()
                    rvRecentWork.adapter = RecentWorkAdapter(this, imageList)
                    rvRecentWork.layoutManager = GridLayoutManager(this, 3)
                }
            }
        }
    }
}