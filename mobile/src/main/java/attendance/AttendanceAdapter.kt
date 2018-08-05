package attendance

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vince.childcare.R
import kotlinx.android.synthetic.main.atten_child_card_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class AttendanceAdapter() : RecyclerView.Adapter<ViewHolder>() {

  lateinit var items: ArrayList<AttenChild>
  private lateinit var context: Context

  constructor(context: Context, items: ArrayList<AttenChild>) : this() {
    this.context = context
    this.items = items
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(context).inflate(R.layout.atten_child_card_view, parent, false))
  }

  @SuppressLint("ResourceType")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.cv.setOnClickListener(View.OnClickListener {

      holder.cv.isActivated = !holder.cv.isActivated
    })

    holder.tvFirstName.text = items[position].firstName
    holder.tvLastName.text = items[position].lastName

    if (items[position].isActive == "Active") {
      holder.tvIsActive.text = ""
      holder.cv.isEnabled = true
    } else {
      holder.tvIsActive.text = "Inactive"
      holder.cv.isEnabled = false
    }


//    Sat Aug 04 10:15:00 EDT 2018

    if(items[position].checkInTime != "") {
      val dateFormat = SimpleDateFormat(
          "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
      val date = dateFormat.parse(items[position].checkInTime)

      val df = SimpleDateFormat("HH:mm:ss", Locale.US)

      df.format(date)

      holder.tvCheckInTime.text = df.format(date).toString()
      holder.cv.isActivated = true
    }else{
      holder.tvCheckInTime.text = ""
    }
  }

  fun refreshData(children: ArrayList<AttenChild>) {
    this.items = children
    notifyDataSetChanged()
  }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val cv: CardView = view.atten_card_view
  val tvFirstName: TextView = view.tv_first_name
  val tvLastName: TextView = view.tv_last_name
  val tvIsActive: TextView = view.tv_active
  val tvCheckInTime: TextView = view.tv_check_in_time
}
