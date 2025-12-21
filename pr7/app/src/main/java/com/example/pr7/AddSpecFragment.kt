package com.example.pr7

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddSpecFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_spec, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spec_code = view.findViewById<EditText>(R.id.spec_code)
        val spec_name = view.findViewById<EditText>(R.id.spec_name)
        val db = CollegeDB
        val daoS = db.getInstance(requireContext()).specializationDao()

        val btnAdd = view.findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            if(spec_name.text.isEmpty() || spec_code.text.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните пустые поля", Toast.LENGTH_LONG)
            } else {
                lifecycleScope.launch {
                    try {
                        val spec = Specialization(specializationName = spec_name.text.toString(), specializationCode = spec_code.text.toString())
                        daoS.add(spec)
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