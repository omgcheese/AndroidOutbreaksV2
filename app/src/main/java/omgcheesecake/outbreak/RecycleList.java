package omgcheesecake.outbreak;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class RecycleList extends RecyclerView.Adapter<RecycleList.ViewHolder>{

    private ArrayList<HashMap<String, String>> arrayListRecent;
    private ArrayList<HashMap<String, Integer>> arrayListOther;
    private static String tabString;
    private static ClickListener clickListener;

    public interface ClickListener{
        void itemViewClicked(View v);
    }

    public RecycleList(ArrayList<HashMap<String, String>> arrayList){
        this.arrayListRecent = arrayList;
        this.tabString = "Recent";
    }

    public RecycleList(ArrayList<HashMap<String, Integer>> arrayList, String s){
        this.arrayListOther = arrayList;
        this.tabString = s;
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    //inflating layout from XML and returning the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;


        if(this.tabString.equals("Recent")){
            //custom layout
            view = inflater.inflate(R.layout.custom_row_recent, parent, false);
        }
        else if(this.tabString.equals("Most")){
            view = inflater.inflate(R.layout.custom_row_most, parent, false);
        }
        else {
            view = inflater.inflate(R.layout.custom_row_region, parent, false);
        }

        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    //Populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(this.tabString.equals("Recent")){
            //get the data model based on position
            HashMap<String, String> AL = this.arrayListRecent.get(position);

            //set item views based on the data model
            TextView textView0 = holder.virusname;
            TextView textView1 = holder.country;
            TextView textView2 = holder.date;

            textView0.setText(AL.get("virusname"));
            textView1.setText(AL.get("country"));
            textView2.setText(AL.get("lastupdated"));
        }
        else if(this.tabString.equals("Most")){
            //get the data model based on position
            HashMap<String, Integer> AL = this.arrayListOther.get(position);

            TextView textView0 = holder.virusname;
            TextView textView1 = holder.totalCount;

            textView0.setText(AL.keySet().iterator().next());
            textView1.setText(String.valueOf(AL.values().iterator().next()));
        }

        else{
            //get the data model based on position
            HashMap<String, Integer> AL = this.arrayListOther.get(position);

            TextView textView0 = holder.country;
            TextView textView1 = holder.totalCount;

            textView0.setText(AL.keySet().iterator().next());
            textView1.setText(String.valueOf(AL.values().iterator().next()));

        }

    }

    @Override
    public int getItemCount() {
        if(this.arrayListRecent != null){
            return this.arrayListRecent.size();
        }
        else{
            return this.arrayListOther.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView virusname;
        public TextView country;
        public TextView date;
        public TextView totalCount;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.breakLine);
            itemView.setOnClickListener(this);

            if(tabString.equals("Recent")){
                virusname = (TextView)itemView.findViewById(R.id.virusText);
                country = (TextView)itemView.findViewById(R.id.countryText);
                date = (TextView)itemView.findViewById(R.id.dateText);
            }
            else if(tabString.equals("Most")){
                virusname = (TextView)itemView.findViewById(R.id.virusText);
                totalCount = (TextView)itemView.findViewById(R.id.totalCountText);
            }
            else {
                country = (TextView)itemView.findViewById(R.id.countryText);
                totalCount = (TextView)itemView.findViewById(R.id.totalCountText);
            }

        }

        @Override
        public void onClick(View v) {
            if(tabString.equals("Recent") || tabString.equals("Region")){
                clickListener.itemViewClicked(v);
            }

        }
    }
}
