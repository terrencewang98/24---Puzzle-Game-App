package edu.mit.vkwang;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class OpSlot {

    private ImageView img;
    private int resID;
    private Operation op;
    private float height;
    private float width;
    private float x;
    private float y;
    private float startX;
    private float startY;
    private float xCoOrdinate;
    private float yCoOrdinate;

    @SuppressLint("ClickableViewAccessibility")
    public OpSlot(final ImageView img, int resID){
            this.img = img;
            this.resID = resID;
            op = null;
            img.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    height = img.getHeight();
                    width = img.getWidth();
                    x = img.getLeft();
                    y = img.getTop();

                    img.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
    }

    public ImageView getImg(){
        return img;
    }

    public void setOp(Operation toSet){
        op = toSet;
        if(op != null){
            int opResID = op.getResID();
            img.setImageResource(opResID);
        }
        else{
            img.setImageResource(resID);
        }
    }

    public Operation getOp(){
        return op;
    }

    public float[] getDimensions(){
        return new float[]{height, width, x, y};
    }
}
