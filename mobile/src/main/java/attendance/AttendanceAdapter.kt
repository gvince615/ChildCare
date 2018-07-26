package attendance

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vince.childcare.R
import kotlinx.android.synthetic.main.atten_child_card_view.view.*

class AttendanceAdapter(var items: ArrayList<AttenChild>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {


  override fun getItemCount(): Int {
    return items.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(context).inflate(R.layout.atten_child_card_view, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.tvFirstName.text = items[position].firstName
    holder.tvLastName.text = items[position].lastName
  }

  fun refreshData(children: ArrayList<AttenChild>) {
    this.items = children
    this.notifyDataSetChanged()
  }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val tvFirstName: TextView = view.tv_first_name
  val tvLastName: TextView = view.tv_last_name
}
