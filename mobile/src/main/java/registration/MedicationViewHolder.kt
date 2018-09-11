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
import com.rengwuxian.materialedittext.MaterialEditText
import com.rengwuxian.materialedittext.validation.RegexpValidator
import com.vince.childcare.R
import kotlinx.android.synthetic.main.registration_medication_data_card.view.*


class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var medicationDeleteButton: ImageButton = itemView.delete_medication_card_button

  var medNameLayout: MaterialEditText = itemView.input_layout_med_name
  var medDoseLayout: MaterialEditText = itemView.input_layout_med_dose
  var medTimeLayout: MaterialEditText = itemView.input_layout_med_time

  companion object {

    fun create(context: Context, parent: ViewGroup): MedicationViewHolder {
      return MedicationViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_medication_data_card, parent, false))
    }

    @Suppress("UNCHECKED_CAST")
    fun bind(context: Context, holder: MedicationViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      holder.medNameLayout.setText((listItem as RegistrationCardItem<Medication>).`object`.medicationName)
      holder.medNameLayout.addValidator(RegexpValidator(context.getString(R.string.name_error), context.getString(R.string.name_validation)))
      holder.medNameLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.medNameLayout.validate()
      }
      holder.medNameLayout.onChange {
        listItem.`object`.medicationName = holder.medNameLayout.text?.toString()!!
      }


      holder.medDoseLayout.setText(listItem.`object`.medicationDose)
      holder.medDoseLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.medDoseLayout.validate()
      }
      holder.medDoseLayout.onChange {
        listItem.`object`.medicationDose = holder.medDoseLayout.text?.toString()!!
      }

      holder.medTimeLayout.setText(listItem.`object`.medicationTime)
      holder.medTimeLayout.addValidator(RegexpValidator(context.getString(R.string.time_error), context.getString(R.string.time_validation)))
      holder.medTimeLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.medTimeLayout.validate()
      }
      holder.medTimeLayout.onChange {
        listItem.`object`.medicationTime = holder.medTimeLayout.text?.toString()!!
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
