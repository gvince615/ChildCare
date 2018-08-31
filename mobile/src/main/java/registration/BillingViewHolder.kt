package registration

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.rengwuxian.materialedittext.MaterialEditText
import com.rengwuxian.materialedittext.validation.RegexpValidator
import com.vince.childcare.R
import fr.ganfra.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.registration_billing_data_card.view.*


class BillingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var billingDeleteButton: ImageButton = itemView.delete_billing_card_button
  var flatRateLayout: LinearLayout = itemView.flat_rate_container
  var hourlyRateLayout: LinearLayout = itemView.hourly_rate_container
  //  var discountLayout: LinearLayout = itemView.discount_container
//  var exampleLayout: LinearLayout = itemView.example_container
  var billingTypeSwitch: Switch = itemView.billing_type_switch
  var flatRateBillingAmount: MaterialEditText = itemView.flat_rate_billing_amount
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

    @Suppress("UNCHECKED_CAST")
    fun bind(context: Context, holder: BillingViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {
      setAdapersForSpinners(holder, context)

      if ((listItem.`object` as Billing).billingType == "Flat Rate") {
        holder.billingTypeSwitch.isChecked = false
        holder.hourlyRateLayout.visibility = View.GONE
        holder.flatRateLayout.visibility = View.VISIBLE

        holder.hourlyRateSpinner.setSelection(0)
        holder.maxHoursSpinner.setSelection(0)
        holder.minTimeSpinner.setSelection(0)
        holder.roundUpRuleSpinner.setSelection(0)

      } else if ((listItem.`object` as Billing).billingType == "Hourly Rate") {
        holder.billingTypeSwitch.isChecked = true
        holder.hourlyRateLayout.visibility = View.VISIBLE
        holder.flatRateLayout.visibility = View.GONE

        holder.flatRateBillingAmount.setText("")
      }

      holder.billingTypeSwitch.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
          holder.hourlyRateLayout.visibility = View.VISIBLE
          holder.flatRateLayout.visibility = View.GONE
          (listItem.`object` as Billing).billingType = "Hourly Rate"
        } else {
          holder.hourlyRateLayout.visibility = View.GONE
          holder.flatRateLayout.visibility = View.VISIBLE
          (listItem.`object` as Billing).billingType = "Flat Rate"
        }
      }

      setupBillingCycleSpinner(holder.billingCycleSpinner, listItem as RegistrationCardItem<Billing>,
          context.resources.getStringArray(R.array.billing_cycle_items))
      setupHourlyRateSpinner(holder.hourlyRateSpinner, listItem, context.resources.getStringArray(R.array.hourly_rate_items))
      setupMaxHoursSpinner(holder.maxHoursSpinner, listItem, context.resources.getStringArray(R.array.max_billable_hours_items))
      setupMinTimeSpinner(holder.minTimeSpinner, listItem, context.resources.getStringArray(R.array.min_billable_time_items))
      setupRoundUpSpinner(holder.roundUpRuleSpinner, listItem, context.resources.getStringArray(R.array.round_up_rule_items))
      setupDiscountSpinner(holder.discountSpinner, listItem, context.resources.getStringArray(R.array.discount_percentage_items))
      setupDiscountDescSpinner(holder.discountDescSpinner, listItem, context.resources.getStringArray(R.array.discount_desc_items))

      holder.flatRateBillingAmount.setText((listItem.`object`).flatRateAmount)
      holder.flatRateBillingAmount.addValidator(
          RegexpValidator(context.getString(R.string.currency_error), context.getString(R.string.currency_validation)))
      holder.flatRateBillingAmount.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.flatRateBillingAmount.validate()
      }
      holder.flatRateBillingAmount.onChange {
        listItem.`object`.flatRateAmount = holder.flatRateBillingAmount.text?.toString()!!
      }

      holder.billingDeleteButton.setOnClickListener {
        listener?.onDeleteCardBtnTapped(holder.adapterPosition)
      }
    }

    private fun setupDiscountDescSpinner(spinner: MaterialSpinner, listItem: RegistrationCardItem<Billing>, stringArray: Array<out String>) {
      spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
          if (position != -1) {
            listItem.`object`.discountType = stringArray[position]
          } else {
            listItem.`object`.discountType = ""
          }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
          listItem.`object`.discountType = ""
        }
      }

      if (listItem.`object`.discountType != "") {
        spinner.setSelection(stringArray.indexOf(listItem.`object`.discountType) + 1)
      }
    }

    private fun setupDiscountSpinner(spinner: MaterialSpinner, listItem: RegistrationCardItem<Billing>, stringArray: Array<out String>) {
      spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
          if (position != -1) {
            listItem.`object`.discountPercent = stringArray[position]
          } else {
            listItem.`object`.discountPercent = ""
          }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
          listItem.`object`.discountPercent = ""
        }
      }

      if (listItem.`object`.discountPercent != "") {
        spinner.setSelection(stringArray.indexOf(listItem.`object`.discountPercent) + 1)
      }
    }

    private fun setupRoundUpSpinner(spinner: MaterialSpinner, listItem: RegistrationCardItem<Billing>, stringArray: Array<out String>) {
      spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
          if (position != -1) {
            listItem.`object`.hourlyRoundUpRule = stringArray[position]
          } else {
            listItem.`object`.hourlyRoundUpRule = ""
          }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
          listItem.`object`.hourlyRoundUpRule = ""
        }
      }

      if (listItem.`object`.hourlyRoundUpRule != "") {
        spinner.setSelection(stringArray.indexOf(listItem.`object`.hourlyRoundUpRule) + 1)
      }
    }

    private fun setupMinTimeSpinner(spinner: MaterialSpinner, listItem: RegistrationCardItem<Billing>, stringArray: Array<out String>) {
      spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
          if (position != -1) {
            listItem.`object`.hourlyMinBillableTime = stringArray[position]
          } else {
            listItem.`object`.hourlyMinBillableTime = ""
          }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
          listItem.`object`.hourlyMinBillableTime = ""
        }
      }

      if (listItem.`object`.hourlyMinBillableTime != "") {
        spinner.setSelection(stringArray.indexOf(listItem.`object`.hourlyMinBillableTime) + 1)
      }
    }

    private fun setupMaxHoursSpinner(spinner: MaterialSpinner, listItem: RegistrationCardItem<Billing>, stringArray: Array<out String>) {
      spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
          if (position != -1) {
            listItem.`object`.hourlyMaxBillableHours = stringArray[position]
          } else {
            listItem.`object`.hourlyMaxBillableHours = ""
          }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
          listItem.`object`.hourlyMaxBillableHours = ""
        }
      }

      if (listItem.`object`.hourlyMaxBillableHours != "") {
        spinner.setSelection(stringArray.indexOf(listItem.`object`.hourlyMaxBillableHours) + 1)
      }
    }

    private fun setupHourlyRateSpinner(spinner: MaterialSpinner, listItem: RegistrationCardItem<Billing>, stringArray: Array<out String>) {
      spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
          if (position != -1) {
            listItem.`object`.hourlyRateAmount = stringArray[position]
          } else {
            listItem.`object`.hourlyRateAmount = ""
          }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
          listItem.`object`.hourlyRateAmount = ""
        }
      }

      if (listItem.`object`.hourlyRateAmount != "") {
        spinner.setSelection(stringArray.indexOf(listItem.`object`.hourlyRateAmount) + 1)

      }
    }

    private fun setupBillingCycleSpinner(spinner: MaterialSpinner, listItem: RegistrationCardItem<Billing>, stringArray: Array<String>) {

      spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
          if (position != -1) {
            listItem.`object`.billingCycle = stringArray[position]
          } else {
            listItem.`object`.billingCycle = ""
          }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
          listItem.`object`.billingCycle = ""
        }
      }

      if (listItem.`object`.billingCycle != "") {
        spinner.setSelection(stringArray.indexOf(listItem.`object`.billingCycle) + 1)
      }
    }

    private fun setAdapersForSpinners(holder: BillingViewHolder, context: Context) {
      holder.billingCycleSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.billing_cycle_items, android.R.layout.simple_list_item_1)
      holder.hourlyRateSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.hourly_rate_items, android.R.layout.simple_list_item_1)
      holder.maxHoursSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.max_billable_hours_items, android.R.layout.simple_list_item_1)
      holder.minTimeSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.min_billable_time_items, android.R.layout.simple_list_item_1)
      holder.roundUpRuleSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.round_up_rule_items, android.R.layout.simple_list_item_1)
      holder.discountSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.discount_percentage_items,
          android.R.layout.simple_list_item_1)
      holder.discountDescSpinner.adapter = ArrayAdapter.createFromResource(context, R.array.discount_desc_items, android.R.layout.simple_list_item_1)
    }

    private fun EditText.onChange(cb: (String) -> Unit) {
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
