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
import android.widget.ImageButton
import com.vince.childcare.R
import kotlinx.android.synthetic.main.registration_pediatrician_data_card.view.*


class PediatricianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var pediatricianDeleteButton: ImageButton = itemView.delete_pediatrician_card_button

  var pedNameLayout: TextInputLayout = itemView.input_layout_ped_name
  var pedOfficeNameLayout: TextInputLayout = itemView.input_layout_ped_office_name
  var pedOfficeNumberLayout: TextInputLayout = itemView.input_layout_ped_contact_num_1

  companion object {

    fun create(context: Context, parent: ViewGroup): PediatricianViewHolder {
      return PediatricianViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_pediatrician_data_card, parent, false))
    }

    fun bind(holder: PediatricianViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      holder.pedNameLayout.editText?.setText((listItem as RegistrationCardItem<PediatricianData>).`object`.pedName)
      holder.pedNameLayout.editText?.onChange {
        (listItem as RegistrationCardItem<PediatricianData>).`object`.pedName = holder.pedNameLayout.editText?.text?.toString()!!
      }

      holder.pedOfficeNameLayout.editText?.setText((listItem as RegistrationCardItem<PediatricianData>).`object`.pedOfficeName)
      holder.pedOfficeNameLayout.editText?.onChange {
        (listItem as RegistrationCardItem<PediatricianData>).`object`.pedOfficeName = holder.pedOfficeNameLayout.editText?.text?.toString()!!
      }

      holder.pedOfficeNumberLayout.editText?.setText((listItem as RegistrationCardItem<PediatricianData>).`object`.pedOfficeNum)
      holder.pedOfficeNumberLayout.editText?.onChange {
        (listItem as RegistrationCardItem<PediatricianData>).`object`.pedOfficeNum = holder.pedOfficeNumberLayout.editText?.text?.toString()!!
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
