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
import android.widget.ImageView
import com.vince.childcare.R
import kotlinx.android.synthetic.main.registration_child_data_card.view.*


class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var childCardView: CardView = itemView.childDataCardView
  var childDeleteButton: ImageButton = itemView.delete_child_card_button
  var childImage: ImageView = itemView.child_image
  var childFirstNameLayout: TextInputLayout = itemView.child_first_name
  var childLastNameLayout: TextInputLayout = itemView.child_last_name
  var childDOBLayout: TextInputLayout = itemView.input_layout_dob
  var childLAddressLn1Layout: TextInputLayout = itemView.input_layout_address_ln_1
  var childLAddressLn2Layout: TextInputLayout = itemView.input_layout_address_ln_2
  var childLAddressCityLayout: TextInputLayout = itemView.input_layout_city
  var childLAddressStateLayout: TextInputLayout = itemView.input_layout_state
  var childLAddressZipLayout: TextInputLayout = itemView.input_layout_zip

  companion object {

    fun create(context: Context, parent: ViewGroup): ChildViewHolder {
      return ChildViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_child_data_card, parent, false))
    }

    fun bind(holder: ChildViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      holder.childFirstNameLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Child>).`object`.firstName = holder.childFirstNameLayout.editText?.text?.toString()
      }
      holder.childLastNameLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Child>).`object`.lastName = holder.childLastNameLayout.editText?.text?.toString()
      }
      holder.childDOBLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Child>).`object`.birthDate = holder.childDOBLayout.editText?.text?.toString()
      }
      holder.childLAddressLn1Layout.editText?.onChange {
        (listItem as RegistrationCardItem<Child>).`object`.addressLn1 = holder.childLAddressLn1Layout.editText?.text?.toString()
      }
      holder.childLAddressLn2Layout.editText?.onChange {
        (listItem as RegistrationCardItem<Child>).`object`.addressLn2 = holder.childLAddressLn2Layout.editText?.text?.toString()
      }
      holder.childLAddressCityLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Child>).`object`.addressCity = holder.childLAddressCityLayout.editText?.text?.toString()
      }
      holder.childLAddressStateLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Child>).`object`.addressState = holder.childLAddressStateLayout.editText?.text?.toString()
      }
      holder.childLAddressZipLayout.editText?.onChange {
        (listItem as RegistrationCardItem<Child>).`object`.addressZip = holder.childLAddressZipLayout.editText?.text?.toString()
      }

      holder.childImage.setOnClickListener {

      }
      holder.childDeleteButton.setOnClickListener {

      }
      holder.childCardView.setOnClickListener {
        listener?.onChildCardClicked("clicked")
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
