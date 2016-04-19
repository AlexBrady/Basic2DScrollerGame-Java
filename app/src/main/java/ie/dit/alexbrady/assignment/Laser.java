package ie.dit.alexbrady.assignment;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Laser extends GameObject
{
    private int speed;
    private Bitmap image;

    public Laser(Bitmap res, int x, int y, int w, int h, int s)
    {
        super.x = x;
        super.y = y;
        width = w;
        height = h;

        speed = 15;
        if(speed>40)
        {
            speed = 40;
        }

        image = Bitmap.createBitmap(res, 0, 0, width, height);


    }
    public void update()
    {
        x += speed;
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

}