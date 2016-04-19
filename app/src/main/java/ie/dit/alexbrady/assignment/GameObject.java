package ie.dit.alexbrady.assignment;

import android.graphics.Rect;
//gameObject class which a few classes will 'extend' with
public abstract class GameObject
{
    //declare variables
    protected int x;
    protected int y;
    protected int dy;
    protected int dx;
    protected int width;
    protected int height;


    public void setY(int y)
    {
        this.y = y;
    }// end setY
    public int getX()
    {
        return x;
    }//end getX
    public int getY()
    {
        return y;
    }//end getY
    public int getHeight()
    {
        return height;
    }//end getHeight

    public Rect getRectangle()
    {
        return new Rect(x, y, x+width, y+height);
    }//end getRectangle

}