package com.example.pr7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class SecondStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_student)

        val btn_logout = findViewById<Button>(R.id.btn_logout)
        btn_logout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val card_students = findViewById<CardView>(R.id.card_students)
        val card_groups = findViewById<CardView>(R.id.card_groups)
        val card_specializations = findViewById<CardView>(R.id.card_specializations)

        card_students.visibility = CardView.GONE
        card_groups.visibility = CardView.GONE
        card_specializations.visibility = CardView.GONE

        val card_teachers = findViewById<CardView>(R.id.card_teachers)

        card_teachers.setOnClickListener {
            val fragment = TeachersFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentreplace, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}