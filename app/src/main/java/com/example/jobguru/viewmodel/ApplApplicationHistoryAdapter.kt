package com.example.jobguru.viewmodel

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.jobguru.R
import com.example.jobguru.model.ApplyModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ApplApplicationHistoryAdapter(
    private var applyList: ArrayList<ApplyModel>,
    private var viewModel: ApplInterviewViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<ApplApplicationHistoryAdapter.ViewHolder>() {

    private lateinit var jListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    // Update setData function to accept filtered list
    fun setData(applyList: List<ApplyModel>) {
        this.applyList.clear() // Clear the existing data
        this.applyList.addAll(applyList) // Add all elements from the provided list
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        jListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.application_history_list_item, parent, false)
        return ViewHolder(itemView, jListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentApply = applyList[position]
        holder.tvJobTitle.text = currentApply.jobTitle
        holder.tvJobCompanyName.text = currentApply.empName
        holder.tvJobWorkState.text = currentApply.jobWorkState

        if (currentApply.appStatus == "Pending") {
            holder.pendingStatus.visibility = View.VISIBLE
            holder.acceptStatus.visibility = View.GONE
            holder.rejectStatus.visibility = View.GONE
        } else if (currentApply.appStatus == "Accepted") {
            holder.acceptStatus.visibility = View.VISIBLE
            holder.pendingStatus.visibility = View.GONE
            holder.rejectStatus.visibility = View.GONE
            holder.tvCalendar.visibility = View.VISIBLE

            holder.tvCalendar.setOnClickListener {
                currentApply.applId?.let { it1 ->
                    currentApply.jobId?.let { it2 ->
                        showInterviewInvitationDialog(
                            holder,
                            it1, it2
                        )
                    }
                }
            }
        } else {
            holder.rejectStatus.visibility = View.VISIBLE
            holder.pendingStatus.visibility = View.GONE
            holder.acceptStatus.visibility = View.GONE
        }

        val localFile = File.createTempFile("tempImage", "jpeg")
        var storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("employersLogo/${currentApply.jobCompanyEmail}")

        imageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            holder.tvJobCompanyLogoImage.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return applyList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val tvJobCompanyLogoImage: ImageView = itemView.findViewById(R.id.tvJobCompanyLogoImage)
        val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvJobCompanyName: TextView = itemView.findViewById(R.id.tvJobCompanyName)
        val tvJobWorkState: TextView = itemView.findViewById(R.id.tvJobWorkState)
        val pendingStatus: LinearLayout = itemView.findViewById(R.id.pendingStatus)
        val acceptStatus: LinearLayout = itemView.findViewById(R.id.acceptStatus)
        val rejectStatus: LinearLayout = itemView.findViewById(R.id.rejectStatus)
        val tvCalendar: ImageView = itemView.findViewById(R.id.tvCalendar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    private fun showInterviewInvitationDialog(holder: ViewHolder, applId: String, jobId: String) {

        val interviewInvitationDialog = AlertDialog.Builder(holder.itemView.context)
        val inflater = LayoutInflater.from(holder.itemView.context)
        val interviewInvitationDialogView =
            inflater.inflate(R.layout.appl_interview_invitation_dialog, null)
        val rejectBtn = interviewInvitationDialogView.findViewById<LinearLayout>(R.id.rejectBtn)
        val acceptBtn = interviewInvitationDialogView.findViewById<LinearLayout>(R.id.acceptBtn)
        val cancelBtn = interviewInvitationDialogView.findViewById<TextView>(R.id.cancelBtn)

        viewModel.getInterviewData(applId, jobId)

        viewModel.intStatus.observe(lifecycleOwner){intStatus ->
            if(intStatus != "Pending"){
                interviewInvitationDialogView.findViewById<LinearLayout>(R.id.intStatus).visibility = View.VISIBLE
                interviewInvitationDialogView.findViewById<TextView>(R.id.applyJobIntStatus).text = intStatus
                interviewInvitationDialogView.findViewById<LinearLayout>(R.id.actionBtn).visibility = View.GONE
                interviewInvitationDialogView.findViewById<TextView>(R.id.cancelBtn).text = "Close"
            }
        }

        viewModel.companyName.observe(lifecycleOwner) { companyName ->
            interviewInvitationDialogView.findViewById<TextView>(R.id.applyJobCompanyName).text = companyName
        }

        viewModel.jobTitle.observe(lifecycleOwner) { jobTitle ->
            interviewInvitationDialogView.findViewById<TextView>(R.id.applyJobTitle).text = jobTitle
        }

        viewModel.intDate.observe(lifecycleOwner) { intDate ->
            interviewInvitationDialogView.findViewById<TextView>(R.id.applyJobIntDate).text = intDate
        }

        viewModel.intTime.observe(lifecycleOwner) { intTime ->
            interviewInvitationDialogView.findViewById<TextView>(R.id.applyJobIntTime).text = intTime
        }

        viewModel.intPlatform.observe(lifecycleOwner) { intPlatform ->
            interviewInvitationDialogView.findViewById<TextView>(R.id.applyJobIntPlatform).text = intPlatform
        }

        viewModel.interviewer.observe(lifecycleOwner) { interviewer ->
            interviewInvitationDialogView.findViewById<TextView>(R.id.applyJobInterviewer).text = interviewer
        }

        interviewInvitationDialog.setView(interviewInvitationDialogView)

        val alertDialog = interviewInvitationDialog.create()
        alertDialog.show()

        rejectBtn.setOnClickListener {
            showRejectInterviewInvitationDialog(holder)
            alertDialog.dismiss()
        }

        acceptBtn.setOnClickListener {
            viewModel.intId.observe(lifecycleOwner){intId->
                viewModel.updateInterviewStatus(intId,  onSuccess = {
                    interviewInvitationDialogView.findViewById<LinearLayout>(R.id.intStatus).visibility = View.VISIBLE
                    interviewInvitationDialogView.findViewById<TextView>(R.id.applyJobIntStatus).text = "Accepted"
                    interviewInvitationDialogView.findViewById<LinearLayout>(R.id.actionBtn).visibility = View.GONE
                    interviewInvitationDialogView.findViewById<TextView>(R.id.cancelBtn).text = "Close"
                },
                    onError = {})
            }
        }

        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun showRejectInterviewInvitationDialog(holder: ViewHolder) {

        val interviewInvitationDialog = AlertDialog.Builder(holder.itemView.context)
        val inflater = LayoutInflater.from(holder.itemView.context)
        val interviewInvitationDialogView =
            inflater.inflate(R.layout.appl_reject_interview_invitation_dialog, null)
        val submitBtn = interviewInvitationDialogView.findViewById<Button>(R.id.submitBtn)
        val cancelBtn = interviewInvitationDialogView.findViewById<TextView>(R.id.cancelBtn)

        interviewInvitationDialog.setView(interviewInvitationDialogView)

        val alertDialog = interviewInvitationDialog.create()
        alertDialog.show()

        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        submitBtn.setOnClickListener {
            val textInputLayout = interviewInvitationDialogView.findViewById<TextInputLayout>(R.id.textInputLayoutRejectReason)
            val rejectReasonEditText = textInputLayout.findViewById<EditText>(R.id.rejectReasonTextBox)
            val reason = rejectReasonEditText.text.toString()

            if(viewModel.validateDate(reason)) {
                viewModel.intId.observe(lifecycleOwner) { intId ->
                    viewModel.updateRejectInterviewStatus(intId, reason, onSuccess = {
                        alertDialog.dismiss()
                        val successMessage = "Interview has been rejected"
                        Toast.makeText(holder.itemView.context, successMessage, Toast.LENGTH_SHORT).show()
                    },
                        onError = {})
                }
            }
        }

        viewModel.reasonError.observe(lifecycleOwner) { errorMessage ->
            interviewInvitationDialogView.findViewById<TextInputLayout>(R.id.textInputLayoutRejectReason).error = errorMessage
        }

        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}