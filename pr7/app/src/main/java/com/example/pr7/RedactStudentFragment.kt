package com.example.pr7

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RedactStudentFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_redact_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etFullname = view.findViewById<EditText>(R.id.et_fullname)
        val spinnerGroup = view.findViewById<Spinner>(R.id.spinner_group)
        val etCourse = view.findViewById<EditText>(R.id.et_course)
        val spinnerSpecial = view.findViewById<Spinner>(R.id.spinner_special)
        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etLogin = view.findViewById<EditText>(R.id.et_login)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val rbBudget = view.findViewById<RadioButton>(R.id.rb_budget)
        val rbNonBudget = view.findViewById<RadioButton>(R.id.rb_nonbudget)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnDelete = view.findViewById<Button>(R.id.btn_Delete)

        val studentId = arguments?.getInt("student_id")

        if (studentId == null) {
            Toast.makeText(requireContext(), "Студент не найден", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val db = CollegeDB.getInstance(requireContext())

        lifecycleScope.launch {
            try {

                val student = db.studentDao().getAll().find { it.id == studentId }

                if (student == null) {
                    Toast.makeText(requireContext(), "Студент не найден", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                    return@launch
                }


                val groups = db.groupDao().getAll()
                val specials = db.specializationDao().getAll()


                val groupNames = groups.map { it.groupName }
                val groupAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, groupNames)
                groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGroup.adapter = groupAdapter

                val specialNames = specials.map { it.specializationName }
                val specialAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, specialNames)
                specialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSpecial.adapter = specialAdapter


                etFullname.setText(student.fullName)
                etCourse.setText(student.course.toString())

                if (student.isBuget) {
                    rbBudget.isChecked = true
                } else {
                    rbNonBudget.isChecked = true
                }


                val group = groups.find { it.id == student.groupID }
                group?.let {
                    val position = groupNames.indexOf(it.groupName)
                    if (position != -1) spinnerGroup.setSelection(position)
                }

                val special = specials.find { it.id == student.specializationID }
                special?.let {
                    val position = specialNames.indexOf(it.specializationName)
                    if (position != -1) spinnerSpecial.setSelection(position)
                }


                if (student.dataID != 0) {
                    val user = db.userDao().getAll().find { it.id == student.dataID }
                    user?.let {
                        etEmail.setText(it.email)
                        etLogin.setText(it.login)
                        etPassword.setText(it.password)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            val fullname = etFullname.text.toString().trim()
            val courseStr = etCourse.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (fullname.isEmpty() || courseStr.isEmpty() || email.isEmpty() || login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!rbBudget.isChecked && !rbNonBudget.isChecked) {
                Toast.makeText(requireContext(), "Выберите план обучения", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {

                    val oldStudent = db.studentDao().getAll().find { it.id == studentId }

                    if (oldStudent == null) {
                        Toast.makeText(requireContext(), "Студент не найден", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    var userId = oldStudent.dataID
                    var user: User? = null

                    // Если у студента уже есть пользователь
                    if (userId != 0) {
                        user = db.userDao().getAll().find { it.id == userId }
                    }


                    if (user == null) {
                        val newUser = User(
                            email = email,
                            login = login,
                            password = password,
                            role = "student"
                        )
                        db.userDao().add(newUser)


                        val allUsers = db.userDao().getAll()
                        user = allUsers.find { it.email == email && it.login == login }
                        userId = user?.id ?: 0
                    } else {

                        val updatedUser = User(
                            id = user.id,
                            email = email,
                            login = login,
                            password = password,
                            role = user.role
                        )
                        db.userDao().update(updatedUser)
                    }


                    val selectedGroupName = spinnerGroup.selectedItem.toString()
                    val group = db.groupDao().getGroupByName(selectedGroupName)
                    val groupId = group?.id ?: oldStudent.groupID

                    val selectedSpecialName = spinnerSpecial.selectedItem.toString()
                    val special = db.specializationDao().getSpecByName(selectedSpecialName)
                    val specialId = special?.id ?: oldStudent.specializationID


                    val updatedStudent = Student(
                        id = oldStudent.id,
                        groupID = groupId,
                        course = courseStr.toInt(),
                        specializationID = specialId,
                        fullName = fullname,
                        isBuget = rbBudget.isChecked,
                        dataID = userId
                    )
                    db.studentDao().update(updatedStudent)

                    Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnDelete.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val student = db.studentDao().getAll().find { it.id == studentId }

                    if (student != null) {
                        db.studentDao().delete(student)


                        if (student.dataID != 0) {
                            val user = db.userDao().getAll().find { it.id == student.dataID }
                            user?.let {
                                db.userDao().delete(it)
                            }
                        }

                        Toast.makeText(requireContext(), "Студент удален", Toast.LENGTH_SHORT).show()
                    }

                    parentFragmentManager.popBackStack()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}