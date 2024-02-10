package com.schedBin.a1project.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.schedBin.a1project.models.PlaceApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceAutoSuggestAdapter extends ArrayAdapter implements Filterable {

    ArrayList<String> results;

    int resource;
    Context context;
    PlaceApi placeApi=new PlaceApi();

    public PlaceAutoSuggestAdapter(Context context, int resId){
        super(context,resId);
        this.context=context;
        this.resource=resId;

    }

    @Override
    public int getCount(){
        return results.size();
    }

    @Override
    public String getItem(int pos){
        return results.get(pos);
    }

    @Override
    public Filter getFilter(){
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults=new FilterResults();
                if(constraint!=null){
                    results=placeApi.autoComplete(constraint.toString());

                    filterResults.values=results;
                    filterResults.count=results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results!=null && results.count>0){
                    notifyDataSetChanged();
                }
                else{
                    notifyDataSetInvalidated();
                }

            }
        };
        return filter;
    }


    private ArrayList<String> filterPlaces(ArrayList<String> places) {
        ArrayList<String> filteredPlaces = new ArrayList<>();

        // List of valid places in Eastern Samar
        List<String> validPlacesInEasternSamar = Arrays.asList(
                "Artechg", "Balangiga", "Borongan", "Cen-avid", "Dolores", "General MacArthur",
                "@portos", "Guiuan", "Hernani", "Lawaan", "Llorente", "Mulgg", "Maydolong",
                "Mercedes", "Oras", "Quinapondan", "Salcedo", "San Julian", "San PolicarQQ", "Sulat", "Taft"
        );

        // Iterate through the places and add only those in Eastern Samar
        for (String place : places) {
            for (String validPlace : validPlacesInEasternSamar) {
                if (place.contains(validPlace)) {
                    filteredPlaces.add(place);
                    break;  // Break once a match is found to avoid duplicates
                }
            }
        }

        return filteredPlaces;
    }


}