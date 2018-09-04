package billing

import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.TextView
import attendance.AttendanceRecord
import com.vince.childcare.R
import core.BottomSheetListView
import kotlinx.android.synthetic.main.billing_child_layout.view.*


class BillingChildAdapter(private val dataSet: ArrayList<BillingChildDataModel>, internal var context: Context,
    internal var billingChildClickListener: BillingChildClickListener) : ArrayAdapter<BillingChildDataModel>(
    context, R.layout.billing_child_layout, dataSet) {
  private var lastPosition = -1

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    var convertView = convertView
    // Get the data item for this position
    // Check if an existing view is being reused, otherwise inflate the view
    val viewHolder: ViewHolder // view lookup cache stored in tag

    val result: View

    if (convertView == null) {

      viewHolder = ViewHolder()
      val inflater = LayoutInflater.from(getContext())
      convertView = inflater.inflate(R.layout.billing_child_layout, parent, false)

      convertView!!.setOnClickListener { v ->
        //        billingChildClickListener.billingChildClicked(viewHolder.txtName?.text.toString(), viewHolder.txtId?.text.toString(), position)

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(R.layout.layout_records_bottom_sheet)
        val attenRecordsLv = dialog.findViewById(R.id.lv_atten_records) as BottomSheetListView?
        val adapter = MySimpleArrayAdapter(context, android.R.layout.simple_list_item_1, prepairData(dataSet[0].attendanceRecord))
        attenRecordsLv?.adapter = adapter
        dialog.show()
      }

      viewHolder.txtName = convertView.child_name_tv
      viewHolder.txtId = convertView.child_id_tv

      result = convertView

      convertView.tag = viewHolder
    } else {
      viewHolder = convertView.tag as ViewHolder
      result = convertView
    }

    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    result.startAnimation(animation)
    lastPosition = position

    viewHolder.txtName!!.text = dataSet[position].firstName
    viewHolder.txtId!!.text = dataSet[position].childId
    // Return the completed view to render on screen
    return convertView
  }

  private fun prepairData(records: ArrayList<AttendanceRecord>): ArrayList<String> {

    val list = ArrayList<String>()


    for (record in records) {
      list.add(record.checkInTime + record.checkOutTime)
    }
    return list
  }

  // View lookup cache
  private class ViewHolder {
    internal var txtName: TextView? = null
    internal var txtId: TextView? = null
  }

  interface BillingChildClickListener {
    fun billingChildClicked(txtName: String, txtId: String, position: Int)
  }
}



