package com.cetmc.portfolio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.DriverManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtUser = findViewById<EditText>(R.id.editUser)
        val txtEmail = findViewById<EditText>(R.id.editEmail)
        val btLogIn = findViewById<Button>(R.id.btLogin)
        btLogIn.setOnClickListener {
            val dbUrl = "jdbc:mariadb://10.0.2.2:3306/utilizadores"
            val dbUser = "root"
            val dbPass = ""

            CoroutineScope(Dispatchers.IO).launch {
                var conn: java.sql.Connection? = null
                var stmt: java.sql.PreparedStatement? = null
                try{
                    Class.forName("org.mariadb.jdbc.Driver")
                    conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)
                    val sql = "INSERT INTO login(nome, email) VALUES(? , ? )"
                    stmt = conn.prepareStatement(sql)

                    stmt.setString(1, txtUser.text.toString())
                    stmt.setString(2, txtEmail.text.toString())


                    stmt.executeUpdate()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LogIn, "Inserido com sucesso", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LogIn, MainActivity::class.java)
                        startActivity(intent)

                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LogIn, "ERRO: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    stmt?.close()
                    conn?.close()
                }
            }
        }
    }
}