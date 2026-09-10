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

class MainActivity2 : AppCompatActivity() {
    private val dbUrl = "jdbc:mariadb://10.0.2.2:3306/utilizadores"
    private val dbUser = "root"
    private val dbPass = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        val idProjeto = intent.getIntExtra("PROJETO_ID", -1)
        val txtTitulo = findViewById<EditText>(R.id.editTitulo)
        val txtDiscricao = findViewById<EditText>(R.id.editDiscricao)
        val txtData = findViewById<EditText>(R.id.editDate)
        val txtID = findViewById<EditText>(R.id.editID)
        val btAtualizar = findViewById<Button>(R.id.btAtualizar)


         if (idProjeto != -1) {
             txtID.setText(idProjeto.toString())
             txtID.isEnabled = false
             carregarDadosProjeto(idProjeto, txtTitulo, txtDiscricao, txtData)
         }

        btAtualizar.setOnClickListener {
            Toast.makeText(this, "A atualizar na base de dados ........", Toast.LENGTH_SHORT).show()

            val dbUrl = "jdbc:mariadb://10.0.2.2:3306/utilizadores"
            val dbUser = "root"
            val dbPass = ""

            CoroutineScope(Dispatchers.IO).launch {
                var conn: java.sql.Connection? = null
                var stmt: java.sql.PreparedStatement? = null
                try{
                    Class.forName("org.mariadb.jdbc.Driver")
                    conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)
                    val sql = "UPDATE projetos SET titulo = ?, discricao = ?, data = ? WHERE id = ?"
                    stmt = conn.prepareStatement(sql)

                    stmt.setString(1, txtTitulo.text.toString())
                    stmt.setString(2, txtDiscricao.text.toString())
                    stmt.setString(3, txtData.text.toString())
                    stmt.setInt(4, txtID.text.toString().toInt())

                    stmt.executeUpdate()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity2, "Atualizado com sucesso", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity2, "ERRO: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    stmt?.close()
                    conn?.close()
                }
            }
        }



    }
    private fun carregarDadosProjeto(id: Int, txtTitulo: EditText, txtDiscricao: EditText, txtData: EditText) {
        CoroutineScope(Dispatchers.IO).launch {
            var conn: java.sql.Connection? = null
            var stmt: java.sql.PreparedStatement? = null
            var resultSet: java.sql.ResultSet? = null
            try {
                Class.forName("org.mariadb.jdbc.Driver")
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)
                val sql = "SELECT * FROM projetos WHERE id = ?"
                stmt = conn.prepareStatement(sql)
                stmt.setInt(1, id)

                resultSet = stmt.executeQuery()

                if (resultSet.next()) {
                    val titulo = resultSet.getString("titulo")
                    val disc = resultSet.getString("discricao")
                    val data = resultSet.getString("data")

                    withContext(Dispatchers.Main) {
                        txtTitulo.setText(titulo)
                        txtDiscricao.setText(disc)
                        txtData.setText(data)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity2, "ERRO ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                resultSet?.close()
                stmt?.close()
                conn?.close()
            }
        }
    }
}