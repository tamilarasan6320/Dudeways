package gmw.app.paring.Activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gmw.app.paring.R

class ProfessionAdapter(private val professions: List<String>, private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<ProfessionAdapter.ProfessionViewHolder>() {

    inner class ProfessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val professionTextView: TextView = itemView.findViewById(R.id.professionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profession, parent, false)
        return ProfessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfessionViewHolder, position: Int) {
        val profession = professions[position]
        holder.professionTextView.text = profession
        holder.itemView.setOnClickListener {
            onItemClick(profession)
        }
    }

    override fun getItemCount(): Int {
        return professions.size
    }
}

