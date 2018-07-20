package com.vince.childcare.core.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.vince.childcare.R
import com.vince.childcare.core.registration.BillingData
import com.vince.childcare.core.registration.ChildData
import com.vince.childcare.core.registration.MedicalData
import com.vince.childcare.core.registration.ParentData
import kotlinx.android.synthetic.main.registration_child_data_card.view.*

class RegistrationAdapter
(
  // The cards to display in your RecyclerView
    private val cards: ArrayList<Any>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  // Return the size of your dataset (invoked by the layout manager)
  override fun getItemCount(): Int {
    return this.cards.size
  }

  override fun getItemViewType(position: Int): Int {
    if (cards[position] is ChildData) {
      return CHILD
    }
    if (cards[position] is ParentData) {
      return PARENT
    }
    if (cards[position] is MedicalData) {
      return MEDICAL
    }
    if (cards[position] is BillingData) {
      return BILLING
    }
    return -1
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val viewHolder: RecyclerView.ViewHolder
    val inflater = LayoutInflater.from(viewGroup.context)

    when (viewType) {
      CHILD -> {
        val v1 = inflater.inflate(R.layout.registration_child_data_card, viewGroup, false)
        viewHolder = ChildHolder(v1)
      }
      PARENT -> {
        val v2 = inflater.inflate(R.layout.registration_parent_data_card, viewGroup, false)
        viewHolder = ParentHolder(v2)
      }
      MEDICAL -> {
        val v3 = inflater.inflate(R.layout.registration_medical_data_card, viewGroup, false)
        viewHolder = MedicalHolder(v3)
      }
      BILLING -> {
        val v4 = inflater.inflate(R.layout.registration_billing_data_card, viewGroup, false)
        viewHolder = BillingHolder(v4)
      }
      else -> {
        val v = inflater.inflate(R.layout.registration_empty_data_card, viewGroup, false)
        viewHolder = EmptyHolder(v)
      }
    }
    return viewHolder
  }

  override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
    when (viewHolder.itemViewType) {
      CHILD -> {
        val vh1 = viewHolder as ChildHolder
        configureChildViewHolder(vh1, position)
      }
      PARENT -> {
        val vh2 = viewHolder as ParentHolder
        configureParentViewHolder(vh2, position)
      }
      MEDICAL -> {
        val vh4 = viewHolder as MedicalHolder
        configureMedicalViewHolder(vh4, position)
      }
      BILLING -> {
        val vh5 = viewHolder as BillingHolder
        configureBillingViewHolder(vh5, position)
      }
      else -> {
        val vh = viewHolder as EmptyHolder
        configureEmptyViewHolder(vh, position)
      }
    }
  }

  private fun configureEmptyViewHolder(vh: EmptyHolder, position: Int) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  private fun configureChildViewHolder(childViewHolder: ChildHolder, position: Int) {
    var childData = (ChildData())
    childData.first_name = childViewHolder.itemView.input_layout_first_name.editText?.text.toString()
    childData.last_name = childViewHolder.itemView.input_layout_last_name.editText?.text.toString()

    childViewHolder.itemView.childDataCardView.setOnClickListener {
      Toast.makeText(context, "child card tapped ", Toast.LENGTH_SHORT).show()
    }

    childViewHolder.itemView.child_image.setOnClickListener {
      Toast.makeText(context, "child image tapped ", Toast.LENGTH_SHORT).show()
      //todo - start image capture, set image bitmap to imageview
    }

    childViewHolder.itemView.delete_child_card_button.setOnClickListener {
      cards.remove(cards[position])
      notifyItemRemoved(position)
      notifyItemRangeChanged(position, itemCount)
    }
  }

  private fun configureParentViewHolder(parentViewHolder: ParentHolder, position: Int) {
    var parentData = (ParentData())
//    parentViewHolder.itemView.parentDataCardView.setOnClickListener {
//      Toast.makeText(context, "parent card tapped ", Toast.LENGTH_SHORT).show()
//    }
//
//    parentViewHolder.itemView.edit_save_card_button.setOnClickListener {
//      if (parentViewHolder.itemView.small_card_layout.visibility == View.VISIBLE) {
//        showParentBigCardLayout(parentViewHolder)
//        Toast.makeText(context, "edit tapped ", Toast.LENGTH_SHORT).show()
//      } else {
//        showParentSmallCardLayout(parentViewHolder)
//        Toast.makeText(context, "save tapped ", Toast.LENGTH_SHORT).show()
//      }
//    }
//
//    parentViewHolder.itemView.delete_card_button.setOnClickListener {
//      Toast.makeText(context, "delete tapped ", Toast.LENGTH_SHORT).show()
//      cards.remove(cards[position])
//      notifyItemRemoved(position)
//      notifyItemRangeChanged(position, itemCount)
//      showParentSmallCardLayout(parentViewHolder)
//    }
  }

  private fun configureMedicalViewHolder(medicalViewHolder: MedicalHolder, position: Int) {
    var medicalData = (MedicalData())

  }

  private fun configureBillingViewHolder(billingViewHolder: BillingHolder, position: Int) {
    var billingData = (BillingData())

//    if (billingData != null) {
//      billingViewHolder.getDiscountDescription_label()
//          .getEditText()
//          .setText(billingData.discount_description);
//      //billingViewHolder.getDiscountAmount().getChildAt(position).getText.(discountData.discount_description);
  }


  companion object {
    const val CHILD = 0
    const val PARENT = 1
    const val MEDICAL = 2
    const val BILLING = 3
  }
}

