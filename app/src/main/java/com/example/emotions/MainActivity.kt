package com.example.emotions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotions.editor.RichEditor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<Post>
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val float = findViewById<FloatingActionButton>(R.id.fab)
        float.setOnClickListener {
            val intent = Intent(this, RichEditor::class.java)
            startActivity(intent)
        }

        userRecyclerView = findViewById(R.id.recyclerViewPosts)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = ArrayList()
        postAdapter = PostAdapter(userArrayList)
        userRecyclerView.adapter = postAdapter

//        postAdapter.setOnItemClickListener(object :PostAdapter.onItemClickListener {
//            override fun onItemClicking(position: Int) {
//                // on clicking each item , what action do you want to perform
//
//                val intent = Intent(this@MainActivity, DetailActivity::class.java)
//                intent.putExtra("heading", userArrayList[position].heading)
//                intent.putExtra("content", userArrayList[position].content)
//                startActivity(intent)
//            }
//
//        })


        dbref = FirebaseDatabase.getInstance().getReference("users")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userArrayList.clear()
                    for (userSnapshot in snapshot.children) {

                        val postsRef = userSnapshot.child("posts")
                        for (postSnapshot in postsRef.children) {
                            val title = postSnapshot.child("title").getValue(String::class.java)
                            val content = postSnapshot.child("content").getValue(String::class.java)
                            if (title != null && content != null) {
                                val post = Post(title, content)
                                userArrayList.add(post)
                            }

                        }
                    }
                    userRecyclerView.adapter = PostAdapter(userArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }
}
