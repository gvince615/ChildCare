package attendance

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vince.childcare.R
import core.*
import de.hdodenhof.circleimageview.CircleImageView
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
    fun childClicked(childRef: String, position: Int)
    fun checkInOutBtnClicked(childRef: String, position: Int)
    fun activateChild(toString: String)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(context).inflate(R.layout.atten_child_card_view, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    holder.cv.setOnClickListener {
      cardItemListener.childClicked(holder.tvChildId.text.toString(), position)
    }

    holder.checkInOutBtn.setOnClickListener {
      if (!holder.cv.isEnabled) {
        cardItemListener.activateChild(holder.tvChildId.text.toString())
      } else {
        cardItemListener.checkInOutBtnClicked(holder.tvChildId.text.toString(), position)
      }
    }

    if (items[position].childImageUri != "" || items[position].childImageUri != "null") {
      DownloadImageTask(holder.ivChildImage).execute(items[position].childImageUri)
    }
    holder.tvChildId.text = items[position].childId
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
    holder.checkInOutBtn.text = context.getText(R.string.activate)
    holder.cv.isEnabled = false
    holder.ivChildImage.borderColor = context.getColor(R.color.darker_grey)
  }

  private fun handleChildIsActive(holder: ViewHolder, position: Int) {
    holder.cv.isEnabled = true
    holder.tvIsActive.text = getAgeFromBirthDate(position)
    if (hasCheckInTime(position)) {
      SimpleDateFormat(CHILD_ATTEN_CARD_TIME_FORMAT, Locale.US).format(
          SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US).parse(items[position].checkInTime))
      holder.tvCheckInTime.text = SimpleDateFormat(CHILD_ATTEN_CARD_TIME_FORMAT, Locale.US).format(
          SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US).parse(items[position].checkInTime)).toString()
      holder.checkInOutBtn.text = context.getText(R.string.check_out)
      holder.cv.isActivated = true
      holder.ivChildImage.borderColor = context.getColor(R.color.colorGreen)
    } else {
      holder.tvCheckInTime.text = ""
      holder.cv.isActivated = false
      holder.checkInOutBtn.text = context.getText(R.string.check_in)
      holder.ivChildImage.borderColor = context.getColor(R.color.colorRed)
    }
  }

  private fun getAgeFromBirthDate(position: Int): String? {
    val year = SimpleDateFormat(CHILD_ATTEN_BIRTHYEAR_FORMAT, Locale.US).format(
        SimpleDateFormat(FIRESTORE_BIRTHDATE_FORMAT, Locale.US).parse(items[position].birthDate))
    val month = SimpleDateFormat(CHILD_ATTEN_BIRTHMONTH_FORMAT, Locale.US).format(
        SimpleDateFormat(FIRESTORE_BIRTHDATE_FORMAT, Locale.US).parse(items[position].birthDate))
    val day = SimpleDateFormat(CHILD_ATTEN_BIRTH_DAY_FORMAT, Locale.US).format(
        SimpleDateFormat(FIRESTORE_BIRTHDATE_FORMAT, Locale.US).parse(items[position].birthDate))
    val today = GregorianCalendar()

    when {
      month.toInt() < today.get(Calendar.MONTH) + 1 -> return ((today.get(Calendar.YEAR) - year.toInt())).toString() + " yrs old"
      month.toInt() > today.get(Calendar.MONTH) + 1 -> return ((today.get(Calendar.YEAR) - year.toInt()) - 1).toString() + " yrs old"
      month.toInt() == today.get(Calendar.MONTH) + 1 -> when {
        day.toInt() < today.get(Calendar.DAY_OF_MONTH) -> return ((today.get(Calendar.YEAR) - year.toInt())).toString() + " yrs old"
        day.toInt() > today.get(Calendar.DAY_OF_MONTH) -> return ((today.get(Calendar.YEAR) - year.toInt()) - 1).toString() + " yrs old"
        day.toInt() == today.get(Calendar.DAY_OF_MONTH) -> return "Happy " + ((today.get(Calendar.YEAR) - year.toInt())).toString() + " Birthday"
      }
    }
    return ""
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
  val tvChildId: TextView = view.tv_child_id
  val ivChildImage: CircleImageView = view.child_image
  val tvFirstName: TextView = view.tv_first_name
  val tvLastName: TextView = view.tv_last_name
  val tvIsActive: TextView = view.tv_active
  val tvCheckInTime: TextView = view.tv_check_in_time
  val checkInOutBtn: TextView = view.check_in_out_btn
}
