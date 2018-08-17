package registration

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import com.vince.childcare.R
import core.DownloadImageTask
import kotlinx.android.synthetic.main.registration_child_data_card.view.*


class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var childImage: ImageView = itemView.child_image
  var childFirstNameLayout: TextInputLayout = itemView.child_first_name
  var childLastNameLayout: TextInputLayout = itemView.child_last_name
  var childIsActive: CheckBox = itemView.active_checkbox
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

      if ((listItem as RegistrationCardItem<Child>).`object`.childImageUrl != "") {
        DownloadImageTask(holder.childImage).execute((listItem).`object`.childImageUrl)
      }

      holder.childFirstNameLayout.editText?.setText((listItem).`object`.firstName)
      holder.childLastNameLayout.editText?.setText((listItem).`object`.lastName)
      holder.childIsActive.isChecked = (listItem.`object`.isActive) == "Active"
      holder.childDOBLayout.editText?.setText(listItem.`object`.birthDate)
      holder.childLAddressLn1Layout.editText?.setText(listItem.`object`.addressLn1)
      holder.childLAddressLn2Layout.editText?.setText(listItem.`object`.addressLn2)
      holder.childLAddressCityLayout.editText?.setText(listItem.`object`.addressCity)
      holder.childLAddressStateLayout.editText?.setText(listItem.`object`.addressState)
      holder.childLAddressZipLayout.editText?.setText(listItem.`object`.addressZip)

      holder.childFirstNameLayout.editText?.onChange {
        holder.childFirstNameLayout.error = if (holder.childFirstNameLayout.editText?.text.toString().length > 1) null else "Minimum length of 2"
        listItem.`object`.firstName = holder.childFirstNameLayout.editText?.text?.toString()!!
      }
      holder.childLastNameLayout.editText?.onChange {
        holder.childLastNameLayout.error = if (holder.childLastNameLayout.editText?.text.toString().length > 1) null else "Minimum length of 2"
        listItem.`object`.lastName = holder.childLastNameLayout.editText?.text?.toString()!!
      }
      holder.childDOBLayout.editText?.onChange {
        holder.childDOBLayout.error = if (holder.childDOBLayout.editText?.text.toString()
                .matches("([0-9]{2})/([0-9]{2})/([0-9]{4})".toRegex())) null else "Please enter a valid date (MM/DD/YYYY)"

        listItem.`object`.birthDate = holder.childDOBLayout.editText?.text?.toString()!!
      }
      holder.childLAddressLn1Layout.editText?.onChange {
        listItem.`object`.addressLn1 = holder.childLAddressLn1Layout.editText?.text?.toString()!!
      }
      holder.childLAddressLn2Layout.editText?.onChange {
        listItem.`object`.addressLn2 = holder.childLAddressLn2Layout.editText?.text?.toString()!!
      }
      holder.childLAddressCityLayout.editText?.onChange {
        listItem.`object`.addressCity = holder.childLAddressCityLayout.editText?.text?.toString()!!
      }
      holder.childLAddressStateLayout.editText?.onChange {
        listItem.`object`.addressState = holder.childLAddressStateLayout.editText?.text?.toString()!!
      }
      holder.childLAddressZipLayout.editText?.onChange {
        listItem.`object`.addressZip = holder.childLAddressZipLayout.editText?.text?.toString()!!
      }

      holder.childIsActive.setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked)
          (listItem.`object`.isActive) = "Active"
        else
          (listItem.`object`.isActive) = "Inactive"
      }

      holder.childImage.setOnClickListener {
        listener?.childImageClicked(it)
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
