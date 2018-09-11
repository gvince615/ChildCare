package registration

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import com.rengwuxian.materialedittext.MaterialEditText
import com.rengwuxian.materialedittext.validation.RegexpValidator
import com.vince.childcare.R
import kotlinx.android.synthetic.main.registration_parent_data_card.view.*


class GuardianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var parentCardView: CardView = itemView.parentDataCardView
  var parentDeleteButton: ImageButton = itemView.delete_parent_card_button
  var parentFirstNameLayout: MaterialEditText = itemView.parent_first_name
  var parentLastNameLayout: MaterialEditText = itemView.parent_last_name
  var parentEmailLayout: MaterialEditText = itemView.parent_email
  var parentPhoneNumber1Layout: MaterialEditText = itemView.parent_contact_num_1
  var parentPhoneNumber2Layout: MaterialEditText = itemView.parent_contact_num_2

  companion object {

    fun create(context: Context, parent: ViewGroup): GuardianViewHolder {
      return GuardianViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_parent_data_card, parent, false))
    }

    @Suppress("UNCHECKED_CAST")
    fun bind(context: Context, holder: GuardianViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      holder.parentFirstNameLayout.setText((listItem as RegistrationCardItem<Guardian>).`object`.firstName)
      holder.parentFirstNameLayout.addValidator(RegexpValidator(context.getString(R.string.name_error), context.getString(R.string.name_validation)))
      holder.parentFirstNameLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.parentFirstNameLayout.validate()
      }
      holder.parentFirstNameLayout.onChange {
        listItem.`object`.firstName = holder.parentFirstNameLayout.text?.toString()!!
      }

      holder.parentLastNameLayout.setText(listItem.`object`.lastName)
      holder.parentLastNameLayout.addValidator(RegexpValidator(context.getString(R.string.name_error), context.getString(R.string.name_validation)))
      holder.parentLastNameLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.parentLastNameLayout.validate()
      }
      holder.parentLastNameLayout.onChange {
        listItem.`object`.lastName = holder.parentLastNameLayout.text?.toString()!!
      }

      holder.parentEmailLayout.setText(listItem.`object`.emailAddress)
      holder.parentEmailLayout.addValidator(RegexpValidator(context.getString(R.string.email_error), Patterns.EMAIL_ADDRESS.toRegex().toString()))
      holder.parentEmailLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.parentEmailLayout.validate()
      }
      holder.parentEmailLayout.onChange {
        listItem.`object`.emailAddress = holder.parentEmailLayout.text?.toString()!!
      }

      holder.parentPhoneNumber1Layout.setText(listItem.`object`.phoneNumber1)
      holder.parentPhoneNumber1Layout.addValidator(
          RegexpValidator(context.getString(R.string.phone_error), context.getString(R.string.phone_validation)))
      holder.parentPhoneNumber1Layout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.parentPhoneNumber1Layout.validate()
      }
      holder.parentPhoneNumber1Layout.onChange {
        listItem.`object`.phoneNumber1 = holder.parentPhoneNumber1Layout.text?.toString()!!
      }

      holder.parentPhoneNumber2Layout.setText(listItem.`object`.phoneNumber2)
      holder.parentPhoneNumber2Layout.addValidator(
          RegexpValidator(context.getString(R.string.phone_error), context.getString(R.string.phone_validation)))
      holder.parentPhoneNumber2Layout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.parentPhoneNumber2Layout.validate()
      }
      holder.parentPhoneNumber2Layout.onChange {
        listItem.`object`.phoneNumber2 = holder.parentPhoneNumber2Layout.text?.toString()!!
      }

      holder.parentDeleteButton.setOnClickListener {
        listener?.onDeleteCardBtnTapped(holder.adapterPosition)
      }

      holder.parentCardView.setOnClickListener {

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
