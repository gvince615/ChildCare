package registration

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.vince.childcare.R
import kotlinx.android.synthetic.main.registration_medication_data_card.view.*


class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var medNameLayout: TextInputLayout = itemView.input_layout_med_name
  var medDoseLayout: TextInputLayout = itemView.input_layout_med_dose
  var medTimeLayout: TextInputLayout = itemView.input_layout_med_time

  companion object {

    fun create(context: Context, parent: ViewGroup): MedicationViewHolder {
      return MedicationViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_medication_data_card, parent, false))
    }

    fun bind(holder: MedicationViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      holder.medNameLayout.editText?.setText((listItem as RegistrationCardItem<MedicationData>).`object`.medName)
      holder.medNameLayout.editText?.onChange {
        (listItem as RegistrationCardItem<MedicationData>).`object`.medName = holder.medNameLayout.editText?.text?.toString()!!
      }

      holder.medDoseLayout.editText?.setText((listItem as RegistrationCardItem<MedicationData>).`object`.medDose)
      holder.medDoseLayout.editText?.onChange {
        (listItem as RegistrationCardItem<MedicationData>).`object`.medDose = holder.medDoseLayout.editText?.text?.toString()!!
      }
      holder.medTimeLayout.editText?.setText((listItem as RegistrationCardItem<MedicationData>).`object`.medTime)
      holder.medTimeLayout.editText?.onChange {
        (listItem as RegistrationCardItem<MedicationData>).`object`.medTime = holder.medTimeLayout.editText?.text?.toString()!!
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
