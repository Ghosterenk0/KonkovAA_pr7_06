package com.example.pr7

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch

class GroupsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab_add = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val db = CollegeDB.getInstance(requireContext())
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_groups)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val activity = requireActivity()
        when(activity) {
            is SecondStudentActivity -> {
                fab_add.visibility = View.GONE
            }

            is SecondTeacherActivity -> {
                fab_add.visibility = View.GONE
            }
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {

            val groups = db.groupDao().getAll()
            if (groups.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                tvEmpty.visibility = View.GONE

                val adapter = object : RecyclerView.Adapter<GroupViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
                        val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_groups, parent, false)
                        return GroupViewHolder(view)
                    }

                    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
                        val group = groups[position]
                        holder.tvGroupName.text = group.groupName
                        holder.tvStudentCount.text = "Студентов: ${group.count_student}"
                    }

                    override fun getItemCount(): Int = groups.size
                }

                recycler.adapter = adapter
            }
        }

        fab_add.setOnClickListener {
            val fragment = AddGroupFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentreplace, fragment)
                .addToBackStack("add_group")
                .commit()
        }
    }
    private class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGroupName: TextView = itemView.findViewById(R.id.tv_group_name)
        val tvStudentCount: TextView = itemView.findViewById(R.id.tv_student_count)
    }
}