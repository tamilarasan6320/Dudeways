package gmw.app.dudeways.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import gmw.app.dudeways.Activity.ProfileViewActivity
import gmw.app.dudeways.Adapter.HomeCategoryAdapter
import gmw.app.dudeways.Adapter.HomePtofilesAdapter
import gmw.app.dudeways.Model.HomeCategory
import gmw.app.dudeways.Model.HomeProfile
import gmw.app.dudeways.databinding.FragmentHomeBinding
import gmw.app.dudeways.helper.ApiConfig
import gmw.app.dudeways.helper.Constant
import gmw.app.dudeways.helper.Session
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        activity = requireActivity()
        session = Session(activity)

        binding.civProfile.setOnClickListener {
            val intent = Intent(activity, ProfileViewActivity::class.java)
            startActivity(intent)
        }

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvProfileList.layoutManager = linearLayoutManager

        binding.rvCategoryList.layoutManager = GridLayoutManager(activity, 3)

        binding.swipeRefreshLayout.setOnRefreshListener {
            ProfileList()
        }

        ProfileList()
        categoryList()

        return binding.root
    }

    private fun ProfileList() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val g = Gson()
                        val homeProfile = ArrayList<HomeProfile>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            if (jsonObject1 != null) {
                                val profile = g.fromJson(jsonObject1.toString(), HomeProfile::class.java)
                                homeProfile.add(profile)
                            }
                        }

                        val homePtofilesAdapter = HomePtofilesAdapter(requireActivity(), homeProfile)
                        binding.rvProfileList.adapter = homePtofilesAdapter
                    } else {
                        Toast.makeText(
                            activity,
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // Stop the refreshing animation once the network request is complete
            binding.swipeRefreshLayout.isRefreshing = false
        }, activity, Constant.TRIP_LIST, params, true, 1)
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
