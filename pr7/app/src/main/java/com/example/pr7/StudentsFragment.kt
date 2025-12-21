package com.example.pr7

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class StudentsFragment : Fragment() {

    private lateinit var adapter: StudentAdapter
    private var allStudents: List<Student> = emptyList()
    private var allGroups: List<Group> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_students, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab_add = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val db = CollegeDB.getInstance(requireContext())
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_students)
        val tv_empty = view.findViewById<TextView>(R.id.tv_empty)
        val linearLayoutGroups = view.findViewById<LinearLayout>(R.id.linearLayoutGroups)
        val ets_search = view.findViewById<EditText>(R.id.ets_search)
        val btn_searchs = view.findViewById<Button>(R.id.btn_searchs)

        val activity = requireActivity()

        when(activity) {
            is SecondStudentActivity -> fab_add.visibility = View.GONE
            is SecondTeacherActivity -> fab_add.visibility = View.GONE
            is SecondActivity -> fab_add.visibility = View.VISIBLE
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = StudentAdapter(emptyList(), emptyList()) { studentId ->
            val isViewOnly = requireActivity() !is SecondActivity
            openRedactStudentFragment(studentId, isViewOnly) // ← ИЗМЕНИЛ ЗДЕСЬ
        }
        recycler.adapter = adapter

        loadAllData(db, tv_empty, linearLayoutGroups)

        btn_searchs.setOnClickListener {
            val searchText = ets_search.text.toString().trim()

            if (searchText.isEmpty()) {
                adapter.updateData(allStudents, allGroups)
                tv_empty.visibility = if (allStudents.isEmpty()) View.VISIBLE else View.GONE
                tv_empty.text = "Нет студентов"
            } else {
                searchStudents(db, searchText, tv_empty)
            }
        }

        fab_add.setOnClickListener {
            openAddStudentFragment()
        }
    }

    private fun loadAllData(db: CollegeDB, tv_empty: TextView, linearLayoutGroups: LinearLayout) {
        lifecycleScope.launch {
            try {
                allStudents = db.studentDao().getAll()
                allGroups = db.groupDao().getAll()

                requireActivity().runOnUiThread {
                    updateGroupsView(linearLayoutGroups, allGroups)
                }

                if (allStudents.isEmpty()) {
                    tv_empty.visibility = View.VISIBLE
                    tv_empty.text = "Нет студентов"
                } else {
                    tv_empty.visibility = View.GONE
                    adapter.updateData(allStudents, allGroups)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                tv_empty.visibility = View.VISIBLE
                tv_empty.text = "Ошибка загрузки"
            }
        }
    }

    private fun updateGroupsView(linearLayoutGroups: LinearLayout, groups: List<Group>) {
        linearLayoutGroups.removeAllViews()

        val allButton = layoutInflater.inflate(R.layout.item_group, linearLayoutGroups, false)
        val allTextView = allButton.findViewById<TextView>(R.id.tv_group_name)
        allTextView.text = "Все"
        allButton.setOnClickListener {
            adapter.updateData(allStudents, allGroups)
        }
        linearLayoutGroups.addView(allButton)

        for (group in groups) {
            val groupView = layoutInflater.inflate(R.layout.item_group, linearLayoutGroups, false)
            val textView = groupView.findViewById<TextView>(R.id.tv_group_name)
            textView.text = group.groupName

            groupView.setOnClickListener {
                filterStudentsByGroup(group.groupName)
            }

            linearLayoutGroups.addView(groupView)
        }
    }

    private fun filterStudentsByGroup(groupName: String) {
        val group = allGroups.find { it.groupName == groupName }

        if (group != null) {
            val filteredStudents = allStudents.filter { it.groupID == group.id }
            adapter.updateData(filteredStudents, allGroups)
        }
    }

    private fun searchStudents(db: CollegeDB, query: String, tv_empty: TextView) {
        lifecycleScope.launch {
            try {
                val searchResults = db.studentDao().searchByName(query)
                val groups = db.groupDao().getAll()

                if (searchResults.isEmpty()) {
                    tv_empty.visibility = View.VISIBLE
                    tv_empty.text = "Студенты не найдены"
                } else {
                    tv_empty.visibility = View.GONE
                }

                adapter.updateData(searchResults, groups)
            } catch (e: Exception) {
                e.printStackTrace()
                tv_empty.visibility = View.VISIBLE
                tv_empty.text = "Ошибка поиска"
            }
        }
    }

    private fun openRedactStudentFragment(studentId: Int?, isViewOnly: Boolean) {
        val fragment = RedactStudentFragment().apply { // ← ИЗМЕНИЛ ЗДЕСЬ
            arguments = Bundle().apply {
                studentId?.let { putInt("student_id", it) }
                putBoolean("is_view_only", isViewOnly)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentreplace, fragment)
            .addToBackStack("student_details")
            .commit()
    }

    private fun openStudentDetailsFragment(studentId: Int, isViewOnly: Boolean) {
        openRedactStudentFragment(studentId, isViewOnly)
    }

    private class StudentAdapter(
        private var students: List<Student>,
        private var groups: List<Group>,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<StudentViewHolder>() {

        fun updateData(newStudents: List<Student>, newGroups: List<Group>) {
            students = newStudents
            groups = newGroups
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student, parent, false)
            return StudentViewHolder(view)
        }

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
            val student = students[position]
            val group = groups.find { it.id == student.groupID }

            holder.tvFullName.text = student.fullName
            holder.tvGroup.text = group?.groupName ?: "Группа не указана"


            holder.itemView.setOnClickListener {
                onItemClick(student.id)
            }
        }

        override fun getItemCount(): Int = students.size
    }

    private class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFullName: TextView = itemView.findViewById(R.id.tv_fullname)
        val tvGroup: TextView = itemView.findViewById(R.id.tv_group)
    }
    private fun openAddStudentFragment() {
        val fragment = AddStudentFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentreplace, fragment)
            .addToBackStack("add_student")
            .commit()
    }
}