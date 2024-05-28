package gmw.app.dudeways.Activity

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import gmw.app.dudeways.Fragment.ExploreFragment
import gmw.app.dudeways.Fragment.HomeFragment
import gmw.app.dudeways.Fragment.InterestFragment
import gmw.app.dudeways.Fragment.LikesFragment
import gmw.app.dudeways.Fragment.MessagesFragment
import gmw.app.dudeways.Fragment.NotificationFragment
import gmw.app.dudeways.Fragment.TripFragment
import gmw.app.dudeways.Fragment.ViewFragment
import gmw.app.dudeways.R
import gmw.app.dudeways.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    lateinit var binding: ActivityHomeBinding
    lateinit var activity: Activity

    private lateinit var fm: FragmentManager

    private var bottomNavigationView: BottomNavigationView? = null

    private var tripFragment = TripFragment()
    private var exploreFragment = ExploreFragment()
    private var likesFragment = LikesFragment()
    private var viewFragment = ViewFragment()
    private var messagesFragment = MessagesFragment()
    private var homeFragment = HomeFragment()
    private var notification = NotificationFragment()
    private var interestFragment = InterestFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        activity = this
        setContentView(binding.root)


        tripFragment = TripFragment()
        exploreFragment = ExploreFragment()
        likesFragment = LikesFragment()
        viewFragment = ViewFragment()
        messagesFragment = MessagesFragment()
        homeFragment = HomeFragment()
        notification = NotificationFragment()
        interestFragment = InterestFragment()


        fm = supportFragmentManager

        fm.beginTransaction().replace(R.id.fragment_container, tripFragment).commit()

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView!!.setOnItemSelectedListener(this)

        fm.beginTransaction().replace(R.id.fragment_container, tripFragment).commit()
        bottomNavigationView!!.selectedItemId = R.id.navHome


    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val transaction: FragmentTransaction = fm.beginTransaction()


        when (item.itemId) {
            R.id.navHome -> {
                transaction.replace(R.id.fragment_container, homeFragment)
            }

            R.id.navExplore -> {
                transaction.replace(R.id.fragment_container, tripFragment)
            }

            R.id.navIntersts -> {
                transaction.replace(R.id.fragment_container, interestFragment)
            }

            R.id.navMessages -> {
                transaction.replace(R.id.fragment_container, messagesFragment)
            }

            R.id.navNotification -> {
                transaction.replace(R.id.fragment_container, notification)
            }

        }



        transaction.commit()
        return true
    }

}