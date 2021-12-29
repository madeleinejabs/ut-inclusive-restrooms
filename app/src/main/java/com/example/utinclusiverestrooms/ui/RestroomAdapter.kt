package com.example.utinclusiverestrooms.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.utinclusiverestrooms.MainApplication
import com.example.utinclusiverestrooms.R
import com.example.utinclusiverestrooms.data.Restroom

class RestroomAdapter(private var dataSet: List<Restroom>) :
    RecyclerView.Adapter<RestroomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameText)
        val roomTextView: TextView = view.findViewById(R.id.roomText)
        val distanceTextView: TextView = view.findViewById(R.id.distanceText)
        val directionsButton: ImageButton = view.findViewById(R.id.directionsButton)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.restroom_frame, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.nameTextView.text = dataSet[position].Name
        viewHolder.roomTextView.text = MainApplication.applicationContext().getString(R.string.room_number, dataSet[position].Building_Abbr, dataSet[position].Room_Number)
        viewHolder.distanceTextView.text = MainApplication.applicationContext().getString(R.string.distance, dataSet[position].distanceTo)

        // When the user clicks on the Google Maps button, open up Google Maps navigation
        viewHolder.directionsButton.setOnClickListener {
            // Second condition handles incorrect main tower address
            val gmmIntentUri: Uri = if (dataSet[position].Address_Full != "" && dataSet[position].Address_Full != "W 24th St, Austin, TX 78705") {
                Uri.parse("google.navigation:q=" + dataSet[position].Address_Full + "&mode=w")
            } else {
                Uri.parse("google.navigation:q=" + dataSet[position].Name + " Austin, TX" + "&mode=w")
            }
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(viewHolder.directionsButton.context, mapIntent, null)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun setData(newDataSet: List<Restroom>) {
        dataSet = newDataSet
    }
}