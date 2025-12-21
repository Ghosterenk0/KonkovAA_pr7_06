package com.example.pr7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = CollegeDB

        val email = findViewById<EditText>(R.id.email)
        val login = findViewById<EditText>(R.id.login)
        val password = findViewById<EditText>(R.id.password)

        val btn = findViewById<Button>(R.id.btnLogin)

        val rb3 = findViewById<RadioButton>(R.id.rbStudent)
        val rb2 = findViewById<RadioButton>(R.id.rbTeacher)
        val rb1 = findViewById<RadioButton>(R.id.rbCommission)

        btn.setOnClickListener {
            if (email.text.isEmpty() || login.text.isEmpty() || password.text.isEmpty()) {
                Toast.makeText(this, "Заполните пустые поля", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!rb1.isChecked && !rb2.isChecked && !rb3.isChecked) {
                Toast.makeText(this, "Выберите режим входа", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (rb1.isChecked) {
                if (email.text.toString() == "admin" && login.text.toString() == "admin" && password.text.toString() == "admin") {
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                } else {
                    toastValid()
                }
            } else if (rb2.isChecked) {
                lifecycleScope.launch {
                    try {
                        val users = db.getInstance(this@MainActivity).userDao().getAll()
                        val emailText = email.text.toString()
                        val loginText = login.text.toString()
                        val passwordText = password.text.toString()

                        val user = users.find {
                            it.email == emailText &&
                                    it.login == loginText &&
                                    it.password == passwordText &&
                                    it.role == "Преподаватель"
                        }

                        if (user != null) {
                            val intent = Intent(this@MainActivity, SecondTeacherActivity::class.java)
                            startActivity(intent)
                        } else {
                            runOnUiThread { toastValid() }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread { toastValid() }
                    }
                }
            } else if (rb3.isChecked) {
                lifecycleScope.launch {
                    try {
                        val users = db.getInstance(this@MainActivity).userDao().getAll()
                        val emailText = email.text.toString()
                        val loginText = login.text.toString()
                        val passwordText = password.text.toString()

                        val user = users.find {
                            it.email == emailText &&
                                    it.login == loginText &&
                                    it.password == passwordText &&
                                    it.role == "Студент"
                        }

                        if (user != null) {
                            val intent = Intent(this@MainActivity, SecondStudentActivity::class.java)
                            startActivity(intent)
                        } else {
                            runOnUiThread { toastValid() }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread { toastValid() }
                    }
                }
            }
        }
    }

    fun toastValid() {
        Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
    }
}