package com.example.textme.AdapterClasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.textme.MainActivity
import com.example.textme.MessageChatActivity
import com.example.textme.ModelClasses.Chat
import com.example.textme.ModelClasses.Users
import com.example.textme.R
import com.example.textme.VisitUserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*

class UserAdapter(
    mContext: Context,
    mUsers: List<Users>,
    isChatCheck: Boolean) :RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

    private val mContext: Context
    private val mUsers: List<Users>
    private val isChatCheck: Boolean
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    var lastMsg: String = ""

    init{
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item,viewGroup, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user: Users = mUsers[i]
        holder.userName.text = user!!.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.placeholder).into(holder.profileImageView)

        if (isChatCheck){
            retrieveLastMessage(user.getUID(), holder.lastMessage)

        } else {
            holder.lastMessage.visibility = View.GONE
        }

        if (isChatCheck){
            if (user.getStatus() == "online"){
                holder.online.visibility = View.VISIBLE
                holder.offline.visibility = View.GONE
            }

            else {
                holder.online.visibility = View.GONE
                holder.offline.visibility = View.VISIBLE
            }
        }

        else {
            holder.online.visibility = View.GONE
            holder.offline.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want")
            builder.setItems(options, DialogInterface.OnClickListener{ dialog, position ->
                if (position == 0 ) {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }

                if (position == 1 ){
                    val intent = Intent(mContext, VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }
            })
            builder.show()

            }
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var userName:TextView
        var profileImageView: CircleImageView
        var online:CircleImageView
        var offline:CircleImageView
        var lastMessage:TextView

        init {
            userName = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById(R.id.profile_image)
            online = itemView.findViewById(R.id.image_online)
            offline = itemView.findViewById(R.id.image_offline)
            lastMessage = itemView.findViewById(R.id.message_last)
        }

    }

    private fun retrieveLastMessage(chatUserId: String?, lastMessage: TextView) {
        lastMsg = "defaultMsg"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               for (dataSnapshot in snapshot.children){
                   val chat:Chat? = dataSnapshot.getValue(Chat::class.java)

                   if (firebaseUser!= null && chat != null){
                       if (chat.getReceiver() == firebaseUser!!.uid &&
                           chat.getSender() == chatUserId || chat.getReceiver() == chatUserId &&
                               chat.getSender() == firebaseUser!!.uid){

                           lastMsg = chat.getMessage()!!
                       }
                   }
               }

                when (lastMsg){
                    "defaultMsg" -> lastMessage.text = "no Message"
                    "sent you an image." -> lastMessage.text = "image sent"
                    else -> lastMessage.text = lastMsg
                }

                lastMsg = "defaultMsg"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}