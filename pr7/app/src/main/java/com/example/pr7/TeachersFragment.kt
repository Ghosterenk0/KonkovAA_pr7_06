package com.example.pr7

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TeachersFragment : Fragment() {

    private lateinit var adapter: TeacherAdapter
    private var allTeachers: List<Teacher> = emptyList()
    private var allSpecializations: List<Specialization> = emptyList()

    private var isAdminMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teachers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab_add = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val db = CollegeDB.getInstance(requireContext())
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_teachers)
        val tv_empty = view.findViewById<TextView>(R.id.tv_empty)
        val et_search = view.findViewById<EditText>(R.id.et_search)
        val btn_search = view.findViewById<Button>(R.id.btn_search)

        isAdminMode = requireActivity() is SecondActivity

        if (isAdminMode) {
            fab_add.visibility = View.VISIBLE
        } else {
            fab_add.visibility = View.GONE
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = TeacherAdapter(emptyList(), emptyList(), isAdminMode) { teacherId ->
            openRedactTeacherFragment(teacherId)
        }
        recycler.adapter = adapter

        loadAllTeachers(db, tv_empty)

        btn_search.setOnClickListener {
            val searchText = et_search.text.toString().trim()

            if (searchText.isEmpty()) {
                adapter.updateData(allTeachers, allSpecializations)
                tv_empty.visibility = if (allTeachers.isEmpty()) View.VISIBLE else View.GONE
                tv_empty.text = "Нет преподавателей"
            } else {
                searchTeachers(db, searchText, tv_empty)
            }
        }

        fab_add.setOnClickListener {
            openAddTeacherFragment()
        }
    }

    private fun loadAllTeachers(db: CollegeDB, tv_empty: TextView) {
        lifecycleScope.launch {
            try {
                allTeachers = db.teacherDao().getAll()
                allSpecializations = db.specializationDao().getAll()

                if (allTeachers.isEmpty()) {
                    tv_empty.visibility = View.VISIBLE
                    tv_empty.text = "Нет преподавателей"
                } else {
                    tv_empty.visibility = View.GONE
                    adapter.updateData(allTeachers, allSpecializations)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                tv_empty.visibility = View.VISIBLE
                tv_empty.text = "Ошибка загрузки"
            }
        }
    }

    private fun searchTeachers(db: CollegeDB, query: String, tv_empty: TextView) {
        lifecycleScope.launch {
            try {
                val searchResults = db.teacherDao().searchByName(query)
                val specializations = db.specializationDao().getAll()

                if (searchResults.isEmpty()) {
                    tv_empty.visibility = View.VISIBLE
                    tv_empty.text = "Преподаватели не найдены"
                } else {
                    tv_empty.visibility = View.GONE
                }

                adapter.updateData(searchResults, specializations)
            } catch (e: Exception) {
                e.printStackTrace()
                tv_empty.visibility = View.VISIBLE
                tv_empty.text = "Ошибка поиска"
            }
        }
    }

    private fun openRedactTeacherFragment(teacherId: Int) {
        val fragment = RedactTecherFragment().apply {
            arguments = Bundle().apply {
                putInt("teacher_id", teacherId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentreplace, fragment)
            .addToBackStack("teacher_details")
            .commit()
    }

    private fun openAddTeacherFragment() {
        val fragment = AddTecherFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentreplace, fragment)
            .addToBackStack("add_teacher")
            .commit()
    }

    private class TeacherAdapter(
        private var teachers: List<Teacher>,
        private var specializations: List<Specialization>,
        private val isAdminMode: Boolean,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<TeacherViewHolder>() {

        fun updateData(newTeachers: List<Teacher>, newSpecializations: List<Specialization>) {
            teachers = newTeachers
            specializations = newSpecializations
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_teacher, parent, false)
            return TeacherViewHolder(view)
        }

        override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
            val teacher = teachers[position]
            val spec = specializations.find { it.id == teacher.specializationID }

            holder.tvFullName.text = teacher.fullName
            holder.spec.text = spec?.specializationName ?: "Специализация не указана"

            holder.itemView.setOnClickListener {
                if (isAdminMode) {
                    onItemClick(teacher.id)
                }
            }
        }

        override fun getItemCount(): Int = teachers.size
    }

    private class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFullName: TextView = itemView.findViewById(R.id.tv_fullname)
        val spec: TextView = itemView.findViewById(R.id.spec)
    }
}