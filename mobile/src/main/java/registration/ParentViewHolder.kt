package registration

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import com.vince.childcare.R
import kotlinx.android.synthetic.main.registration_parent_data_card.view.*


class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var parentCardView: CardView = itemView.parentDataCardView
  var parentDeleteButton: ImageButton = itemView.delete_parent_card_button
  var parentFirstNameLayout: TextInputLayout = itemView.parent_first_name
  var parentLastNameLayout: TextInputLayout = itemView.parent_last_name

  var parentEmailLayout: TextInputLayout = itemView.parent_email
  var parentPhoneNumber1Layout: TextInputLayout = itemView.parent_contact_num_1
  var parentPhoneNumber2Layout: TextInputLayout = itemView.parent_contact_num_2


  var parentAddressLn1Layout: TextInputLayout = itemView.parent_layout_address_ln_1
  var parentAddressLn2Layout: TextInputLayout = itemView.parent_layout_address_ln_2
  var parentAddressCityLayout: TextInputLayout = itemView.parent_layout_city
  var parentAddressStateLayout: TextInputLayout = itemView.parent_layout_state
  var parentAddressZipLayout: TextInputLayout = itemView.parent_layout_zip

  var parentAddressCheckbox: CheckBox = itemView.address_checkbox
  var parentAddressLinearLayout: LinearLayout = itemView.address_layout


  companion object {

    fun create(context: Context, parent: ViewGroup): ParentViewHolder {
      return ParentViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_parent_data_card, parent, false))
    }

    fun bind(holder: ParentViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      holder.parentFirstNameLayout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.firstName)
      holder.parentLastNameLayout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.lastName)
      holder.parentEmailLayout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.emailAddress)
      holder.parentPhoneNumber1Layout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.phoneNumber1)
      holder.parentPhoneNumber2Layout.editText?.setText((listItem as RegistrationCardItem<Parent>).`object`.phoneNumber2)

      holder.parentFirstNameLayout.editText?.onChange {
        holder.parentFirstNameLayout.error = if (holder.parentFirstNameLayout.editText?.text.toString().length > 1) null else "Minimum length of 2"
        (listItem as RegistrationCardItem<Parent>).`object`.firstName = holder.parentFirstNameLayout.editText?.text?.toString()!!
      }
      holder.parentLastNameLayout.editText?.onChange {
        holder.parentLastNameLayout.error = if (holder.parentLastNameLayout.editText?.text.toString().length > 1) null else "Minimum length of 2"
        (listItem as RegistrationCardItem<Parent>).`object`.lastName = holder.parentLastNameLayout.editText?.text?.toString()!!
      }
      holder.parentEmailLayout.editText?.onChange {
        holder.parentEmailLayout.error = if (holder.parentEmailLayout.editText?.text.toString().matches(
                Patterns.EMAIL_ADDRESS.toRegex())) null else "Please enter a valid email address (name@example.com)"
      }
      holder.parentPhoneNumber1Layout.editText?.onChange {
        holder.parentPhoneNumber1Layout.error = if (holder.parentPhoneNumber1Layout.editText?.text.toString()
                .matches("([0-9]{3})-([0-9]{3})-([0-9]{4})".toRegex())) null else "Please enter a valid phone number (###-###-####)"
      }
      holder.parentPhoneNumber2Layout.editText?.onChange {
        holder.parentPhoneNumber2Layout.error = if (holder.parentPhoneNumber2Layout.editText?.text.toString()
                .matches("([0-9]{3})-([0-9]{3})-([0-9]{4})".toRegex())) null else "Please enter a valid phone number (###-###-####)"
      }

      holder.parentAddressCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked) {
          holder.parentAddressLinearLayout.visibility = View.GONE

        } else {
          holder.parentAddressLinearLayout.visibility = View.VISIBLE
        }
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
