package com.kariaki.choice.ui.DialogBox;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.R;
import com.kariaki.choice.ui.DialogBox.ListAdapter.MultiChoiceAdapter;
import com.kariaki.choice.ui.util.Theme;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ChoiceDialogBoxMultiChoice extends DialogFragment {

    RadioGroup radioGroup;
    AppCompatRadioButton choice2,choice1,choice3;

    List<String> text=new ArrayList<>();
    RecyclerView choiceList;
    MultiChoiceAdapter adapter;
    TextView dialogCancelProcess,choiceDialogConfirmProcess,choiceDialogTittle;
    String tittle="";
    RelativeLayout rootView;

    public void setText(List<String> text) {
        this.text = text;
    }
    RadioButton[]radioList;
    private int selected;

    public void setSelected(int selected) {
        this.selected = selected;
    }
    private int color= R.color.textContext;

    public void setColor(int color) {
        this.color = color;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public interface onItemClickListener{
        void onItemClick(int position);
        void onPositiveButtonClick();
        void onNegativeButtonClick();
    }
    private onItemClickListener itemClickListener;

    public void setItemClickListener(onItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View contentView=View.inflate(getContext(), R.layout.choice_dialog_box_multichoice,null);
        dialog.setContentView(contentView);
        ( (View)contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        adapter=new MultiChoiceAdapter();
        choice1=contentView.findViewById(R.id.choice1);
        choice2=contentView.findViewById(R.id.choice2);
        choiceDialogTittle=contentView.findViewById(R.id.choiceDialogTittle);
        choiceDialogConfirmProcess=contentView.findViewById(R.id.choiceDialogConfirmProcess);
        dialogCancelProcess=contentView.findViewById(R.id.dialogCancelProcess);
        rootView=contentView.findViewById(R.id.rootView);
        choice3=contentView.findViewById(R.id.choice3);
        choiceList=contentView.findViewById(R.id.choiceList);
        radioGroup=contentView.findViewById(R.id.radioGroup);
        choiceDialogTittle.setText(tittle);

        radioList=new RadioButton[]{choice1,choice2,choice3};

        radioList[selected].setChecked(true);

        adapter.setChoices(text);
        adapter.setColor(color);
        adapter.setOnItemClickListener(new MultiChoiceAdapter.onItemClickListener() {
            @Override
            public void itemClicked(int position) {
                itemClickListener.onItemClick(position);
                radioList[position].setChecked(true);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        choiceDialogConfirmProcess.setOnClickListener(v->{
            itemClickListener.onPositiveButtonClick();
        });

        dialogCancelProcess.setOnClickListener(v->{
            itemClickListener.onNegativeButtonClick();
        });

        choiceList.setLayoutManager(new LinearLayoutManager(getContext()));
        choiceList.setAdapter(adapter);

        loadTheme();



    }

    public void setTextColors( int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:
                rootView.setBackground(getResources().getDrawable(R.drawable.full_curve));
                choiceDialogTittle.setTextColor(getContext().getResources().getColor(R.color.textHeaderColor));
                break;
            case Theme.DEEP:

                rootView.setBackground(getResources().getDrawable(R.drawable.full_curve_dark));
                choiceDialogTittle.setTextColor(getContext().getResources().getColor(R.color.whiteGreen));
                break;

        }
    }
    private void loadTheme(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("themes",MODE_PRIVATE);

        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.DEEP);

        setTextColors(currentTheme);


    }
}
