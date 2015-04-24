package main.input;

import java.awt.*;
import java.awt.image.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Takes screenshots at a specific frequency and outputs them to a
 * ConcurrentLinkedQueue<BufferedImage> at a specific resolution.
 */
public class ScreenGrabber {
    private Robot myRobot;
    private Rectangle screenRectangle;
    private ConcurrentLinkedQueue<BufferedImage> buffer;
    private long frequency;
    private AtomicBoolean isCapturing;

    private ScreenGrabber(Robot robot,
                          ConcurrentLinkedQueue<BufferedImage> buffer,
                          long frequency) {
        this.myRobot = robot;
        this.screenRectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        this.buffer = buffer;
        this.frequency = frequency;
        this.isCapturing = new AtomicBoolean();
    }

    public static ScreenGrabber fromQueueAndFrequency(ConcurrentLinkedQueue<BufferedImage> buffer,
                                                      long frequency)
            throws AWTException {;
        return new ScreenGrabber(new Robot(), buffer, frequency);
    }

    public void startCapture() {
        this.isCapturing.set(true);
        new Thread(() -> capture()).start(); // lambda function that is coerced to be a Runnable
    }

    public void endCapture() {
        this.isCapturing.set(false);
    }

    public void capture() {
        while (this.myRobot != null && this.isCapturing.get()) {
            long startTimeMillis = System.currentTimeMillis();
            BufferedImage img = this.myRobot.createScreenCapture(this.screenRectangle);
            if(img != null) {
                this.buffer.add(img);
            }

            long timeElapsed = System.currentTimeMillis() - startTimeMillis;
            long sleepTime = (this.frequency - timeElapsed > 0) ? this.frequency - timeElapsed : 0;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
