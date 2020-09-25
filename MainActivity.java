package edu.mit.vkwang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout screen;
    ArrayList<String> combos;
    CardSlot[] cardSlots;
    Card[] cards;
    OpSlot[] opSlots;
    Operation[] ops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            combos = savedInstanceState.getStringArrayList("combos");
        }
        else{
            readComboFile();
        }
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        screen = (ConstraintLayout) findViewById(R.id.screen);

        initCardSlots();
        initCards();
        initOpSlots();
        initOps();

        ImageView tooltipTop = findViewById(R.id.tooltipTopBorder);
        ImageView tooltipBot = findViewById(R.id.tooltipBotBorder);
        tooltipTop.setImageAlpha(0);
        tooltipBot.setImageAlpha(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putStringArrayList("combos", combos);
        super.onSaveInstanceState(outState);
    }

    public void checkWin(){

        for(CardSlot s : cardSlots){
            if(s.getCard() == null){
                return;
            }
        }

        for(OpSlot s : opSlots){
            if(s.getOp() == null){
                return;
            }
        }

        int result = cardSlots[0].getCard().getValue();
        for(int i = 0; i < opSlots.length; i++){
            String type = opSlots[i].getOp().getType();
            int value =  cardSlots[i + 1].getCard().getValue();
            if(type.equals("+")){
                result += value;
            }
            else if(type.equals("-")){
                result -= value;
            }
            else if(type.equals("*")){
                result *= value;
            }
            else{
                result /= value;
            }
        }

        if(result == 24){
            startActivity(new Intent(this, EndScreen.class));
        }
    }

    public void readComboFile(){
        String string = "";
        combos = new ArrayList<String>();
        InputStream is = this.getResources().openRawResource(R.raw.combinations);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((string = reader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            combos.add(string);
        }
    }

    public void initCardSlots(){
        cardSlots = new CardSlot[4];
        cardSlots[0] = new CardSlot((ImageView) findViewById(R.id.cardSlot1));
        cardSlots[1] = new CardSlot((ImageView) findViewById(R.id.cardSlot2));
        cardSlots[2] = new CardSlot((ImageView) findViewById(R.id.cardSlot3));
        cardSlots[3] = new CardSlot((ImageView) findViewById(R.id.cardSlot4));
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initCards(){
        Collections.shuffle(combos);
        String comboStr = combos.remove(0);
        combos.add(comboStr);

        String[] cardNums = comboStr.split("\\s+");
        String[] suits = new String[]{"s","c","d","h"};
        String[] values = new String[]{"a", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};

        cards = new Card[4];
        for(int i = 0; i < cards.length; i++){
            String suit = suits[(int)(Math.random() * suits.length)];
            String cardID = cardNums[i] + suit;
            int resID = getResources().getIdentifier(cardID, "drawable", getPackageName());

            ImageView image = null;
            if(i == 0){
                image = (ImageView) findViewById(R.id.card1);
            }
            else if(i == 1){
                image = (ImageView) findViewById(R.id.card2);
            }
            else if(i == 2){
                image = (ImageView) findViewById(R.id.card3);
            }
            else{
                image = (ImageView) findViewById(R.id.card4);
            }

            image.setImageResource(resID);

            int value = -1;
            for(int j = 0; j < values.length; j++){
                if(cardNums[i].equals(values[j])){
                    value = j + 1;
                    break;
                }
            }

            final Card card = new Card(image, value);
            cards[i] = card;

            card.getImg().setOnTouchListener(new View.OnTouchListener() {
                float xCoOrdinate = 0;
                float yCoOrdinate = 0;

                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            if(card.getFirstTouch()){
                                card.setStartPos(view.getX(), view.getY());
                                card.setFirstTouch(false);
                            }
                            if(card.getCardSlot() != null){
                                card.setOldCardSlotPos(view.getX(), view.getY());
                            }
                            xCoOrdinate = view.getX() - event.getRawX();
                            yCoOrdinate = view.getY() - event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            view.animate().x(event.getRawX() + xCoOrdinate);
                            view.animate().y(event.getRawY() + yCoOrdinate);
                            view.animate().setDuration(0);
                            view.animate().start();
                            break;
                        case MotionEvent.ACTION_UP:
                            float endX = card.getStartX();
                            float endY = card.getStartY();
                            float eventX = event.getRawX();
                            float eventY = event.getRawY();
                            CardSlot newCardSlot = null;

                            for(CardSlot s : cardSlots){
                                float cardSlotHeight = s.getDimensions()[0];
                                float cardSlotWidth = s.getDimensions()[1];
                                float cardSlotX = s.getDimensions()[2];
                                float cardSlotY = s.getDimensions()[3];
                                int offset = 250;
                                if(eventX > cardSlotX && eventX < cardSlotX + cardSlotWidth && eventY > cardSlotY - offset && eventY < cardSlotY + cardSlotHeight + offset){
                                    newCardSlot = s;
                                    endX = cardSlotX;
                                    endY = cardSlotY;
                                    break;
                                }
                            }

                            if(newCardSlot != null) {
                                card.addToCardSlot(newCardSlot);
                            }
                            else{
                                if(card.getCardSlot() != null) {
                                    card.getCardSlot().setCard(null);
                                }
                                card.setCardSlot(null);
                            }

                            view.animate().x(endX);
                            view.animate().y(endY);

                            checkWin();
                    }
                    return true;
                }
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initOpSlots(){
        int resID = getResources().getIdentifier("emptyop", "drawable", getPackageName());
        opSlots = new OpSlot[3];
        opSlots[0] = new OpSlot((ImageView) findViewById(R.id.opSlot1), resID);
        opSlots[1] = new OpSlot((ImageView) findViewById(R.id.opSlot2), resID);
        opSlots[2] = new OpSlot((ImageView) findViewById(R.id.opSlot3), resID);

        for(final OpSlot opSlot : opSlots){

            opSlot.getImg().setOnTouchListener(new View.OnTouchListener(){
                float startX = 0;
                float startY = 0;
                float xCoOrdinate = 0;
                float yCoOrdinate = 0;

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if(opSlot.getOp() != null) {
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_DOWN:
                                startX = view.getX();
                                startY = view.getY();
                                xCoOrdinate = view.getX() - event.getRawX();
                                yCoOrdinate = view.getY() - event.getRawY();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                view.animate().x(event.getRawX() + xCoOrdinate);
                                view.animate().y(event.getRawY() + yCoOrdinate);
                                view.animate().setDuration(0);
                                view.animate().start();
                                break;
                            case MotionEvent.ACTION_UP:
                                float eventX = event.getRawX();
                                float eventY = event.getRawY();

                                OpSlot newOpSlot = null;
                                Operation opToSet = null;
                                int offset = 150;
                                for(OpSlot s : opSlots){
                                    float[] dimensions = s.getDimensions();
                                    float height = dimensions[0];
                                    float width = dimensions[1];
                                    float x = dimensions[2];
                                    float y = dimensions[3];

                                    if ((eventX > x - offset && eventX < x + width + offset && eventY > y - offset && eventY < y + height + offset)) {
                                        newOpSlot = s;
                                        opToSet = opSlot.getOp();
                                        break;
                                    }
                                }

                                opSlot.setOp(null);

                                if(newOpSlot != null) {
                                    if(newOpSlot.getOp() != null){
                                        opSlot.setOp(newOpSlot.getOp());
                                    }
                                    newOpSlot.setOp(opToSet);
                                }

                                view.animate().x(startX);
                                view.animate().y(startY);

                                checkWin();
                        }
                    }
                    return true;
                }
            });
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    public void initOps(){
        ops = new Operation[4];

        String[] types = new String[]{"+", "*", "-", "/"};
        for(int i = 0; i < ops.length; i++){
            String opID = null;
            ImageView img = null;
            if(i == 0){
                opID = "plus";
                img = (ImageView) findViewById(R.id.op1);
            }
            else if(i == 1){
                opID = "times";
                img = (ImageView) findViewById(R.id.op2);
            }
            else if(i == 2){
                opID = "minus";
                img = (ImageView) findViewById(R.id.op3);
            }
            else{
                opID = "divide";
                img = (ImageView) findViewById(R.id.op4);
            }

            int resID = getResources().getIdentifier(opID, "drawable", getPackageName());
            final Operation op = new Operation(resID, types[i]);
            ops[i] = op;

            img.setOnTouchListener(new View.OnTouchListener() {
                float startX = 0;
                float startY = 0;
                float xCoOrdinate = 0;
                float yCoOrdinate = 0;

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = view.getX();
                            startY = view.getY();
                            xCoOrdinate = view.getX() - event.getRawX();
                            yCoOrdinate = view.getY() - event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            view.animate().x(event.getRawX() + xCoOrdinate);
                            view.animate().y(event.getRawY() + yCoOrdinate);
                            view.animate().setDuration(0);
                            view.animate().start();
                            break;
                        case MotionEvent.ACTION_UP:
                            float eventX = event.getRawX();
                            float eventY = event.getRawY();
                            OpSlot newOpSlot = null;
                            int offset = 150;
                            for(OpSlot s : opSlots){
                                float opSlotHeight = s.getDimensions()[0];
                                float opSlotWidth = s.getDimensions()[1];
                                float opSlotX = s.getDimensions()[2];
                                float opSlotY = s.getDimensions()[3];

                                if(eventX > opSlotX - offset && eventX < opSlotX + opSlotWidth + offset && eventY > opSlotY - offset && eventY < opSlotY + opSlotHeight + offset){
                                    newOpSlot = s;
                                    break;
                                }
                            }

                            op.addToOpSlot(newOpSlot);

                            view.animate().x(startX);
                            view.animate().y(startY);

                            checkWin();
                    }
                    return true;
                }
            });
        }
    }

    public void sendMessage(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void backButton(View view){
        Intent intent = new Intent(this, StartScreen.class);
        startActivity(intent);
    }

    public void helpButton(View view){
        TextView tooltip = findViewById(R.id.tooltip);
        ImageView tooltipTop = findViewById(R.id.tooltipTopBorder);
        ImageView tooltipBot = findViewById(R.id.tooltipBotBorder);
        if(tooltip.getAlpha() == 0){
            tooltip.setAlpha(255);
            tooltipTop.setImageAlpha(255);
            tooltipBot.setImageAlpha(255);
        }
        else {
            tooltip.setAlpha(0);
            tooltipTop.setImageAlpha(0);
            tooltipBot.setImageAlpha(0);
        }
    }
}
