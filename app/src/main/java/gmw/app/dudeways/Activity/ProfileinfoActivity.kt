package gmw.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import gmw.app.dudeways.R
import gmw.app.dudeways.databinding.ActivityProfileinfoBinding

class ProfileinfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileinfoBinding
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profileinfo)
        binding = ActivityProfileinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        binding.tvTripsPosted.setOnClickListener {
           binding.rlTrips.visibility = View.VISIBLE
            binding.rlConnections.visibility = View.INVISIBLE
            binding.rlInterested.visibility = View.INVISIBLE

        }

        binding.tvConnections.setOnClickListener {
            binding.rlTrips.visibility = View.INVISIBLE
            binding.rlConnections.visibility = View.VISIBLE
            binding.rlInterested.visibility = View.INVISIBLE
        }

        binding.tvInterested.setOnClickListener {
            binding.rlTrips.visibility = View.INVISIBLE
            binding.rlConnections.visibility = View.INVISIBLE
            binding.rlInterested.visibility = View.VISIBLE
        }

    }
}