package gmw.app.paring.Activity.Trip.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import gmw.app.paring.Activity.Trip.StarttripActivity
import gmw.app.paring.R
import gmw.app.paring.databinding.FragmentOneBinding


class oneFragment : Fragment() {

    lateinit var binding: FragmentOneBinding
    lateinit var activity: Activity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOneBinding.inflate(layoutInflater)

        activity = requireActivity()


        (activity as StarttripActivity).binding.tvTitle.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.GONE
        (activity as StarttripActivity).binding.btnBack.visibility = View.GONE



        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvtrip.layoutManager = linearLayoutManager




        tripplan()



        return binding.root


    }


    private fun tripplan() {
        //add data to list
        val list = ArrayList<String>()

        // add drawable image to list not string
        list.add(R.drawable.road_trip_img.toString())
        list.add(R.drawable.adventur_trip_img.toString())
        list.add(R.drawable.explore_city_img.toString())
        list.add(R.drawable.airport_trip_img.toString())


        //set adapter
        val adapter = TripAdapter(activity, list)
        binding.rvtrip.adapter = adapter

    }

    class TripAdapter(activity: Activity, list: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var activity: Activity
        var list: ArrayList<String>

        init {
            this.activity = activity
            this.list = list
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
            return ItemHolder(v)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val itemHolder = holder as ItemHolder

            // R.drawable.road_trip_img.toString() will return the string "2131165300"
            // so we need to convert it to Image resource id
            val image = list[position].toInt()
            Glide.with(activity).load(image).into(itemHolder.ivImage)


            itemHolder.ivImage.setOnClickListener {
                // move to next fragment
                (activity as StarttripActivity).fm.beginTransaction().replace(R.id.frameLayout, twoFragment()).commit()

                // call activity override fun onResume()
                (activity as StarttripActivity).onResume()
            }




        }

        override fun getItemCount(): Int {
            return list.size
        }

        class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var ivImage: ImageView

            init {
                ivImage = itemView.findViewById(R.id.ivImage)
            }
        }

    }

}