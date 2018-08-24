package registration

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import com.vince.childcare.R
import fr.ganfra.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.registration_billing_data_card.view.*


class BillingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var billingDeleteButton: ImageButton = itemView.delete_billing_card_button

  var billingCycleSpinner: MaterialSpinner = itemView.billing_cycle_spinner
  var hourlyRateSpinner: MaterialSpinner = itemView.hourly_rate_spinner
  var maxHoursSpinner: MaterialSpinner = itemView.max_billable_hours_spinner
  var minTimeSpinner: MaterialSpinner = itemView.min_billable_time_spinner
  var roundUpRuleSpinner: MaterialSpinner = itemView.round_up_rule_spinner
  var discountSpinner: MaterialSpinner = itemView.discount_percentage_spinner
  var discountDescSpinner: MaterialSpinner = itemView.discount_desc_spinner

  companion object {

    fun create(context: Context, parent: ViewGroup): BillingViewHolder {
      return BillingViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_billing_data_card, parent, false))
    }

    fun bind(context: Context, holder: BillingViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {
      holder.billingCycleSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.billing_cycle_items, android.R.layout.simple_list_item_1)
      holder.hourlyRateSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.hourly_rate_items, android.R.layout.simple_list_item_1)
      holder.maxHoursSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.max_billable_hours_items, android.R.layout.simple_list_item_1)
      holder.minTimeSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.min_billable_time_items, android.R.layout.simple_list_item_1)
      holder.roundUpRuleSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.round_up_rule_items, android.R.layout.simple_list_item_1)
      holder.discountSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.discount_percentage_items,
          android.R.layout.simple_list_item_1)
      holder.discountDescSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.discount_desc_items, android.R.layout.simple_list_item_1)

//      holder.billingCycleSpinner.editText?.setText((listItem as RegistrationCardItem<MedicationData>).`object`.medName)
//      holder.medNameLayout.editText?.onChange {
//        (listItem as RegistrationCardItem<MedicationData>).`object`.medName = holder.medNameLayout.editText?.text?.toString()!!
//      }
//
//      holder.medDoseLayout.editText?.setText((listItem as RegistrationCardItem<MedicationData>).`object`.medDose)
//      holder.medDoseLayout.editText?.onChange {
//        (listItem as RegistrationCardItem<MedicationData>).`object`.medDose = holder.medDoseLayout.editText?.text?.toString()!!
//      }
//      holder.medTimeLayout.editText?.setText((listItem as RegistrationCardItem<MedicationData>).`object`.medTime)
//      holder.medTimeLayout.editText?.onChange {
//        (listItem as RegistrationCardItem<MedicationData>).`object`.medTime = holder.medTimeLayout.editText?.text?.toString()!!
//      }

      holder.billingDeleteButton.setOnClickListener {
        listener?.onDeleteCardBtnTapped(holder.adapterPosition)
      }
    }


    fun EditText.onChange(cb: (String) -> Unit) {
      this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
          cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
      })
    }
  }
}
