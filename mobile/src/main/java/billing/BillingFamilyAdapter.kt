package billing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vince.childcare.R
import kotlinx.android.synthetic.main.billing_family_card.view.*
import kotlinx.android.synthetic.main.billing_generate_layout.view.*
import java.util.*


class BillingFamilyAdapter() : RecyclerView.Adapter<ViewHolder>() {

  private lateinit var items: ArrayList<BillingFamily>
  private lateinit var context: Context

  constructor(context: Context, items: ArrayList<BillingFamily>, billingCardItemListener: BillingCardItemListener) : this() {
    this.context = context
    this.items = items
  }

  interface BillingCardItemListener {
    fun billingFamilyCardClicked(position: Int)
    fun generateBillClicked(position: Int)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(context).inflate(R.layout.billing_family_card, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.cv.setOnClickListener {
      if (holder.lvChildren.visibility == View.VISIBLE) {
        holder.lvChildren.visibility = View.GONE
      } else {
        holder.lvChildren.visibility = View.VISIBLE
      }
    }
    holder.tvFamilyName.text = items[position].familyName
    holder.tvFamilyId.text = items[position].familyId
    holder.lvChildren.adapter = BillingChildAdapter(items[position].children, context)
  }

  fun refreshData(familyList: ArrayList<BillingFamily>, pos: Int) {
    this.items = familyList

    if (pos != -1) {
      notifyItemInserted(pos)
    }
    notifyDataSetChanged()
  }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val cv: CardView = view.billingFamilyCardView
  val tvFamilyName: TextView = view.family_name_tv
  val tvFamilyId: TextView = view.family_id_tv
  val lvChildren: ListView = view.billing_child_list
  val generateBillBtn: Button = view.generate_bill_btn
}
