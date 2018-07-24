package com.vince.childcare.core.registration

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vince.childcare.R

class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var parentCardView: CardView = itemView.findViewById(R.id.parentDataCardView)

  companion object {

    fun create(context: Context, parent: ViewGroup): ParentViewHolder {
      return ParentViewHolder(LayoutInflater.from(context).inflate(R.layout.registration_parent_data_card, parent, false))
    }

    fun bind(holder: ParentViewHolder, cardItem: Parent, listener: RegistrationAdapter.CardItemListener?) {
      holder.parentCardView.setOnClickListener {
        listener?.onParentCardClicked("Parent Card Clicked")
      }
    }
  }

}
