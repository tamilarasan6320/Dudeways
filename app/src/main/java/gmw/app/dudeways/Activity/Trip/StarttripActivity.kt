package gmw.app.dudeways.Activity.Trip

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import gmw.app.dudeways.Activity.Trip.Fragment.FiveFragment
import gmw.app.dudeways.Activity.Trip.Fragment.FourFragment
import gmw.app.dudeways.Activity.Trip.Fragment.SixFragment
import gmw.app.dudeways.Activity.Trip.Fragment.oneFragment
import gmw.app.dudeways.Activity.Trip.Fragment.threeFragment
import gmw.app.dudeways.Activity.Trip.Fragment.twoFragment
import gmw.app.dudeways.R
import gmw.app.dudeways.databinding.ActivityStarttripBinding


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


            }
            is FourFragment -> {

            }
            is FiveFragment -> {


            }
            is SixFragment -> {

            }
        }

    }



    private fun btnBack(it: View?) {
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is oneFragment -> {
                onBackPressed()
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