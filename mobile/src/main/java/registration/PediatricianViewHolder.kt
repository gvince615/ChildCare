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
import kotlinx.android.synthetic.main.registration_pediatrician_data_card.view.*


class PediatricianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var pediatricianDeleteButton: ImageButton = itemView.delete_pediatrician_card_button

  var pedNameLayout: MaterialEditText = itemView.input_layout_ped_name
  var pedOfficeNameLayout: MaterialEditText = itemView.input_layout_ped_office_name
  var pedOfficeNumberLayout: MaterialEditText = itemView.input_layout_ped_contact_num_1

  companion object {

    fun create(context: Context, parent: ViewGroup): PediatricianViewHolder {
      return PediatricianViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_pediatrician_data_card, parent, false))
    }

    @Suppress("UNCHECKED_CAST")
    fun bind(context: Context, holder: PediatricianViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      holder.pedNameLayout.setText((listItem as RegistrationCardItem<Pediatrician>).`object`.pediatricianName)
      holder.pedNameLayout.addValidator(RegexpValidator(context.getString(R.string.name_error), context.getString(R.string.name_validation)))
      holder.pedNameLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.pedNameLayout.validate()
      }
      holder.pedNameLayout.onChange {
        listItem.`object`.pediatricianName = holder.pedNameLayout.text?.toString()!!
      }

      holder.pedOfficeNameLayout.setText(listItem.`object`.pediatricianOfficeName)
      holder.pedOfficeNameLayout.addValidator(RegexpValidator(context.getString(R.string.name_error), context.getString(R.string.name_validation)))
      holder.pedOfficeNameLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.pedOfficeNameLayout.validate()
      }
      holder.pedOfficeNameLayout.onChange {
        listItem.`object`.pediatricianOfficeName = holder.pedOfficeNameLayout.text?.toString()!!
      }

      holder.pedOfficeNumberLayout.setText(listItem.`object`.pediatricianOfficeNumber)
      holder.pedOfficeNumberLayout.addValidator(
          RegexpValidator(context.getString(R.string.phone_error), context.getString(R.string.phone_validation)))
      holder.pedOfficeNumberLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.pedOfficeNumberLayout.validate()
      }
      holder.pedOfficeNumberLayout.onChange {
        listItem.`object`.pediatricianOfficeNumber = holder.pedOfficeNumberLayout.text?.toString()!!
      }

      holder.pediatricianDeleteButton.setOnClickListener {
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
