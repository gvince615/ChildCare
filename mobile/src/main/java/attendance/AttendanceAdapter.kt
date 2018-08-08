package attendance

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vince.childcare.R
import core.ACTIVE
import core.CHILD_ATTEN_CARD_TIME_FORMAT
import core.FIRESTORE_DATE_TIME_FORMAT
import core.INACTIVE
import kotlinx.android.synthetic.main.atten_child_card_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class AttendanceAdapter() : RecyclerView.Adapter<ViewHolder>() {

  lateinit var items: ArrayList<AttenChild>
  private lateinit var context: Context
  private lateinit var cardItemListener: CardItemListener

  constructor(context: Context, items: ArrayList<AttenChild>, cardItemListener: CardItemListener) : this() {
    this.context = context
    this.items = items
    this.cardItemListener = cardItemListener
  }

  interface CardItemListener {
    fun onChildCardClicked(childRef: String)
    fun onChildCardLongClicked()
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(context).inflate(R.layout.atten_child_card_view, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.cv.setOnClickListener {

      cardItemListener.onChildCardClicked(holder.tvLastName.text.toString() + "_" + holder.tvFirstName.text.toString())

//      val childRef = holder.tvLastName.text.toString() + "_" + holder.tvFirstName.text.toString()
//      val attendancePresenter = AttendancePresenter()
//      attendancePresenter.setUp(context, childRef, holder.cv.isActivated)
//      attendancePresenter.postAttendance()
    }

    holder.tvFirstName.text = items[position].firstName
    holder.tvLastName.text = items[position].lastName

    if (items[position].isActive == ACTIVE) {
      holder.tvIsActive.text = ""
      holder.cv.isEnabled = true
      if (items[position].checkInTime != "") {
        val dateFormat = SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US)
        val date = dateFormat.parse(items[position].checkInTime)
        val df = SimpleDateFormat(CHILD_ATTEN_CARD_TIME_FORMAT, Locale.US)
        df.format(date)
        holder.tvCheckInTime.text = df.format(date).toString()
        holder.cv.isActivated = true
      } else {
        holder.tvCheckInTime.text = ""
        holder.cv.isActivated = false
      }
    } else {
      holder.tvIsActive.text = INACTIVE
      holder.cv.isEnabled = false
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
