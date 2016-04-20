package ie.dit.alexbrady.assignment;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;



public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    //declare variables
    public static final int WIDTH = 852; //scale for background
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;
    private long pewStartTime;
    private long missileStartTime;
    private long laserStartTime;
    private MainThread thread;
    private Background spacebg;
    private Player player;
    private ArrayList<Laser> pew;
    private ArrayList<Missile> missiles;
    private ArrayList<TopBorder> topborder;
    private ArrayList<BotBorder> botborder;
    private Random rand = new Random();
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    private boolean newGameCreated;
    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean dissapear;
    private boolean started;
    private int best;

    //set lower for more difficulty
    private int progressDenom = 5;



    public GamePanel(Context context)
    {
        super(context);


        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);



        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }// end constructor GamePanel

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }//end surfaceChanged

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        int counter = 0; //prevent infinite loop
        while(retry && counter<1000)
        {
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;

            }catch(InterruptedException e){e.printStackTrace();}

        }//end while loop

    }//end surfaceDestroyed

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        //instantiating
        spacebg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.bg));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.ship),64,64);
        pew = new ArrayList<Laser>();
        missiles = new ArrayList<Missile>();
        topborder = new ArrayList<TopBorder>();
        botborder = new ArrayList<BotBorder>();
        laserStartTime =  System.nanoTime();
        missileStartTime = System.nanoTime();

        thread = new MainThread(getHolder(), this);
        //we can start the game loop
        thread.setRunning(true);
        thread.start();

    }//surfaceCreated
    @Override
    public boolean onTouchEvent(MotionEvent event)//if the screen is pressed, the spaceship goes up and if released, goes down
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            if(!player.getPlaying() && newGameCreated && reset)
            {
                player.setPlaying(true);
                player.setUp(true);
            }// end inner if
            if(player.getPlaying())
            {

                if(!started)started = true;
                reset = false;
                player.setUp(true);
            }//end inner if
            return true;
        }//end outer
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }//end if




        return super.onTouchEvent(event);
    }//end onTouchEvent

    public void update()

    {
        if(player.getPlaying())
        {

            if(botborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }
            if(topborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }

            spacebg.update();
            player.update();

            /*calculate the threshold of height the border can have based on the score max and min
            border heart are updated, and the border switched direction when either max or min is met*/

            maxBorderHeight = 30 + player.getScore() / progressDenom;
            //max border capped so it doesn't fill up loads of the screen
            if(maxBorderHeight > HEIGHT / 4)
            {
                maxBorderHeight = HEIGHT / 4;
                minBorderHeight = 5 + player.getScore() / progressDenom;
            }//end if

            //check border collisions (top and bottom)
            for(int i = 0; i < botborder.size(); i++)
            {
                if(collision(botborder.get(i), player))
                    player.setPlaying(false);
            }//end for
            for(int i = 0; i <topborder.size(); i++)
            {
                if(collision(topborder.get(i),player))
                    player.setPlaying(false);
            }//end for

            //add missiles on timer
            long missileElapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missileElapsed >(2000 - player.getScore()/4))
            {


                //first missile always down the middle
                if(missiles.size()==0)
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),WIDTH + 10, HEIGHT/2, 45, 15, player.getScore(), 13));
                }//end if
                else
                {

                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT - (maxBorderHeight * 2))+maxBorderHeight),45,15, player.getScore(),13));
                }//end else

                //reset timer
                missileStartTime = System.nanoTime();
            }//end if
            //loop through every missile and check collision/remove
            for(int i = 0; i<missiles.size();i++)
            {
                //update missile
                missiles.get(i).update();

                if(collision(missiles.get(i),player))
                {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }//end if
                //remove missile if it is way off the screen
                if(missiles.get(i).getX()<-100)
                {
                    missiles.remove(i);
                    break;
                }//end if
            }//end for

            //add bullets on timer
            long elapsed = (System.nanoTime() - pewStartTime)/1000000;
            if(elapsed > 600)
            {
                pew.add(new Laser(BitmapFactory.decodeResource(getResources(), R.drawable.
                        laser),player.getX(), player.getY(), 40, 40, player.getScore()));
                pewStartTime = System.nanoTime();
            }//end if

            for(int i = 0; i<pew.size();i++) {
                pew.get(i).update();

                for (int j = 0; j < missiles.size(); j++) {
                    pew.get(i).update();

                    if (collision(pew.get(i), missiles.get(j))) {
                        missiles.remove(j);
                        explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), pew.get(i).getX(),
                                pew.get(i).getY()-30, 100, 100, 25);
                        player.upScore();
                        break;
                    }//end if
                    explosion.update();
                    if (pew.get(i).getX() < -10) {
                        pew.remove(i);
                    }//end if
                    //end for
                }
            }
        }//end if(getPlaying)
        else
        {
            //reset if not playing
            player.resetDY();
            if(!reset)
            {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                dissapear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),player.getX(),
                        player.getY()-30, 100, 100, 25);
            }//end if

            explosion.update();
            long resetElapsed = (System.nanoTime() - startReset) / 1000000;

            if(resetElapsed > 2500 && !newGameCreated)
            {
                newGame();
            }//end if


        }//end else

    }//end update()

    public boolean collision(GameObject a, GameObject b) //collisions
    {
        if(Rect.intersects(a.getRectangle(), b.getRectangle()))
        {
            return true;
        }//end if
        return false;
    }
    @Override
    public void draw(Canvas canvas)
    {
        //scaling
        final float scaleFactorX = getWidth() / (WIDTH * 1.f);
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);

        if(canvas!=null)
        {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            spacebg.draw(canvas);//draw background
            if(!dissapear)
            {
                player.draw(canvas);
            }//end if
            //draw lasers
            for(Laser l: pew)
            {
                l.draw(canvas);
            }//end for
            //draw missiles
            for(Missile m: missiles)
            {
                m.draw(canvas);
            }//end for


            //draw topborder
            for(TopBorder tb: topborder)
            {
                tb.draw(canvas);
            }//end for

            //draw botborder
            for(BotBorder bb: botborder)
            {
                bb.draw(canvas);
            }//end for
            //draw explosion
            if(started)
            {
                explosion.draw(canvas);
            }//end for
            drawText(canvas);
            canvas.restoreToCount(savedState);

        }//end outer if
    }//end draw()


    public void newGame()
    {
        //if new game is started
        dissapear = false;
        //clear borders
        botborder.clear();
        topborder.clear();
        //clear missiles
        missiles.clear();
        pew.clear();
        //reset
        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.resetDY();
        player.resetScore();
        player.setY(HEIGHT/2);

        if(player.getScore()>best)
        {
            best = player.getScore();
        }//end if

        //create initial borders

        //initial top border
        for(int i = 0; i*20<WIDTH+40;i++)
        {
            //first top border create
            if(i==0)
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, 10));
            }//end inner if
            else
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, topborder.get(i-1).getHeight()+1));
            }//end inner if
        }//end for
        //initial bottom border
        for(int i = 0; i*20<WIDTH+40; i++)
        {
            //first border ever created
            if(i==0)
            {
                botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        ,i*20,HEIGHT - minBorderHeight));
            }//end if
            //adding borders until the initial screen is filed
            else
            {
                botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i * 20, botborder.get(i - 1).getY() - 1));
            }//end else
        }//end for

        newGameCreated = true;


    }
    public void drawText(Canvas canvas)
    {
        //all text shown on the screen
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("SCORE: " + (player.getScore()*3), 10, HEIGHT - 10, paint);
        canvas.drawText("HIGH SCORE: " + best, WIDTH - 215, HEIGHT - 10, paint);

        if(!player.getPlaying()&&newGameCreated&&reset)
        {
            Paint paint1 = new Paint();
            paint1.setColor(Color.GREEN);
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH/2-50, HEIGHT/2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2 + 40, paint1);
        }
    }


}