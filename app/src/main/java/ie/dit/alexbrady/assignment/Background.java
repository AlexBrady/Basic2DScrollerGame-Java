package ie.dit.alexbrady.assignment;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background
{

    private Bitmap image;
    private int y = 0;
    private int x = 0;

    public Background(Bitmap res)
    {
        image = res;
    }

    public void update()
    {

    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
    }
}