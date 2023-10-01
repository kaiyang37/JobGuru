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
    private var context: Context,
    private var applList: ArrayList<ApplicantModel>
) :
    RecyclerView.Adapter<EmpApplicantAdapter.ViewHolder>() {
    private lateinit var aListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    // Update setData function to accept filtered list
    fun setData(applList: List<ApplicantModel>) {
        this.applList = applList as ArrayList<ApplicantModel>
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
        val firstName = currentAppl.applFirstName
        val lastName = currentAppl.applLastName
        val fullName = "$firstName $lastName"

        holder.tvApplName.text = fullName
        holder.tvApplEducationLvl.text = currentAppl.applEducationLevel
        holder.tvApplExpectedSalary.text =
            String.format("%.2f", currentAppl.applMinimumMonthlySalary)
        holder.tvApplLiveIn.text = currentAppl.applLiveIn
        getJobId(holder, currentAppl.applId)
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

    private fun getJobId(holder: ViewHolder, applId: String?) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val delimitedJobIds = sharedPreferences.getString("myJobIdList", "") ?: ""

        val applyRef = FirebaseDatabase.getInstance().getReference("Apply")
        val jobIdList = delimitedJobIds.split(",")

        for (jobId in jobIdList) {
            val query = applyRef.orderByChild("jobId").equalTo(jobId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (applySnap in dataSnapshot.children) {
                            val applyData = applySnap.getValue(ApplyModel::class.java)

                            if (applyData != null && applyData.applId.equals(applId) && applyData.appStatus == "Pending") {
                                //holder.tvJobTitle.text = jobId
                                getJobTitle(holder, jobId)
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e("Firebase", "Error: ${databaseError.message}")
                }

            })
        }
    }

    private fun getJobTitle(holder: ViewHolder, jobId: String) {
        val jobRef = FirebaseDatabase.getInstance().getReference("Jobs")
        val jobQuery = jobRef.orderByChild("jobId").equalTo(jobId)

        jobQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming jobTitle is stored as a child under the jobId node
                    val jobTitle = dataSnapshot.child(jobId).child("jobTitle").value.toString()
                    holder.tvJobTitle.text = jobTitle
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("Firebase", "Error: ${databaseError.message}")
            }
        })
    }
}

