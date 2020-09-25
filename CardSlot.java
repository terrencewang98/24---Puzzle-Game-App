package edu.mit.vkwang;

import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class CardSlot {

    private Card card;
    private float height;
    private float width;
    private float x;
    private float y;

    public CardSlot(final ImageView cardSlot){
        card = null;
        cardSlot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                height = cardSlot.getHeight();
                width = cardSlot.getWidth();
                x = cardSlot.getLeft();
                y = cardSlot.getTop();

                cardSlot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setCard(Card toSet){
        card = toSet;
    }

    public Card getCard(){
        return card;
    }

    public float[] getDimensions(){
        return new float[]{height, width, x, y};
    }

}
