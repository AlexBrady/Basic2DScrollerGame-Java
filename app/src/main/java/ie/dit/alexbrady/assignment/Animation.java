package ie.dit.alexbrady.assignment;

import android.graphics.Bitmap;
//class for the animations(explosions ect.)
public class Animation
{
    //declare variables
    private Bitmap[] frames;
    private int currentFrame;
    private long startTime;
    private long delay;
    private boolean playedOnce; //some animations only played once
    //make sure the frames are correct
    public void setFrames(Bitmap[] frames)
    {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
    }// end setFrames
    public void setDelay(long d)
    {
        delay = d;
    }//end setDelay

    public void update()
    {
        long elapsed = (System.nanoTime() - startTime) / 1000000;

        if(elapsed>delay)
        {
            currentFrame++;
            startTime = System.nanoTime();
        }//end if
        if(currentFrame == frames.length)
        {
            currentFrame = 0;
            playedOnce = true;
        }//end if
    }
    public Bitmap getImage()
    {
        return frames[currentFrame];
    }// end getImage

    public boolean playedOnce()
    {
        return playedOnce;
    }//end playedOnce
}//end class Animation