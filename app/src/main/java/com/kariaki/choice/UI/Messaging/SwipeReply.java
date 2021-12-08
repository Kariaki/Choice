package com.kariaki.choice.UI.Messaging;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.R;


public abstract class SwipeReply extends ItemTouchHelper.Callback {


    private View mView;
    private Context context;
    private float dx=0f;
    private boolean swipBack=false;
    private boolean startTracking=false;
    private RecyclerView.ViewHolder currenViewHolder;
    private long lastReplyButtonAnimationTime=0;
    private float replyButtonProgress=0f;
    private Drawable shareRound;
    private Drawable imageDrawable;
    private boolean isVibrate=false;


    public SwipeReply(Context context){
        this.context=context;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        mView=viewHolder.itemView;
        shareRound=context.getDrawable(R.drawable.ic_round_shape);
        imageDrawable=context.getDrawable(R.drawable.ic_reply_black_24dp);
        return ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE,ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if(swipBack){
            swipBack=false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
       //
        if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE){
            setTouchListener(recyclerView,viewHolder);
        }

        if(mView.getTranslationX()<converToDp(130)||dX<this.dx){
            this.dx=dX;
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            startTracking=true;
        }
        currenViewHolder=viewHolder;
        drawReplyButton(c);

    }

    private void setTouchListener(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipBack=event.getAction()==MotionEvent.ACTION_CANCEL||event.getAction()==MotionEvent.ACTION_UP;
                if(swipBack){
                    if(Math.abs(mView.getTranslationX())>converToDp(100)){
                        swipeAction(viewHolder.getAdapterPosition());

                    }
                }
                return false;
            }
        });

    }



    private float converToDp(float x){
        return  x/(float)context.getResources().getDisplayMetrics().densityDpi/ DisplayMetrics.DENSITY_DEFAULT;
    }

    private void drawReplyButton(Canvas canvas){
        if(currenViewHolder==null){
            return;
        }
        float translationX=mView.getTranslationX();
        long newTime=System.currentTimeMillis();
        double dt=Math.min(17,newTime=lastReplyButtonAnimationTime);
        lastReplyButtonAnimationTime=newTime;
        boolean showing=translationX>=converToDp(30);
        if(showing){
            if(replyButtonProgress<1.0f){
                replyButtonProgress+=dt/(190.0f-10);
                if(replyButtonProgress>1.0f){
                    replyButtonProgress=1.0f;
                }else {
                    mView.invalidate();
                }
            }
        }else if(translationX<=0.0f){
            replyButtonProgress=0f;
            startTracking=false;
            isVibrate=false;

        }else {
            if(replyButtonProgress>0.0f){
                replyButtonProgress-=dt/(190f-10);
                if(replyButtonProgress<0.1f){
                    replyButtonProgress=0f;
                }else {
                    mView.invalidate();

                }
            }
        }

        int alpha;
        float scale;

        if(showing){
            if(replyButtonProgress<=0.9-0.1){
                scale= (float) (1.2f*(replyButtonProgress/(0.9-0.1f))/0.2f);
            }else {
                scale = (float) (1.2-0.2f*(replyButtonProgress-(0.9-1.0f))/0.2f);
            }
            alpha= (int) Math.min(255f,255f*replyButtonProgress/(0.9f-0.1));
        }else {
            scale=replyButtonProgress;
            alpha= (int) Math.min(255f,255f*replyButtonProgress);
        }
        shareRound.setAlpha(alpha);
        imageDrawable.setAlpha(alpha);

        /*
        if(startTracking){

            if(!isVibrate&&mView.getTranslationX()>=converToDp(100)){
                mView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                isVibrate=true;
            }
        }

         */
        int x=0;
        if(mView.getTranslationX()>converToDp(130)){
            x= (int) (converToDp(130)/2);
        }else {
            x= (int) (mView.getTranslationX()/2);
        }

        float y=(mView.getTop()+mView.getMeasuredHeight()/2);
        shareRound.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context,R.color.lineBackground),
                PorterDuff.Mode.MULTIPLY));


        shareRound.setBounds(
                (int)(x-converToDp(19-1)*scale),
                (int)(y-converToDp(19-1)*scale),
                (int)(x+converToDp(19-1)*scale),
                (int)(y+converToDp(19-1)*scale)
        );
        shareRound.draw(canvas);
        imageDrawable.setBounds(
                (int)(x-converToDp(12)*scale),
                (int)(y-converToDp(11)*scale),
                (int)(x+converToDp(12)*scale),
                (int)(y+converToDp(10)*scale)
        );
        imageDrawable.draw(canvas);
        shareRound.setAlpha(255);
        imageDrawable.setAlpha(255);


    }

    public abstract void swipeAction(int position);
}
