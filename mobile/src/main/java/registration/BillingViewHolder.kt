package registration

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import com.vince.childcare.R
import kotlinx.android.synthetic.main.registration_billing_data_card.view.*


class BillingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var billingDeleteButton: ImageButton = itemView.delete_billing_card_button


//  var medNameLayout: TextInputLayout = itemView.input_layout_ped_name
//  var medDoseLayout: TextInputLayout = itemView.input_layout_ped_office_name
//  var medTimeLayout: TextInputLayout = itemView.input_layout_ped_contact_num_1

  companion object {

    fun create(context: Context, parent: ViewGroup): BillingViewHolder {
      return BillingViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_billing_data_card, parent, false))
    }

    fun bind(holder: BillingViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

//      holder.medNameLayout.editText?.setText((listItem as RegistrationCardItem<MedicationData>).`object`.medName)
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
