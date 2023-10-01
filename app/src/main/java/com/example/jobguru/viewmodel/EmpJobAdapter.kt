package com.example.jobguru.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jobguru.R
import com.example.jobguru.model.JobModel

class EmpJobAdapter(private var jobList: ArrayList<JobModel>) :
    RecyclerView.Adapter<EmpJobAdapter.ViewHolder>() {

    private lateinit var jListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    // Update setData function to accept filtered list
    fun setData(jobList: List<JobModel>) {
        this.jobList = jobList as ArrayList<JobModel>
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        jListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.emp_job_list_item, parent, false)
        return ViewHolder(itemView, jListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentJob = jobList[position]
        holder.tvJobTitle.text = currentJob.jobTitle
        holder.tvJobRole.text = currentJob.jobRole
        holder.tvJobWorkState.text = currentJob.jobWorkState
        holder.tvJobMinSalary.text = String.format("%.2f", currentJob.jobMinSalary)
        holder.tvJobMaxSalary.text = String.format("%.2f", currentJob.jobMaxSalary)

    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvJobTitle : TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvJobRole : TextView = itemView.findViewById(R.id.tvJobRole)
        val tvJobWorkState : TextView = itemView.findViewById(R.id.tvJobWorkState)
        val tvJobMinSalary : TextView = itemView.findViewById(R.id.tvJobMinSalary)
        val tvJobMaxSalary : TextView = itemView.findViewById(R.id.tvJobMaxSalary)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}