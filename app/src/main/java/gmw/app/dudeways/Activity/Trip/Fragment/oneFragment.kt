package gmw.app.dudeways.Activity.Trip.Fragment

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
import gmw.app.dudeways.Activity.Trip.StarttripActivity
import gmw.app.dudeways.R
import gmw.app.dudeways.databinding.FragmentOneBinding

class oneFragment : Fragment() {

    lateinit var binding: FragmentOneBinding
    lateinit var activity: Activity
    private var selectedItemPosition = RecyclerView.NO_POSITION

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOneBinding.inflate(layoutInflater)

        activity = requireActivity()

        (activity as StarttripActivity).binding.tvTitle.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvtrip.layoutManager = linearLayoutManager

        tripplan()

        return binding.root
    }

    private fun tripplan() {
        val list = ArrayList<String>()
        list.add(R.drawable.road_trip_img.toString())
        list.add(R.drawable.adventur_trip_img.toString())
        list.add(R.drawable.trip_3.toString())
        list.add(R.drawable.airport_trip_img.toString())

        val adapter = TripAdapter(activity, list)
        binding.rvtrip.adapter = adapter
    }

    inner class TripAdapter(private val activity: Activity, private val list: ArrayList<String>) : RecyclerView.Adapter<TripAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
            return ItemHolder(v)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val itemHolder = holder
            val image = list[position].toInt()
            Glide.with(activity).load(image).into(itemHolder.ivImage)

            if (position == selectedItemPosition) {
                itemHolder.ivCheck.visibility = View.VISIBLE
            } else {
                itemHolder.ivCheck.visibility = View.GONE
            }

            itemHolder.itemView.setOnClickListener {
                val previousSelectedPosition = selectedItemPosition
                selectedItemPosition = holder.adapterPosition
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(holder.adapterPosition)
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var ivImage: ImageView = itemView.findViewById(R.id.ivImage)
            var ivCheck: ImageView = itemView.findViewById(R.id.ivCheck)
        }
    }
}
