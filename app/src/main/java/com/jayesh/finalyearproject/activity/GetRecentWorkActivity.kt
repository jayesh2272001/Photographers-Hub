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
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.RecentWorkAdapter
import java.util.*
import kotlin.collections.HashMap

class GetRecentWorkActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var storageReference: StorageReference? = null
    lateinit var databaseReference: FirebaseDatabase
    private val pickImage = 2
    private val dataUri = ArrayList<Uri>()
    private lateinit var imageUrls: ArrayList<String>
    lateinit var rvRecentWork: RecyclerView
    lateinit var tvSkip: TextView
    lateinit var btnSubmit: Button
    lateinit var btnSelectImages: Button
    lateinit var rlProgressBar: RelativeLayout
    private lateinit var mdbRef: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_recent_work)

        rvRecentWork = findViewById(R.id.rvRecentWork)
        btnSubmit = findViewById(R.id.btnSubmitRecentWork)
        btnSelectImages = findViewById(R.id.btnSelectImages)
        tvSkip = findViewById(R.id.tvSkip)
        auth = FirebaseAuth.getInstance()
        mdbRef = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        databaseReference = FirebaseDatabase.getInstance()
        rlProgressBar = findViewById(R.id.rlProgressBar)
        storageReference = FirebaseStorage.getInstance().reference
        imageUrls = ArrayList()

        selectDefaultImages()

        //selecting images
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
            uploadImages()
        }

    }

    private fun selectDefaultImages() {
        val curUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
        mdbRef.child("users").child(curUser).child("recent_work")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
//                        Log.i("Recent work", "Existing work :$postSnapshot")

//                        val image = postSnapshot.getValue(ImageUrl::class.java)
                        imageUrls.add(postSnapshot.value.toString())
                    }
                    for (item in imageUrls) {
                        Log.i("Recent work", "Existing work :$item")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        //setting default images to recycler view


    }

    private fun uploadImages() {
        rlProgressBar.visibility = View.VISIBLE

        var uploadCount = 0
        val curUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
        for (item in dataUri) {
            val imageName: StorageReference =
                storageReference!!.child("image" + item.lastPathSegment)
            //val reference = storageReference?.child("myImages/")
            imageName.putFile(item).addOnSuccessListener {
                imageName.downloadUrl.addOnSuccessListener { uri ->
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("imageUrl", uri.toString())
                    mdbRef.child("users").child(curUser).child("recent_work").push()
                        .setValue(hashMap).addOnSuccessListener {
                            Toast.makeText(this, "Images uploaded successfully", Toast.LENGTH_SHORT)
                                .show()
                        }

                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImage) {
            if (resultCode == RESULT_OK) {
                if (data?.clipData != null) {
                    val clipCount: Int = data.clipData!!.itemCount
                    var currentImage = 0
                    while (currentImage < clipCount) {
                        val image: Uri = data.clipData!!.getItemAt(currentImage).uri
                        dataUri.add(image)
                        currentImage += 1
                    }
                    Toast.makeText(
                        this,
                        "You Have Selected " + dataUri.size.toString() + " Pictures",
                        Toast.LENGTH_SHORT
                    ).show()
                    rvRecentWork.adapter = RecentWorkAdapter(this, dataUri)
                    rvRecentWork.layoutManager = GridLayoutManager(this, 3)
                } else {
                    Toast.makeText(this, "Please Select Multiple Images", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}