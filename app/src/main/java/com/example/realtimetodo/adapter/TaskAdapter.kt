package com.example.realtimetodo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.realtimetodo.R
import com.example.realtimetodo.model.Task

class TaskAdapter(
    private var taskList: List<Task>,
    private val onStatusChanged: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.textViewTaskTitle)
        val taskStatus: CheckBox = itemView.findViewById(R.id.checkBoxTaskStatus)
        val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.taskTitle.text = task.title
        holder.taskStatus.isChecked = task.status

        holder.taskStatus.setOnCheckedChangeListener { _, isChecked ->
            task.status = isChecked
            onStatusChanged(task)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(task)
        }
    }

    fun updateList(newList: List<Task>) {
        taskList = newList
        notifyDataSetChanged()
    }
}
