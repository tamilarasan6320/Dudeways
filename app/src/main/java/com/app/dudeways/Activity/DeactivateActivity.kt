package com.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.R
import com.app.dudeways.databinding.ActivityDeactivateBinding

class DeactivateActivity : AppCompatActivity() {


    lateinit var binding: ActivityDeactivateBinding
    lateinit var activity: Activity



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deactivate)

        binding = ActivityDeactivateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        binding.ivBack.setOnClickListener {
           onBackPressed()
        }


//        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)

//        val options = listOf(
//            Option("I have safety concerns with ______"),
//            Option("I got another ________ account"),
//            Option("I will be back soon. This is temporary."),
//            Option("I think am getting too many emails and notifications ."),
//            Option("I have a privacy concern."),
//            Option("I spent too much time on this app"),
//            Option("I don’t understand how it works.")
//
//        )

//        val adapter = OptionsAdapter(options)
//        recyclerView.adapter = adapter

    }
}