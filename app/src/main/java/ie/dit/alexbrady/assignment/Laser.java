package ie.dit.alexbrady.assignment;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Laser extends GameObject {
    private int speed;
    private Bitmap image;

    public Laser(Bitmap res, int x, int y, int w, int h, int s) {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        //speed of laser bullets
        speed = 10;
        if (speed > 20)
        {
            speed = 20;
        }

        image = Bitmap.createBitmap(res, 0, 0, width, height);


    }

    public void update() {
        x += speed;
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(image, x, y, null);
        } catch (Exception e) {
        }

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
