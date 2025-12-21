package com.example.pr7

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddStudentFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        val db = CollegeDB
        val daoS = db.getInstance(requireContext()).studentDao()
        val daoU = db.getInstance(requireContext()).userDao()
        val daoG = db.getInstance(requireContext()).groupDao()
        val daoSp = db.getInstance(requireContext()).specializationDao()

        val groupName = view.findViewById<Spinner>(R.id.groupName)

        lifecycleScope.launch {
            val groups = daoG.getAll()
            val groupname = groups.map { it.groupName }

            val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, groupname)
            groupName.adapter = adapter
        }



        val course = view.findViewById<EditText>(R.id.course)
        val numberSpecial = view.findViewById<Spinner>(R.id.numberSpecial)
        lifecycleScope.launch {
            val Specials = daoSp.getAll()
            val Speciale = Specials.map { it.specializationName }

            val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, Speciale)
            numberSpecial.adapter = adapter
        }
        val fullName = view.findViewById<EditText>(R.id.fullName)

        val rb1 = view.findViewById<RadioButton>(R.id.rb1)
        val rb2 = view.findViewById<RadioButton>(R.id.rb2)

        val email = view.findViewById<EditText>(R.id.email)
        val login = view.findViewById<EditText>(R.id.login)
        val password = view.findViewById<EditText>(R.id.password)

        btnAdd.setOnClickListener {
            if(
                course.text.isEmpty() ||
                fullName.text.isEmpty() ||
                email.text.isEmpty() ||
                login.text.isEmpty() ||
                password.text.isEmpty()
                ) {
                Toast.makeText(requireContext(), "Заполните пустые поля", Toast.LENGTH_LONG).show()
            } else {
                if(rb1.isChecked) {
                    lifecycleScope.launch {
                        try {
                            val selectedSpecials = numberSpecial.selectedItem.toString()
                            val Speciale = daoG.getGroupByName(selectedSpecials)
                            var SpecialeID: Int = 0
                            if(Speciale != null)
                                SpecialeID = Speciale.id

                            val selectedGroupName = groupName.selectedItem.toString()
                            val group = daoG.getGroupByName(selectedGroupName)
                            var groupId: Int = 0
                            if(group != null)
                                groupId = group.id

                            val user = User(
                                email = email.text.toString(),
                                login = login.text.toString(),
                                password = password.text.toString(),
                                role = "Студент"
                            )

                            daoU.add(user)

                            val student = Student(
                                groupID = groupId,
                                course = course.text.toString().toInt(),
                                specializationID = SpecialeID,
                                fullName = fullName.text.toString(),
                                isBuget = false,
                                dataID = user.id,
                            )
                            daoS.add(student)

                            parentFragmentManager.popBackStack()
                        }
                        catch (e: Exception) {
                            Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_LONG).show()
                        }
                    }
                } else if(rb2.isChecked) {
                    lifecycleScope.launch {
                        try {

                            val selectedSpecials = numberSpecial.selectedItem.toString()
                            val Speciale = daoG.getGroupByName(selectedSpecials)
                            var SpecialeID: Int = 0
                            if(Speciale != null)
                                SpecialeID = Speciale.id

                            val selectedGroupName = groupName.selectedItem.toString()
                            val group = daoG.getGroupByName(selectedGroupName)
                            var groupId: Int = 0
                            if(group != null)
                                groupId = group.id

                            val user = User(
                                email = email.text.toString(),
                                login = login.text.toString(),
                                password = password.text.toString(),
                                role = "Студент"
                            )

                            daoU.add(user)

                            val student = Student(
                                groupID = groupId,
                                course = course.text.toString().toInt(),
                                specializationID = SpecialeID,
                                fullName = fullName.text.toString(),
                                isBuget = false,
                                dataID = user.id
                            )
                            daoS.add(student)

                            parentFragmentManager.popBackStack()


                        }
                        catch (e: Exception) {
                            Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Не выбран план обучения", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

}