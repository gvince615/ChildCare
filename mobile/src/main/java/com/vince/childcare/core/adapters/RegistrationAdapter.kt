package com.vince.childcare.core.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vince.childcare.R
import com.vince.childcare.core.registration.BillingData
import com.vince.childcare.core.registration.ChildData
import com.vince.childcare.core.registration.MedicalData
import com.vince.childcare.core.registration.ParentData

class RegistrationAdapter
(
  // The cards to display in your RecyclerView
  private val cards: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

  }

  private fun configureParentViewHolder(parentViewHolder: ParentHolder, position: Int) {
    var parentData = (ParentData())

  }

  private fun configureMedicalViewHolder(medicalViewHolder: MedicalHolder, position: Int) {
    var medicalData = (MedicalData())

  }

  private fun configureBillingViewHolder(billingViewHolder: BillingHolder, position: Int) {
    var billingData = (BillingData())

    //if (billingData != null) {
    //  billingViewHolder.getDiscountDescription_label()
    //      .getEditText()
    //      .setText(billingData.discount_description);
    //  //billingViewHolder.getDiscountAmount().getChildAt(position).getText.(discountData.discount_description);
  }

  companion object {

    val CHILD = 0
    val PARENT = 1
    val MEDICAL = 2
    val BILLING = 3
  }
}

