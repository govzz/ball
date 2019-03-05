package com.example.ball;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView blue;
    private ImageView orange;
    private ImageView pink;
    private ImageView black;

    //Size
    private int frameHeight;
    private int blueSize;
    private int screenWidth;
    private int screenHeight;

    //Position
    private int blueY;
    private int orangeX;
    private int orangeY;
    private int pinkX;
    private int pinkY;
    private int blackX;
    private int blackY;

    //Speed
    private int blueSpeed;
    private int orangeSpeed;
    private int pinkSpeed;
    private int blackSpeed;

    //Score
    private int score = 0;

    //Initialize Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    //Status Check
    private boolean action_flg = false;
    private boolean start_flg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       sound = new SoundPlayer(this);

        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);
        blue = (ImageView) findViewById(R.id.blue);
        orange = (ImageView) findViewById(R.id.orange);
        pink = (ImageView) findViewById(R.id.pink);
        black = (ImageView) findViewById(R.id.black);

        //get screen size
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenHeight = size.y;
        screenWidth = size.x;



        blueSpeed = Math.round(screenHeight / 60F); //1184 / 60 = 19.7333 >= 20
        orangeSpeed = Math.round(screenWidth / 60F);//768 / 60 = 12.8 >= 13
        pinkSpeed = Math.round(screenWidth / 36F);  //768 / 36 = 21.333 >= 21
        blackSpeed = Math.round(screenWidth / 45F); //768 / 45 = 17.066 >= 17

        orange.setX(-80);
        orange.setY(-80);
        pink.setX(-80);
        pink.setY(-80);
        black.setX(-80);
        black.setY(-80);

        scoreLabel.setText("Score : 0");


    }

    public void changePos(){
        hitCheck();
        //orange
        orangeX -= orangeSpeed;
        if(orangeX < 0){
            orangeX = screenWidth + 20;
            orangeY = (int)Math.floor(Math.random()*(frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //black
        blackX -= blackSpeed;
        if(blackX < 0){
            blackX = screenWidth + 10;
            blackY = (int)Math.floor(Math.random()*(frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        //pink
        pinkX -= pinkSpeed;
        if (pinkX < 0){
            pinkX = screenWidth + 3000;
            pinkY = (int)Math.floor(Math.random()*(frameHeight - pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        //Move blue
        if(action_flg == true){
            //Touching
            blueY -=blueSpeed;
        }else{
            //Releasing
            blueY +=blueSpeed;
        }

        //Check blue position
        if (blueY < 0)blueY = 0;

        if (blueY > frameHeight - blueSize)blueY = frameHeight - blueSize;
        blue.setY(blueY);

        scoreLabel.setText("Score : " + score);
    }

    public void hitCheck(){
        //if the center of the ball is in the box,it counts as a hit

        //orange
        int orangeCenterX = orangeX + orange.getWidth() / 2;
        int orangeCenterY = orangeY + orange.getHeight() / 2;

        //0<= orangeCenterX <= blueWidth
        //blueY <= orangeCenterY <= blueY + blueHeight

        if(0 <= orangeCenterX && orangeCenterX <= blueSize &&
                blueY <= orangeCenterY && orangeCenterY <= blueY + blueSize){
            score += 10;
            orangeX = -10;
            sound.playHitSound();
        }

        //pink
        int pinkCenterX = pinkX + pink.getWidth() / 2;
        int pinkCenterY = pinkY + pink.getHeight() / 2;

        if(0 <= pinkCenterX && pinkCenterX <= blueSize &&
                blueY <= pinkCenterY && pinkCenterY <= blueY + blueSize){
            score += 30;
            pinkX = -10;
            sound.playHitSound();
        }

        //black
        int blackCenterX = blackX + black.getWidth() / 2;
        int blackCenterY = blackY + black.getHeight() / 2;

        if(0 <= blackCenterX && blackCenterX <= blueSize &&
                blueY <= blackCenterY && blackCenterY <= blueY + blueSize){
            //stop timer
            timer.cancel();
            timer = null;

            sound.playOverSound();

            //show result
            Intent intent = new Intent(getApplicationContext(),result.class);
            intent.putExtra("SCORE",score);
            startActivity(intent);

        }
    }

    public boolean onTouchEvent(MotionEvent me){
        if (start_flg == false){

            start_flg = true;
            //The UI has not been set on the screen in OnCreate()

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            blueY = (int)blue.getY();

            //the blue is a square.(height and width are the same)
            blueSize = blue.getHeight();
            startLabel.setVisibility(View.GONE);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            },0,20);

        }else {
            if(me.getAction() == MotionEvent.ACTION_DOWN){
                action_flg = true;
            }else if (me.getAction() == MotionEvent.ACTION_UP){
                action_flg = false;
            }
        }


        return true;
    }

    //Disable Return Button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){

        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
