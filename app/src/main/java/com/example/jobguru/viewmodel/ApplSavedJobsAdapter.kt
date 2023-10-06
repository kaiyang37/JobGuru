package com.example.jobguru.viewmodel

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jobguru.R
import com.example.jobguru.model.SaveJobModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ApplSavedJobsAdapter(private var saveJobList: ArrayList<SaveJobModel>) :
    RecyclerView.Adapter<ApplSavedJobsAdapter.ViewHolder>() {

    private lateinit var jListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    // Update setData function to accept filtered list
    fun setData(saveJobList: List<SaveJobModel>) {
        this.saveJobList.clear() // Clear the existing data
        this.saveJobList.addAll(saveJobList) // Add all elements from the provided list
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        jListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.appl_job_list_item, parent, false)
        return ViewHolder(itemView, jListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentJob = saveJobList[position]
        holder.tvJobTitle.text = currentJob.jobTitle
        holder.tvJobWorkState.text = currentJob.jobWorkState
        holder.tvJobMinSalary.text = String.format("%.2f", currentJob.jobMinSalary)
        holder.tvJobMaxSalary.text = String.format("%.2f", currentJob.jobMaxSalary)
        holder.tvJobCompanyName.text = currentJob.empName

        val localFile = File.createTempFile("tempImage", "jpeg")
        var storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("employersLogo/${currentJob.personInChargeEmail}")

        imageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            holder.tvJobCompanyLogoImage.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return saveJobList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvJobTitle : TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvJobWorkState : TextView = itemView.findViewById(R.id.tvJobWorkState)
        val tvJobMinSalary : TextView = itemView.findViewById(R.id.tvJobMinSalary)
        val tvJobMaxSalary : TextView = itemView.findViewById(R.id.tvJobMaxSalary)
        val tvJobCompanyName : TextView = itemView.findViewById(R.id.tvJobCompanyName)
        val tvJobCompanyLogoImage : ImageView = itemView.findViewById(R.id.tvJobCompanyLogoImage)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}