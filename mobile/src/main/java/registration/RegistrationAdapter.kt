package registration

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class RegistrationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private var listener: CardItemListener? = null
  private val context: Context
  private var list: MutableList<RegistrationCardItem<*>>

  constructor(context: Context, data: MutableList<RegistrationCardItem<*>>, listener: CardItemListener) {
    this.context = context
    this.listener = listener
    this.list = data
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    when (viewType) {
      RegistrationCardItem.PARENT -> return GuardianViewHolder.create(context, viewGroup)
      RegistrationCardItem.CHILD -> return ChildViewHolder.create(context, viewGroup)
      RegistrationCardItem.PEDIATRICIAN -> return PediatricianViewHolder.create(context, viewGroup)
      RegistrationCardItem.MEDICATION -> return MedicationViewHolder.create(context, viewGroup)
      RegistrationCardItem.BILLING -> return BillingViewHolder.create(context, viewGroup)
    }
    throw RuntimeException()
  }

  override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

    when (getItemViewType(position)) {
      RegistrationCardItem.PARENT -> GuardianViewHolder.bind(viewHolder as GuardianViewHolder, list[position], listener)
      RegistrationCardItem.CHILD -> ChildViewHolder.bind(context, viewHolder as ChildViewHolder, list[position], listener)
      RegistrationCardItem.PEDIATRICIAN -> PediatricianViewHolder.bind(viewHolder as PediatricianViewHolder, list[position], listener)
      RegistrationCardItem.MEDICATION -> MedicationViewHolder.bind(viewHolder as MedicationViewHolder, list[position], listener)
      RegistrationCardItem.BILLING -> BillingViewHolder.bind(context, viewHolder as BillingViewHolder, list[position], listener)
    }
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun getItemViewType(position: Int): Int {
    return list[position].viewType
  }

  fun getList(): MutableList<RegistrationCardItem<*>> {
    return list
  }

  fun setUpForEdit() {
    notifyDataSetChanged()
  }

  fun deleteCard(adapterPosition: Int) {
    list.removeAt(adapterPosition)
    notifyItemRemoved(adapterPosition)
  }

  fun addParent(guardian: Guardian) {
    list.add(RegistrationCardItem(guardian, RegistrationCardItem.PARENT))
    notifyItemInserted(list.size)
  }

  fun addChild(child: Child) {
    list.add(RegistrationCardItem(child, RegistrationCardItem.CHILD))
    notifyItemInserted(list.size)
  }

  fun addPediatrician(pediatrician: Pediatrician) {
    list.add(RegistrationCardItem(pediatrician, RegistrationCardItem.PEDIATRICIAN))
    notifyItemInserted(list.size)
  }

  fun addMedication(medication: Medication) {
    list.add(RegistrationCardItem(medication, RegistrationCardItem.MEDICATION))
    notifyItemInserted(list.size)
  }

  fun addBilling(billing: Billing) {
    list.add(RegistrationCardItem(billing, RegistrationCardItem.BILLING))
    notifyItemInserted(list.size)
  }

  interface CardItemListener {
    fun onDeleteCardBtnTapped(adapterPosition: Int)
    fun childImageClicked(it: View?)
  }
}
