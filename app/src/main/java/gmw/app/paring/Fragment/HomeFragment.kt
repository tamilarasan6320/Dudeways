package gmw.app.paring.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import gmw.app.paring.Activity.ProfileActivity
import gmw.app.paring.Activity.ProfileDetailsActivity
import gmw.app.paring.Activity.ProfileViewActivity
import gmw.app.paring.Adapter.HomeCategoryAdapter
import gmw.app.paring.Adapter.HomePtofilesAdapter
import gmw.app.paring.Model.HomeCategory
import gmw.app.paring.Model.HomeProfile
import gmw.app.paring.databinding.FragmentHomeBinding
import java.util.Locale.Category


class HomeFragment : Fragment() {


    lateinit var binding: FragmentHomeBinding
    lateinit var activity: Activity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        activity = requireActivity()


        binding.civProfile.setOnClickListener {

            val intent = Intent(activity, ProfileViewActivity::class.java)
            startActivity(intent)
        }

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvProfileList.layoutManager = linearLayoutManager

        val linearLayoutManager1 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategoryList.layoutManager = linearLayoutManager1

        ProfileList()
        categoryList()


        return binding.root


    }


    private fun ProfileList() {
        val homeProfile = ArrayList<HomeProfile>()
        val cat1 = HomeProfile("1", "Shafeeka", "")




        repeat(5){
            homeProfile.add(cat1)
        }




        val homePtofilesAdapter = HomePtofilesAdapter(requireActivity(),homeProfile)
        binding.rvProfileList.adapter = homePtofilesAdapter
    }



    private fun categoryList() {
        val homeCategory = ArrayList<HomeCategory>()
        val cat1 = HomeCategory("1", "Nearby", "")
        val cat2 = HomeCategory("1", "Latest", "")
        val cat3 = HomeCategory("1", "Trip Date", "")







        homeCategory.add(cat1)
        homeCategory.add(cat2)
        homeCategory.add(cat3)





        val homeCategoryAdapter = HomeCategoryAdapter(requireActivity(), homeCategory)
        binding.rvCategoryList.adapter = homeCategoryAdapter
    }




}