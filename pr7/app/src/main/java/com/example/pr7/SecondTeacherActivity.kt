package com.example.pr7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SecondTeacherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_teacher)
        val btn_logout = findViewById<Button>(R.id.btn_logout)
        btn_logout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val search = findViewById<EditText>(R.id.et_search)
        val btn_search = findViewById<Button>(R.id.btn_search)

        val card_students = findViewById<CardView>(R.id.card_students)
        val card_teachers = findViewById<CardView>(R.id.card_teachers)
        val card_groups = findViewById<CardView>(R.id.card_groups)
        val card_specializations = findViewById<CardView>(R.id.card_specializations)

        card_teachers.visibility = CardView.GONE
        card_specializations.visibility = CardView.GONE

        card_students.setOnClickListener {
            val fragment = StudentsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentreplace, fragment)
                .commit()
        }

        card_groups.setOnClickListener {
            val fragment = GroupsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentreplace, fragment)
                .commit()
        }


        lifecycleScope.launch {
            val db = CollegeDB.getInstance(this@SecondTeacherActivity)

            val studentsCount = db.studentDao().getAll().size
            val teachersCount = db.teacherDao().getAll().size

            findViewById<TextView>(R.id.tv_students_count).text = studentsCount.toString()
            findViewById<TextView>(R.id.tv_teachers_count).text = teachersCount.toString()
        }
    }
}