package com.example.jobguru.viewmodel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CombinedSpinnerAdapter(
    private val context: Context,
    private val yearsArray: Array<String>,
    private val monthsArray: Array<String>
) : BaseAdapter() {

    override fun getCount(): Int {
        // Return the total number of items (months * years)
        return yearsArray.size * monthsArray.size
    }

    override fun getItem(position: Int): String {
        // Calculate the month and year for the given position
        val yearIndex = position % yearsArray.size
        val monthIndex = position / yearsArray.size
        val year = yearsArray[yearIndex]
        val month = monthsArray[monthIndex]

        // Combine and return the month and year
        return "$month $year"
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false) as TextView
        itemView.text = getItem(position)
        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}