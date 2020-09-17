package com.example.textme

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private val pd: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar : Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@LoginActivity, StartActivity::class.java)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()


        login_btn.setOnClickListener {
            val pd = ProgressDialog(this@LoginActivity)
            pd.setMessage("please wait...")
            pd.show()
            val email: String = email_log.text.toString()
            val password: String = password_log.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
            ) {
                Toast.makeText(this@LoginActivity, "All fields are required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            val reference =
                                FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(auth.currentUser!!.uid)

                            reference.addValueEventListener(object: ValueEventListener {
                                override fun onDataChange(p0: DataSnapshot) {
                                    pd.dismiss()
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }

                                override fun onCancelled(p0: DatabaseError) {
                                    pd.dismiss()
                                }
                            })
                        } else {
                        pd!!.dismiss()
                        Toast.makeText(this@LoginActivity,
                            "Error Message:" + task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }}
                    }
            }
        }

}