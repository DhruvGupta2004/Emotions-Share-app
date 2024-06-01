package com.example.emotions

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PostAdapter(private val userList : ArrayList<Post> ) : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {


//    private lateinit var myListener: onItemClickListener
//    interface onItemClickListener {
//        fun onItemClicking(position: Int)
//    }
//
//    fun setOnItemClickListener(listener : onItemClickListener){
//        myListener = listener
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_item_layout,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]
        holder.heading.text = currentitem.heading
        holder.content.text = currentitem.content

    }

    override fun getItemCount(): Int {
        return userList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val heading: TextView = itemView.findViewById(R.id.textViewHeading)
        val content: TextView = itemView.findViewById(R.id.textViewContent)

//        init {
//            itemView.setOnClickListener {
//                listener.onItemClicking(adapterPosition)
//            }
//        }

    }

}