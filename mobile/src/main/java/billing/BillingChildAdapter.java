package billing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.vince.childcare.R;
import java.util.ArrayList;

public class BillingChildAdapter extends ArrayAdapter<BillingChildDataModel> implements View.OnClickListener {

  Context context;
  private ArrayList<BillingChildDataModel> dataSet;
  private int lastPosition = -1;

  public BillingChildAdapter(ArrayList<BillingChildDataModel> data, Context context) {
    super(context, R.layout.billing_child_layout, data);
    this.dataSet = data;
    this.context = context;
  }

  @Override public void onClick(View v) {

    int position = (Integer) v.getTag();
    Object object = getItem(position);
    BillingChildDataModel billingChildDataModel = (BillingChildDataModel) object;

    //switch (v.getId())
    //{
    //case R.id.item_info:
    //  Snackbar.make(v, "Release date " + billingChildDataModel.getFeature(), Snackbar.LENGTH_LONG)
    //      .setAction("No action", null).show();
    //  break;
    //}
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    // Get the data item for this position
    BillingChildDataModel billingChildDataModel = getItem(position);
    // Check if an existing view is being reused, otherwise inflate the view
    ViewHolder viewHolder; // view lookup cache stored in tag

    final View result;

    if (convertView == null) {

      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.billing_child_layout, parent, false);
      viewHolder.txtName = convertView.findViewById(R.id.child_name_tv);
      viewHolder.txtId = convertView.findViewById(R.id.child_id_tv);

      result = convertView;

      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
      result = convertView;
    }

    Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
    result.startAnimation(animation);
    lastPosition = position;

    viewHolder.txtName.setText(dataSet.get(position).getFirstName());
    viewHolder.txtId.setText(dataSet.get(position).getChildId());
    // Return the completed view to render on screen
    return convertView;
  }

  // View lookup cache
  private static class ViewHolder {
    TextView txtName;
    TextView txtId;
  }
}

