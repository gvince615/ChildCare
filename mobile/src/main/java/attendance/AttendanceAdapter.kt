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
    fun editChildClicked(childRef: String, position: Int)
    fun checkInOutBtnClicked(childRef: String, position: Int, view: View)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(context).inflate(R.layout.atten_child_card_view, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    holder.editCardBtn.setOnClickListener {
      cardItemListener.editChildClicked(holder.tvLastName.text.toString() + "_" + holder.tvFirstName.text.toString(), position)
    }

    holder.checkInOutBtn.setOnClickListener {
      cardItemListener.checkInOutBtnClicked(holder.tvLastName.text.toString() + "_" + holder.tvFirstName.text.toString(), position, it)
    }

    holder.tvFirstName.text = items[position].firstName
    holder.tvLastName.text = items[position].lastName

    if (childIsActive(position)) {
      handleChildIsActive(holder, position)
    } else {
      handleChildIsInactive(holder)
    }
  }

  private fun handleChildIsInactive(holder: ViewHolder) {
    holder.tvIsActive.text = INACTIVE
    holder.cv.isEnabled = false
  }

  private fun handleChildIsActive(holder: ViewHolder, position: Int) {
    holder.cv.isEnabled = true
    holder.tvIsActive.text = ""
    if (hasCheckInTime(position)) {
      SimpleDateFormat(CHILD_ATTEN_CARD_TIME_FORMAT, Locale.US).format(
          SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US).parse(items[position].checkInTime))
      holder.tvCheckInTime.text = SimpleDateFormat(CHILD_ATTEN_CARD_TIME_FORMAT, Locale.US).format(
          SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US).parse(items[position].checkInTime)).toString()
      holder.checkInOutBtn.text = context.getText(R.string.check_out)
      holder.cv.isActivated = true
    } else {
      holder.tvCheckInTime.text = ""
      holder.cv.isActivated = false
      holder.checkInOutBtn.text = context.getText(R.string.check_in)
    }
  }

  private fun hasCheckInTime(position: Int) = items[position].checkInTime != "" && items[position].checkInTime != "null"

  private fun childIsActive(position: Int) = items[position].isActive == ACTIVE

  fun refreshData(children: ArrayList<AttenChild>, pos: Int) {
    this.items = children

    if (pos != -1) {
      notifyItemInserted(pos)
    }
    notifyDataSetChanged()
  }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val cv: CardView = view.atten_card_view
  val tvFirstName: TextView = view.tv_first_name
  val tvLastName: TextView = view.tv_last_name
  val tvIsActive: TextView = view.tv_active
  val tvCheckInTime: TextView = view.tv_check_in_time
  val checkInOutBtn: TextView = view.check_in_out_btn
  val editCardBtn: TextView = view.edit_card_btn
}
