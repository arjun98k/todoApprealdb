package com.example.realtimetodo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realtimetodo.R
import com.example.realtimetodo.adapter.TaskAdapter
import com.example.realtimetodo.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: MutableList<Task>

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextTask: EditText
    private lateinit var buttonAddTask: Button
    private lateinit var buttonLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        editTextTask = findViewById(R.id.editTextTask)
        buttonAddTask = findViewById(R.id.buttonAddTask)
       buttonLogout = findViewById(R.id.buttonLogout) // optional if you added logout button

        taskList = mutableListOf()
        taskAdapter = TaskAdapter(taskList,
            onStatusChanged = { task -> updateTaskStatus(task) },
            onDelete = { task -> deleteTask(task) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // Get currently logged in user
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Use user ID as Firebase node
        dbRef = FirebaseDatabase.getInstance().getReference("tasks").child(userId)

        buttonAddTask.setOnClickListener {
            val taskText = editTextTask.text.toString().trim()
            if (taskText.isNotEmpty()) {
                addTask(taskText)
                editTextTask.text.clear()
            }
        }

        buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loadTasks()
    }

    private fun addTask(title: String) {
        val taskId = dbRef.push().key ?: return
        val task = Task(taskId, title, false)
        dbRef.child(taskId).setValue(task)
    }

    private fun updateTaskStatus(task: Task) {
        dbRef.child(task.id).child("status").setValue(task.status)
    }

    private fun deleteTask(task: Task) {
        dbRef.child(task.id).removeValue()
    }

    private fun loadTasks() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<Task>()
                for (taskSnap in snapshot.children) {
                    val task = taskSnap.getValue(Task::class.java)
                    task?.let { tempList.add(it) }
                }
                taskList = tempList
                taskAdapter.updateList(taskList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load tasks", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
