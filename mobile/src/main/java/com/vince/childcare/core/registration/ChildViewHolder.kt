package com.vince.childcare.core.registration

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.vince.childcare.R


class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


  internal var childCardView: CardView = itemView.findViewById(R.id.childDataCardView)
  internal var childDeleteButton: ImageButton = itemView.findViewById(R.id.delete_child_card_button)
  internal var childImage: ImageView = itemView.findViewById(R.id.child_image)
  internal var childFirstNameLayout: TextInputLayout = itemView.findViewById(R.id.child_first_name)
  internal var childLastNameLayout: TextInputLayout = itemView.findViewById(R.id.child_last_name)
  internal var childDOBLayout: TextInputLayout = itemView.findViewById(R.id.input_layout_dob)
  internal var childLAddressLn1Layout: TextInputLayout = itemView.findViewById(R.id.input_layout_address_ln_1)
  internal var childLAddressLn2Layout: TextInputLayout = itemView.findViewById(R.id.input_layout_address_ln_2)
  internal var childLAddressCityLayout: TextInputLayout = itemView.findViewById(R.id.input_layout_city)
  internal var childLAddressStateLayout: TextInputLayout = itemView.findViewById(R.id.input_layout_state)
  internal var childLAddressZipLayout: TextInputLayout = itemView.findViewById(R.id.input_layout_zip)

  companion object {

    fun create(context: Context, parent: ViewGroup): ChildViewHolder {
      return ChildViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_child_data_card, parent, false))
    }

    fun bind(
        holder: ChildViewHolder, cardItem: Child, pos: Int, listener: RegistrationAdapter.CardItemListener?) {

      holder.childFirstNameLayout.editText?.addTextChangedListener(object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
          cardItem.firstName = s.toString()
        }

      })
      holder.childLastNameLayout.editText?.addTextChangedListener(object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
          cardItem.lastName = s.toString()
        }

      })

//      cardItem.firstName = holder.childFirstNameLayout.editText?.text?.toString()
//      cardItem.lastName = holder.childLastNameLayout.editText?.text?.toString()
      cardItem.birthDate = holder.childDOBLayout.editText?.text?.toString()
      cardItem.addressLn1 = holder.childLAddressLn1Layout.editText?.text?.toString()
      cardItem.addressLn2 = holder.childLAddressLn2Layout.editText?.text?.toString()
      cardItem.addressCity = holder.childLAddressCityLayout.editText?.text?.toString()
      cardItem.addressState = holder.childLAddressStateLayout.editText?.text?.toString()
      cardItem.addressZip = holder.childLAddressZipLayout.editText?.text?.toString()


      holder.childImage.setOnClickListener {

      }
      holder.childDeleteButton.setOnClickListener {

      }
      holder.childCardView.setOnClickListener {
        listener?.onChildCardClicked("clicked")
      }
    }
  }
}
