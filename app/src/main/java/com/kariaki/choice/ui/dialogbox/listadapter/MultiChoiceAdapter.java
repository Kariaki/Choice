package com.kariaki.choice.ui.DialogBox.ListAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.kariaki.choice.R;

import java.util.ArrayList;
import java.util.List;

public class MultiChoiceAdapter extends RecyclerView.Adapter<MultiChoiceAdapter.innerViewHolder> {

    List<String>choices=new ArrayList<>();
    private int color= R.color.textContext;

    public void setColor(int color) {
        this.color = color;
    }

    public interface onItemClickListener{
        void itemClicked(int position);
    }
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(MultiChoiceAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    @NonNull
    @Override
    public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_chocie_design,parent,false);

        return new innerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {
        holder.choiceItem.setText(choices.get(position));
        holder.choiceItem.setOnClickListener(v->{
            onItemClickListener.itemClicked(position);
        });

    }

    @Override
    public int getItemCount() {
        return choices.size();
    }

    public class innerViewHolder extends RecyclerView.ViewHolder{

        TextView choiceItem;
        public innerViewHolder(@NonNull View itemView) {
            super(itemView);
            choiceItem=itemView.findViewById(R.id.choiceItem);
            choiceItem.setTextColor(itemView.getContext().getColor(color));
        }
    }

}
