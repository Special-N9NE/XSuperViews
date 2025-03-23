package org.nine.application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TestAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<TestAdapter.SampleViewHolder>() {

    inner class SampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    }

    fun add(value: String) {
        items.add(value)
        notifyItemInserted(items.size - 1)
    }

    fun removeFirst() {
        items.removeAt(0)
        notifyItemRemoved(0)
    }

    fun resetAll() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addAll(values: ArrayList<String>) {
        items.addAll(values)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
        return SampleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.tvTitle.text = items[position]
    }

    override fun getItemCount(): Int = items.size
}
