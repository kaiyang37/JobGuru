package com.example.jobguru.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jobguru.R
import com.example.jobguru.model.InterviewModel

class EmpInterviewAdapter(private var interviewList: ArrayList<InterviewModel>) :
    RecyclerView.Adapter<EmpInterviewAdapter.ViewHolder>() {

    private lateinit var iListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    // Update setData function to accept filtered list
    fun setData(interviewList: List<InterviewModel>) {
        this.interviewList = interviewList as ArrayList<InterviewModel>
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        iListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.emp_interview_list_item, parent, false)
        return ViewHolder(itemView, iListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentInterview = interviewList[position]
        holder.tvJobTitle.text = currentInterview.jobTitle
        holder.tvApplName.text = currentInterview.applName
        holder.tvInterviewDate.text = currentInterview.intvwDate
        holder.tvInterviewTime.text = currentInterview.intvwTime
        holder.tvInterviewStatus.text = currentInterview.intvwStatus

    }

    override fun getItemCount(): Int {
        return interviewList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvJobTitle : TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvApplName:  TextView = itemView.findViewById(R.id.tvApplName)
        val tvInterviewDate : TextView = itemView.findViewById(R.id.tvInterviewDate)
        val tvInterviewTime : TextView = itemView.findViewById(R.id.tvInterviewTime)
        val tvInterviewStatus : TextView = itemView.findViewById(R.id.tvInterviewStatus)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}