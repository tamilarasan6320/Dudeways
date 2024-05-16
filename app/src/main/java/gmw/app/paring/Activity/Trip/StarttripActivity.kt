package gmw.app.paring.Activity.Trip

import android.R.attr
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import gmw.app.paring.Activity.Trip.Fragment.FiveFragment
import gmw.app.paring.Activity.Trip.Fragment.FourFragment
import gmw.app.paring.Activity.Trip.Fragment.SixFragment
import gmw.app.paring.Activity.Trip.Fragment.oneFragment
import gmw.app.paring.Activity.Trip.Fragment.threeFragment
import gmw.app.paring.Activity.Trip.Fragment.twoFragment
import gmw.app.paring.R
import gmw.app.paring.databinding.ActivityStarttripBinding


class StarttripActivity : AppCompatActivity() {

    lateinit var binding:ActivityStarttripBinding
    lateinit var activity: Activity
    lateinit var fm: FragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starttrip)

        binding = ActivityStarttripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this


        fm = supportFragmentManager
        val fragment_one = oneFragment()


        fm.beginTransaction().replace(R.id.frameLayout, fragment_one).commit()



        binding.btnNext.setOnClickListener {
            btnNext(it)
        }

        binding.btnBack.setOnClickListener{
            btnBack(it)
        }




    }

    // resume

    public override fun onResume() {
        super.onResume()
        // check the current fragment
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is oneFragment -> {


            }
            is twoFragment -> {


            }
            is threeFragment -> {
                Toast.makeText(this, "threeFragment", Toast.LENGTH_SHORT).show()

            }
            is FourFragment -> {
                Toast.makeText(this, "FourFragment", Toast.LENGTH_SHORT).show()
            }
            is FiveFragment -> {
                Toast.makeText(this, "FiveFragment", Toast.LENGTH_SHORT).show()

            }
            is SixFragment -> {
                Toast.makeText(this, "SixFragment", Toast.LENGTH_SHORT).show()
            }
        }

    }



    private fun btnBack(it: View?) {
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is oneFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FourFragment()).commit()
                onResume()
            }
            is twoFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, oneFragment()).commit()
                onResume()


            }
            is threeFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, twoFragment()).commit()
                onResume()

            }
            is FourFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, threeFragment()).commit()
                onResume()

            }
            is FiveFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FourFragment()).commit()
                onResume()

            }
            is SixFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FiveFragment()).commit()
                onResume()

            }
        }
    }

    fun btnNext(view: View) {
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is oneFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, twoFragment()).commit()
                onResume()

            }
            is twoFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, threeFragment()).commit()
                onResume()

            }
            is threeFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FourFragment()).commit()
                onResume()

            }
            is FourFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FiveFragment()).commit()
                onResume()

            }
            is FiveFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, SixFragment()).commit()
                onResume()

            }
        } }









}