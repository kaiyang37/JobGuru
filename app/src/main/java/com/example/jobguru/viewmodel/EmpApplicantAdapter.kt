package com.example.jobguru.viewmodel

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jobguru.R
import com.example.jobguru.model.ApplicantModel
import com.example.jobguru.model.ApplyModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpApplicantAdapter(
    private var applList: ArrayList<ApplyModel>
) :
    RecyclerView.Adapter<EmpApplicantAdapter.ViewHolder>() {
    private lateinit var aListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    // Update setData function to accept filtered list
    fun setData(applList: List<ApplyModel>) {
        this.applList = applList as ArrayList<ApplyModel>
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        aListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.emp_applicant_list_item, parent, false)
        return ViewHolder(itemView, aListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAppl = applList[position]
        holder.tvJobTitle.text = currentAppl.jobTitle
        holder.tvApplName.text = currentAppl.applName
        holder.tvApplEducationLvl.text = currentAppl.applEducationLevel
        holder.tvApplExpectedSalary.text =
            String.format("%.2f", currentAppl.applMinimumMonthlySalary)
        holder.tvApplLiveIn.text = currentAppl.applLiveIn
    }

    override fun getItemCount(): Int {
        return applList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvApplName: TextView = itemView.findViewById(R.id.tvApplName)
        val tvApplEducationLvl: TextView = itemView.findViewById(R.id.tvApplEducationLvl)
        val tvApplExpectedSalary: TextView = itemView.findViewById(R.id.tvApplExpectedSalary)
        val tvApplLiveIn: TextView = itemView.findViewById(R.id.tvApplLiveIn)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}

