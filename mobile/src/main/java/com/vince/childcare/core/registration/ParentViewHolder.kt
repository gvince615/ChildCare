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
import android.widget.EditText
import android.widget.ImageButton
import com.vince.childcare.R
import kotlinx.android.synthetic.main.registration_parent_data_card.view.*

class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var parentCardView: CardView = itemView.parentDataCardView
  var parentDeleteButton: ImageButton = itemView.delete_parent_card_button
  var parentFirstName: TextInputLayout = itemView.parent_first_name
  var parentLastName: TextInputLayout = itemView.parent_last_name

  companion object {

    fun create(context: Context, parent: ViewGroup): ParentViewHolder {
      return ParentViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_parent_data_card, parent, false))
    }

    fun bind(holder: ParentViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {
      holder.parentFirstName.editText?.onChange {
        (listItem as RegistrationCardItem<Parent>).`object`.firstName = holder.parentFirstName.editText?.text?.toString()
      }
      holder.parentLastName.editText?.onChange {
        (listItem as RegistrationCardItem<Parent>).`object`.lastName = holder.parentLastName.editText?.text?.toString()
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
