package com.example.pr7

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class SpecializationsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specializations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fab_add = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val db = CollegeDB.getInstance(requireContext())
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_specializations)
        val tv_empty = view.findViewById<TextView>(R.id.tv_empty)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {

            val specializations = db.specializationDao().getAll()
            if (specializations.isEmpty()) {
                tv_empty.visibility = View.VISIBLE
            } else {
                tv_empty.visibility = View.GONE

                val adapter = object : RecyclerView.Adapter<GroupViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
                        val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_specialization, parent, false)
                        return GroupViewHolder(view)
                    }

                    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
                        val specializationss = specializations[position]
                        holder.tvSpecCode.text = specializationss.specializationCode
                        holder.tvSpecName.text = specializationss.specializationName
                        holder.tvStudentCount.text = "Студентов: ${specializationss.count_student}"
                    }

                    override fun getItemCount(): Int = specializations.size
                }

                recycler.adapter = adapter
            }
        }

        fab_add.setOnClickListener {
            val fragment = AddSpecFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentreplace, fragment)
                .addToBackStack("add_spec")
                .commit()
        }
    }

    private class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSpecCode: TextView = itemView.findViewById(R.id.tv_specialization_code)
        val tvSpecName: TextView = itemView.findViewById(R.id.tv_specialization_name)
        val tvStudentCount: TextView = itemView.findViewById(R.id.tv_student_count)
    }
}