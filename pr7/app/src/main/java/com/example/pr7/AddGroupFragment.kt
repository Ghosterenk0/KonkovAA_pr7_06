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

class AddGroupFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = CollegeDB
        val daoG = db.getInstance(requireContext()).groupDao()

        val btnAdd = view.findViewById<Button>(R.id.btnAdd)
        val groups_name = view.findViewById<EditText>(R.id.groups_name)

        btnAdd.setOnClickListener {
            if(groups_name.text.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните пустые поля", Toast.LENGTH_LONG)
            } else {
                lifecycleScope.launch {
                    try {
                        val group = Group(groupName = groups_name.text.toString())
                        daoG.add(group)
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