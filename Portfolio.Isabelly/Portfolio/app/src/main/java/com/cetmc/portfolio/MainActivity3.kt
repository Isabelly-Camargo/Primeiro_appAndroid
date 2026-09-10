package com.cetmc.portfolio

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

class MainActivity3 : AppCompatActivity() {
    private val dbUrl = "jdbc:mariadb://10.0.2.2:3306/utilizadores"
    private val dbUser = "root"
    private val dbPass = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btInserir = findViewById<Button>(R.id.btInserir)
        val txtTitulo = findViewById<EditText>(R.id.editTitulo)
        val txtDiscricao = findViewById<EditText>(R.id.editDiscricao)
        val txtData = findViewById<EditText>(R.id.editDate)
        val idProjeto = intent.getIntExtra("PROJETO_ID", -1)


        btInserir.setOnClickListener {
            Toast.makeText(this, "A inserir na base de dados ........", Toast.LENGTH_SHORT).show()

            val dbUrl = "jdbc:mariadb://10.0.2.2:3306/utilizadores"
            val dbUser = "root"
            val dbPass = ""

            CoroutineScope(Dispatchers.IO).launch {
                var conn: java.sql.Connection? = null
                var stmt: java.sql.PreparedStatement? = null
                try{
                    Class.forName("org.mariadb.jdbc.Driver")
                    conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)
                    val sql = "INSERT INTO projetos(titulo, discricao, data) VALUES(? , ? , ?)"
                    stmt = conn.prepareStatement(sql)

                    stmt.setString(1, txtTitulo.text.toString())
                    stmt.setString(2, txtDiscricao.text.toString())
                    stmt.setString(3, txtData.text.toString())

                    stmt.executeUpdate()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity3, "Inserido com sucesso", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity3, "ERRO: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    stmt?.close()
                    conn?.close()
                }
            }
        }




    }
}