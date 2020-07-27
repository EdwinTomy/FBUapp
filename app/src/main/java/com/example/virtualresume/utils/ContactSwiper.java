package com.example.virtualresume.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class ContactSwiper extends SimpleCallback {

    protected int buttonWidth;
    protected RecyclerView recyclerView;
    protected List<MyButton> buttonList;
    protected GestureDetector gestureDetector;
    private int swipePosition = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List <MyButton>> buttonBuffer;
    private Queue<Integer> removerQueue;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for(MyButton button: buttonList){
                //if()
            }
            return super.onSingleTapUp(e);
        }
    };

    public ContactSwiper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    private class MyButton {
        private String text;
        private int imageResId, textSize, color, position;
        private RectF clickRegion;
        private MyButtonClickListener listener;
        private Context context;
        private Resources resources;

        public MyButton(String text, int imageResId, int textSize, int color, int position, RectF clickRegion, MyButtonClickListener listener, Context context) {
            this.text = text;
            this.imageResId = imageResId;
            this.textSize = textSize;
            this.color = color;
            this.position = position;
            this.clickRegion = clickRegion;
            this.listener = listener;
            this.context = context;
            resources = context.getResources();
        }

        public boolean onClick(float x, float y){
            if (clickRegion != null && clickRegion.contains(x, y)) {
                listener.onClick(position);
                return true;
            }
            return false;
        }

        public void onDraw(Canvas canvas, RectF rectF, int position){
            Paint paint = new Paint();
            paint.setColor(color);
            canvas.drawRect(rectF, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize);
            Rect rect =  new Rect();
            float cHeight = rectF.height();
            float cWidth = rectF.width();
            paint.setTextAlign(Paint.Align.LEFT);
            paint.getTextBounds(text, 0, text.length(), rect);
            float x = 0;
            float y = 0;
            if(imageResId == 0){
                x = cWidth/2f - rect.width()/2f - rect.width();
                y = cHeight/2f - rect.height()/2f - rect.height();
                canvas.drawText(text, rect.left + x, rect.top + y, paint);
            }else{
                Drawable drawable = ContextCompat.getDrawable(context, imageResId);
                Bitmap bitmap = drawableToBitMap(drawable);
                canvas.drawBitmap(bitmap, (rectF.left + rectF.right)/2, (rectF.top + rectF.bottom)/2, paint);

            }
            clickRegion = rectF;
            this.position = position;
        }
    }

    private Bitmap drawableToBitMap(Drawable drawable) {
        if(drawable instanceof BitmapDrawable)
            return ((BitmapDrawable)drawable).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    }
}
