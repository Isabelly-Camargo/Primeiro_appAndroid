package com.cetmc.portfolio

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.DriverManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btTrabalhos = findViewById<Button>(R.id.btTrabalhos)
        val layTrabalhos = findViewById<LinearLayout>(R.id.layTrabalhos)
        val btNovoTrabalho = findViewById<Button>(R.id.btNovoTrabalho)

        btNovoTrabalho.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity3::class.java)
            startActivity(intent)
        }


        btTrabalhos.setOnClickListener {
            Toast.makeText(this, "A Listar ........", Toast.LENGTH_SHORT).show()

            val dbUrl = "jdbc:mariadb://10.0.2.2:3306/utilizadores"
            val dbUser = "root"
            val dbPass = ""

            CoroutineScope(Dispatchers.IO).launch {
                var conn: java.sql.Connection? = null
                var stmt: java.sql.PreparedStatement? = null
                var resultSet: java.sql.ResultSet? = null
                try {
                    Class.forName("org.mariadb.jdbc.Driver")
                    conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)
                    val sql = "SELECT * FROM projetos "
                    stmt = conn.prepareStatement(sql)

                    resultSet = stmt.executeQuery()

                    val listar = mutableListOf<Array<String>>()
                    while (resultSet.next()) {
                        val id = resultSet.getString("id")
                        val titulo = resultSet.getString("titulo")
                        val disc = resultSet.getString("discricao")
                        val data = resultSet.getString("data")
                        listar.add(arrayOf(id, titulo, disc, data))
                    }
                    withContext(Dispatchers.Main) {
                        layTrabalhos.removeAllViews()

                        for (item in listar) {
                            val id = item[0]
                            val titulo = item[1]
                            val disc = item[2]
                            val data = item[3]

                            val itemLayout = LinearLayout(this@MainActivity).apply {
                                orientation = LinearLayout.VERTICAL
                                setPadding(0, 0, 0, 32)
                            }

                            val txtInfo = TextView(this@MainActivity).apply {
                                text = "$id :: Título: $titulo\nDescrição: $disc\nData: $data"
                                textSize = 16f
                                setTextColor(Color.BLACK)
                            }

                            val botoesLayout = LinearLayout(this@MainActivity).apply {
                                orientation = LinearLayout.HORIZONTAL
                            }

                            val btModificar = Button(this@MainActivity).apply {
                                text = "Editar"
                                setOnClickListener {
                                    val intent = Intent(this@MainActivity, MainActivity2::class.java)
                                    intent.putExtra("PROJETO_ID", id.toInt())
                                    startActivity(intent)
                                }
                            }

                            val btApagar = Button(this@MainActivity).apply {
                                text = "Apagar"
                                setOnClickListener {
                                    btDeletar(id.toInt())
                                }
                            }

                            botoesLayout.addView(btModificar)
                            botoesLayout.addView(btApagar)

                            itemLayout.addView(txtInfo)
                            itemLayout.addView(botoesLayout)
                            layTrabalhos.addView(itemLayout)
                        }
                        Toast.makeText(this@MainActivity, "Listado com sucesso", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "ERRO: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    stmt?.close()
                    conn?.close()
                }
            }
        }
    }

    private fun btDeletar(id: Int) {
        val dbUrl = "jdbc:mariadb://10.0.2.2:3306/utilizadores"
        val dbUser = "root"
        val dbPass = ""

        CoroutineScope(Dispatchers.IO).launch {
            var conn: java.sql.Connection? = null
            var stmt: java.sql.PreparedStatement? = null
            try {
                Class.forName("org.mariadb.jdbc.Driver")
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)
                val sql = "DELETE FROM projetos WHERE id = ?"
                stmt = conn.prepareStatement(sql)
                stmt.setInt(1, id)

                stmt.executeUpdate()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Projeto apago com sucesso!", Toast.LENGTH_SHORT).show()
                    findViewById<Button>(R.id.btTrabalhos).performClick()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "ERRO: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                stmt?.close()
                conn?.close()
            }
        }
    }
}