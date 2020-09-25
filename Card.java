package edu.mit.vkwang;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Card extends AppCompatActivity{

    private ImageView img;
    private int value;
    private CardSlot cardSlot;
    private boolean firstTouch;
    private float startX;
    private float startY;
    private float oldCardSlotX;
    private float oldCardSlotY;

    public Card(ImageView img, int value){
        this.img = img;
        this.value = value;
        cardSlot = null;
        firstTouch = true;
    }

    public void addToCardSlot(CardSlot cardSlot){
        Card toSwap = cardSlot.getCard();
        if(toSwap != null) {
            if(this.cardSlot != null){
                toSwap.setPosition(oldCardSlotX, oldCardSlotY);
                toSwap.setCardSlot(this.cardSlot);
                this.cardSlot.setCard(toSwap);
            }
            else{
                toSwap.resetPosition();
                toSwap.setCardSlot(null);
            }
        }
        else{
            if(this.cardSlot != null) {
                this.cardSlot.setCard(null);
            }
        }
        cardSlot.setCard(this);
        setCardSlot(cardSlot);
    }

    public void setPosition(float x, float y){
        img.animate().x(x);
        img.animate().y(y);
    }
    public void resetPosition(){
        img.animate().x(startX);
        img.animate().y(startY);
    }

    public ImageView getImg(){
        return img;
    }

    public int getValue(){
        return value;
    }

    public CardSlot getCardSlot(){
        return cardSlot;
    }

    public void setCardSlot(CardSlot cardSlot){
        this.cardSlot = cardSlot;
    }

    public boolean getFirstTouch(){
        return firstTouch;
    }

    public void setFirstTouch(boolean b){
        firstTouch = b;
    }

    public float getStartX(){
        return startX;
    }

    public float getStartY(){
        return startY;
    }

    public void setStartPos(float x, float y){
        startX = x;
        startY = y;
    }

    public void setOldCardSlotPos(float x, float y){
        oldCardSlotX = x;
        oldCardSlotY = y;
    }
}