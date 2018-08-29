package registration

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.rengwuxian.materialedittext.MaterialEditText
import com.rengwuxian.materialedittext.validation.RegexpValidator
import com.vince.childcare.R
import core.DownloadImageTask
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.registration_child_data_card.view.*


class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var childId: TextView = itemView.child_id
  var childImage: CircleImageView = itemView.child_image
  var childFirstNameLayout: MaterialEditText = itemView.child_first_name
  var childLastNameLayout: MaterialEditText = itemView.child_last_name
  var childIsActive: CheckBox = itemView.active_checkbox
  var childDOBLayout: MaterialEditText = itemView.input_layout_dob
  var childLAddressLn1Layout: MaterialEditText = itemView.input_layout_address_ln_1
  var childLAddressLn2Layout: MaterialEditText = itemView.input_layout_address_ln_2
  var childLAddressCityLayout: MaterialEditText = itemView.input_layout_city
  var childLAddressStateLayout: MaterialEditText = itemView.input_layout_state
  var childLAddressZipLayout: MaterialEditText = itemView.input_layout_zip

  companion object {

    fun create(context: Context, parent: ViewGroup): ChildViewHolder {
      return ChildViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_child_data_card, parent, false))
    }

    @Suppress("UNCHECKED_CAST")
    fun bind(context: Context, holder: ChildViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      if ((listItem as RegistrationCardItem<Child>).`object`.childImageUrl != ""
          && listItem.`object`.childImageUrl != "null") {
        DownloadImageTask(holder.childImage).execute((listItem).`object`.childImageUrl)
      }
      holder.childImage.setOnClickListener {
        listener?.childImageClicked(it)
      }

      holder.childId.text = (listItem).`object`.childId

      holder.childFirstNameLayout.setText((listItem).`object`.firstName)
      holder.childFirstNameLayout.addValidator(RegexpValidator(context.getString(R.string.name_error), context.getString(R.string.name_validation)))
      holder.childFirstNameLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.childFirstNameLayout.validate()
      }
      holder.childFirstNameLayout.onChange {
        listItem.`object`.firstName = holder.childFirstNameLayout.text?.toString()!!
      }

      holder.childLastNameLayout.setText((listItem).`object`.lastName)
      holder.childLastNameLayout.addValidator(RegexpValidator(context.getString(R.string.name_error), context.getString(R.string.name_validation)))
      holder.childLastNameLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.childLastNameLayout.validate()
      }
      holder.childLastNameLayout.onChange {
        listItem.`object`.lastName = holder.childLastNameLayout.text?.toString()!!
      }

      holder.childDOBLayout.setText((listItem).`object`.birthDate)
      holder.childDOBLayout.addValidator(RegexpValidator(context.getString(R.string.date_error), context.getString(R.string.date_validation_regex)))
      holder.childDOBLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus)
          holder.childDOBLayout.validate()
      }
      holder.childDOBLayout.onChange {
        listItem.`object`.birthDate = holder.childDOBLayout.text?.toString()!!
      }

      holder.childLAddressLn1Layout.setText(listItem.`object`.addressLn1)
      holder.childLAddressLn2Layout.setText(listItem.`object`.addressLn2)
      holder.childLAddressCityLayout.setText(listItem.`object`.addressCity)
      holder.childLAddressStateLayout.setText(listItem.`object`.addressState)
      holder.childLAddressZipLayout.setText(listItem.`object`.addressZip)


      holder.childId.onChange {
        listItem.`object`.childId = holder.childId.text?.toString()!!
      }


      holder.childLAddressLn1Layout.onChange {
        listItem.`object`.addressLn1 = holder.childLAddressLn1Layout.text?.toString()!!
      }
      holder.childLAddressLn2Layout.onChange {
        listItem.`object`.addressLn2 = holder.childLAddressLn2Layout.text?.toString()!!
      }
      holder.childLAddressCityLayout.onChange {
        listItem.`object`.addressCity = holder.childLAddressCityLayout.text?.toString()!!
      }
      holder.childLAddressStateLayout.onChange {
        listItem.`object`.addressState = holder.childLAddressStateLayout.text?.toString()!!
      }
      holder.childLAddressZipLayout.onChange {
        listItem.`object`.addressZip = holder.childLAddressZipLayout.text?.toString()!!
      }

      holder.childIsActive.isChecked = (listItem.`object`.isActive) == "Active"
      holder.childIsActive.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked)
          (listItem.`object`.isActive) = "Active"
        else
          (listItem.`object`.isActive) = "Inactive"
      }
    }

    private fun MaterialEditText.onChange(cb: (String) -> Unit) {
      this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
          cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
      })
    }

    private fun TextView.onChange(cb: (String) -> Unit) {
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
