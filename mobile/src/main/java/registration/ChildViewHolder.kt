package registration

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.rengwuxian.materialedittext.MaterialEditText
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

    fun bind(holder: ChildViewHolder, listItem: RegistrationCardItem<*>, listener: RegistrationAdapter.CardItemListener?) {

      if ((listItem as RegistrationCardItem<Child>).`object`.childImageUrl != "") {
        DownloadImageTask(holder.childImage).execute((listItem).`object`.childImageUrl)
      }

      holder.childId.text = (listItem).`object`.childId
      holder.childFirstNameLayout.setText((listItem).`object`.firstName)
      holder.childLastNameLayout.setText((listItem).`object`.lastName)
      holder.childIsActive.isChecked = (listItem.`object`.isActive) == "Active"
      holder.childDOBLayout.setText(listItem.`object`.birthDate)
      holder.childLAddressLn1Layout.setText(listItem.`object`.addressLn1)
      holder.childLAddressLn2Layout.setText(listItem.`object`.addressLn2)
      holder.childLAddressCityLayout.setText(listItem.`object`.addressCity)
      holder.childLAddressStateLayout.setText(listItem.`object`.addressState)
      holder.childLAddressZipLayout.setText(listItem.`object`.addressZip)


      holder.childId.onChange {
        listItem.`object`.childId = holder.childId.text?.toString()!!
      }

      holder.childFirstNameLayout.onChange {
        holder.childFirstNameLayout.error = if (holder.childFirstNameLayout.text.toString().length > 1) null else "Minimum length of 2"
        listItem.`object`.firstName = holder.childFirstNameLayout.text?.toString()!!
      }
      holder.childLastNameLayout.onChange {
        holder.childLastNameLayout.error = if (holder.childLastNameLayout.text.toString().length > 1) null else "Minimum length of 2"
        listItem.`object`.lastName = holder.childLastNameLayout.text?.toString()!!
      }
      holder.childDOBLayout.onChange {
        holder.childDOBLayout.error = if (holder.childDOBLayout.text.toString()
                .matches("([0-9]{2})/([0-9]{2})/([0-9]{4})".toRegex())) null else "Please enter a valid date (MM/DD/YYYY)"

        listItem.`object`.birthDate = holder.childDOBLayout.text?.toString()!!
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

    fun TextView.onChange(cb: (String) -> Unit) {
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
