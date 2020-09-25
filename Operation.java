package edu.mit.vkwang;

import android.annotation.SuppressLint;
import android.media.Image;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Operation extends AppCompatActivity{

    private int resID;
    private String type;

    @SuppressLint("ClickableViewAccessibility")
    public Operation(int resID, String type){
        this.resID = resID;
        this.type = type;
    }

    public void addToOpSlot(OpSlot opSlot){
        if(opSlot != null){
            opSlot.setOp(this);
        }
    }

    public int getResID(){
        return resID;
    }

    public String getType(){
        return type;
    }
}