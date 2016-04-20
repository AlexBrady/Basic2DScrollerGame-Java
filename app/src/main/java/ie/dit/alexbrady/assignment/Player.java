package ie.dit.alexbrady.assignment;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Player extends GameObject
{
    private Bitmap image;
    private int score;



    private boolean up;
    private boolean playing;
    private long startTime;

    public Player(Bitmap res, int w, int h)
    {

        x = 100;
        y = GamePanel.HEIGHT / 2;
        dy = 0;
        score = 0;

        width = w;
        height = h;

        image = Bitmap.createBitmap(res, 0, 0, width, height);

        startTime = System.nanoTime();

    }

    public void setUp(boolean b)
    {
        up = b;
    }

    public void update()
    {
        long elapsed = (System.nanoTime()-startTime)/1000000;
        if(elapsed>300)
        {
            score++;
            startTime = System.nanoTime();
        }

        if(up)
        {
            dy -=1;
        }
        else
        {
            dy +=1;
        }

        if(dy>14)dy = 14;
        if(dy<-14)dy = -14;

        y += dy*2;

    }

    public void draw(Canvas canvas)
    {
        try
        {
            canvas.drawBitmap(image,x,y,null);
        }
        catch(Exception e)
        {
        }

    }



    public int getScore(){return score;}
    public void upScore() {score += 10;}
    public boolean getPlaying(){return playing;}
    public void setPlaying(boolean b){playing = b;}
    public void resetDY(){dy = 0;}
    public void resetScore(){score = 0;}
}