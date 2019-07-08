package richinfo.attendance.util;

/**
 * Created by Daniel on 2019/5/8.
 */
public class testL {

    public volatile int inc = 0;

    public void increase() {
        inc++;
    }



    public static void main(String[] args) {
        final testL test = new testL();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<10000;j++)
                        test.increase();
                };
            }.start();
        }


        while(Thread.activeCount()>1 )  {
            Thread.yield();
            System.out.println(test.inc);

    }


    }
}
