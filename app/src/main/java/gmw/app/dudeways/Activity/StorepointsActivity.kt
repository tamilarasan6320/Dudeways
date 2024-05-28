package gmw.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gmw.app.dudeways.R
import gmw.app.dudeways.databinding.ActivityStorepointsBinding

class StorepointsActivity : AppCompatActivity() {


    lateinit var binding: ActivityStorepointsBinding
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storepoints)

        binding = ActivityStorepointsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}