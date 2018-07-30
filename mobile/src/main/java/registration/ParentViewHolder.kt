package registration

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import com.vince.childcare.R
import kotlinx.android.synthetic.main.registration_parent_data_card.view.*



class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var parentCardView: CardView = itemView.parentDataCardView
  var parentDeleteButton: ImageButton = itemView.delete_parent_card_button
  var parentFirstNameLayout: TextInputLayout = itemView.parent_first_name
  var parentLastNameLayout: TextInputLayout = itemView.parent_last_name
  var parentAddressLn1Layout: TextInputLayout = itemView.parent_layout_address_ln_1
  var parentAddressLn2Layout: TextInputLayout = itemView.parent_layout_address_ln_2
  var parentAddressCityLayout: TextInputLayout = itemView.parent_layout_city
  var parentAddressStateLayout: TextInputLayout = itemView.parent_layout_state
  var parentAddressZipLayout: TextInputLayout = itemView.parent_layout_zip

  companion object {

    fun create(context: Context, parent: ViewGroup): ParentViewHolder {
      return ParentViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_parent_data_card, parent, false))
    }

    fun bind(holder: ParentViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      holder.parentFirstNameLayout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.firstName)
      holder.parentLastNameLayout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.lastName)
      holder.parentAddressLn1Layout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.addressLn1)
      holder.parentAddressLn2Layout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.addressLn2)
      holder.parentAddressCityLayout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.addressCity)
      holder.parentAddressStateLayout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.addressState)
      holder.parentAddressZipLayout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.addressZip)

      holder.parentFirstNameLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Parent>).`object`.firstName = holder.parentFirstNameLayout.editText?.text?.toString()!!
      }
      holder.parentLastNameLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Parent>).`object`.lastName = holder.parentLastNameLayout.editText?.text?.toString()!!
      }
      holder.parentAddressLn1Layout.editText?.onChange {
        (listItem as RegistrationCardItem<Parent>).`object`.addressLn1 = holder.parentAddressLn1Layout.editText?.text?.toString()!!
      }
      holder.parentAddressLn2Layout.editText?.onChange {
        (listItem as RegistrationCardItem<Parent>).`object`.addressLn2 = holder.parentAddressLn2Layout.editText?.text?.toString()!!
      }
      holder.parentAddressCityLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Parent>).`object`.addressCity = holder.parentAddressCityLayout.editText?.text?.toString()!!
      }
      holder.parentAddressStateLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Parent>).`object`.addressState = holder.parentAddressStateLayout.editText?.text?.toString()!!
      }
      holder.parentAddressZipLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Parent>).`object`.addressZip = holder.parentAddressZipLayout.editText?.text?.toString()!!
      }




      holder.parentCardView.setOnClickListener {
        listener?.onParentCardClicked("Parent Card Clicked")
      }

      holder.parentDeleteButton.setOnClickListener {
        listener?.onParentCardClicked("Delete Parent Card Clicked")
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
