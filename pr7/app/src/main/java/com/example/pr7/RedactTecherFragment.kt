package com.example.pr7

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RedactTecherFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_redact_techer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullName = view.findViewById<EditText>(R.id.fullName)
        val specialition = view.findViewById<Spinner>(R.id.specialition)
        val count_hour = view.findViewById<EditText>(R.id.count_hour)
        val email = view.findViewById<EditText>(R.id.email)
        val login = view.findViewById<EditText>(R.id.login)
        val password = view.findViewById<EditText>(R.id.password)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete)

        val db = CollegeDB.getInstance(requireContext())
        val daoT = db.teacherDao()
        val daoU = db.userDao()
        val daoSp = db.specializationDao()

        val teacherId = arguments?.getInt("teacher_id")

        lifecycleScope.launch {
            try {
                val specials = daoSp.getAll()
                val specialNames = specials.map { it.specializationName }
                val specialAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, specialNames)
                specialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                specialition.adapter = specialAdapter

                if (teacherId != null) {
                    val teachers = daoT.getAll()
                    val teacher = teachers.find { it.id == teacherId }

                    if (teacher != null) {
                        fullName.setText(teacher.fullName)
                        count_hour.setText(teacher.totalHours.toString())

                        if (teacher.dataID != 0) {
                            val users = daoU.getAll()
                            val user = users.find { it.id == teacher.dataID }

                            user?.let {
                                email.setText(it.email)
                                login.setText(it.login)
                                password.setText(it.password)
                            }
                        }

                        val special = specials.find { it.id == teacher.specializationID }
                        special?.let {
                            val position = specialNames.indexOf(it.specializationName)
                            if (position != -1) specialition.setSelection(position)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnSave.setOnClickListener {
            if (fullName.text.isEmpty() || count_hour.text.isEmpty() ||
                email.text.isEmpty() || login.text.isEmpty() || password.text.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (teacherId == null) {
                Toast.makeText(requireContext(), "Преподаватель не найден", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val selectedSpecial = specialition.selectedItem.toString()
                    val special = daoSp.getSpecByName(selectedSpecial)
                    var specialID = 0
                    if (special != null) specialID = special.id

                    val teachers = daoT.getAll()
                    val oldTeacher = teachers.find { it.id == teacherId }

                    if (oldTeacher != null) {
                        var userId = oldTeacher.dataID
                        var user: User? = null

                        if (userId != 0) {
                            user = daoU.getAll().find { it.id == userId }
                        }

                        if (user == null) {
                            val newUser = User(
                                email = email.text.toString(),
                                login = login.text.toString(),
                                password = password.text.toString(),
                                role = "Преподаватель"
                            )
                            daoU.add(newUser)

                            val allUsers = daoU.getAll()
                            user = allUsers.find { it.email == email.text.toString() && it.login == login.text.toString() }
                            userId = user?.id ?: 0
                        } else {
                            val updatedUser = User(
                                id = user.id,
                                email = email.text.toString(),
                                login = login.text.toString(),
                                password = password.text.toString(),
                                role = user.role
                            )
                            daoU.update(updatedUser)
                        }

                        val updatedTeacher = Teacher(
                            id = oldTeacher.id,
                            fullName = fullName.text.toString(),
                            totalHours = count_hour.text.toString().toInt(),
                            specializationID = specialID,
                            dataID = userId
                        )
                        daoT.update(updatedTeacher)

                        Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Ошибка обновления", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnDelete.setOnClickListener {
            if (teacherId == null) {
                Toast.makeText(requireContext(), "Преподаватель не найден", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val teachers = daoT.getAll()
                    val teacher = teachers.find { it.id == teacherId }

                    if (teacher != null) {
                        daoT.delete(teacher)

                        if (teacher.dataID != 0) {
                            val user = daoU.getAll().find { it.id == teacher.dataID }
                            user?.let {
                                daoU.delete(it)
                            }
                        }

                        Toast.makeText(requireContext(), "Преподаватель удален", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Ошибка удаления", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}