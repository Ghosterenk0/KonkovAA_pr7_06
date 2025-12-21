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

class AddTecherFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_techer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        val db = CollegeDB
        val daoT = db.getInstance(requireContext()).teacherDao()
        val daoU = db.getInstance(requireContext()).userDao()
        val daoSp = db.getInstance(requireContext()).specializationDao()


        val fullName = view.findViewById<EditText>(R.id.fullName)
        val specialition = view.findViewById<Spinner>(R.id.specialition)
        val count_hour = view.findViewById<EditText>(R.id.count_hour)

        lifecycleScope.launch {
            val Specials = daoSp.getAll()
            val Speciale = Specials.map { it.specializationName }

            val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, Speciale)
            specialition.adapter = adapter
        }

        val email = view.findViewById<EditText>(R.id.email)
        val login = view.findViewById<EditText>(R.id.login)
        val password = view.findViewById<EditText>(R.id.password)

        btnAdd.setOnClickListener {
            if(
                count_hour.text.isEmpty() ||
                fullName.text.isEmpty() ||
                email.text.isEmpty() ||
                login.text.isEmpty() ||
                password.text.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Заполните пустые поля", Toast.LENGTH_LONG)
            } else {
                lifecycleScope.launch {
                    try {
                        val selectedSpecials = specialition.selectedItem.toString()
                        val Speciale = daoSp.getSpecByName(selectedSpecials)
                        var SpecialeID: Int = 0
                        if(Speciale != null)
                            SpecialeID = Speciale.id

                        val user = User(
                            email = email.text.toString(),
                            login = login.text.toString(),
                            password = password.text.toString(),
                            role = "Преподаватель"
                        )

                        daoU.add(user)

                        val teacher = Teacher(
                            fullName = fullName.text.toString(),
                            totalHours = count_hour.text.toString().toInt(),
                            specializationID = SpecialeID,
                            dataID = user.id,
                        )
                        daoT.add(teacher)

                        parentFragmentManager.popBackStack()
                    }
                    catch (e: Exception) {
                        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_LONG)
                    }
                }
            }

        }
    }
}