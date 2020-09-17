package com.example.textme

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var refUsers: DatabaseReference

    private var firebaseUserID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar : Toolbar = findViewById(R.id.toolbar_reg)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@RegisterActivity, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
        auth = FirebaseAuth.getInstance()
        register_btn.setOnClickListener{

            registerUser()
        }
    }

    private fun registerUser() {
        val username: String = username_reg.text.toString()
        val email: String = email_reg.text.toString()
        val password: String = password_reg.text.toString()


        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)
            || TextUtils.isEmpty(password)){
            Toast.makeText(this@RegisterActivity, "All Fields Required.", Toast.LENGTH_SHORT).show()
        } else if (password.length < 6){
            Toast.makeText(this@RegisterActivity, "Password must have 6 characters.", Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    firebaseUserID = auth.currentUser!!.uid
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserID
                    userHashMap["username"] = username
                    userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/textme-be07b.appspot.com/o/Portrait_Placeholder.png?alt=media&token=02290250-60c5-4f36-be8a-77390e389cfc"
                    userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/textme-be07b.appspot.com/o/cover.jpg?alt=media&token=d5ce0078-7fea-434c-a88a-eac93b6ab188"
                    userHashMap["status"] = "offline"
                    userHashMap["search"] = username.toLowerCase()
                    userHashMap["facebook"] = "https://m.facebook.com"
                    userHashMap["instagram"] = "https://m.instagram.com"
                    userHashMap["website"] = "https://www.google.com"

                    refUsers.updateChildren(userHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()

                            }
                        }

                } else {

                    Toast.makeText(this@RegisterActivity,
                        "Error Message:" + task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


}