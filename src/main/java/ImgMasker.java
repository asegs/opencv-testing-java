import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

public class ImgMasker {

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    public static final Scalar low = new Scalar(0,0,0);
    public static final Scalar high = new Scalar(100,150,150);

    public static final int rows = 1;
    public static final int cols = 1;

    public static final int concurrentThreads = 16;
    public static final int frames = 60;
    public static final Mat input = imageToMat("src/main/java/mona_lisa.jpg");

    public static Mat imageToMat (String filename) {
        return Imgcodecs.imread(filename);
    }

    public static void process(Mat frame,String out) {
        Mat frameHSV = new Mat();
        Imgproc.cvtColor(frame, frameHSV, Imgproc.COLOR_BGR2HSV);
        Mat thresh = new Mat();
        Core.inRange(frameHSV, low,
                high, thresh);
        //Imgcodecs.imwrite(out, thresh);
    }

    public static void testThreaded() throws InterruptedException {

        //Mat [][] part = partition(input,rows,cols);
        long start = System.nanoTime();
        process(input,"src/main/java/mona_lisa_out.jpg");
        long end = System.nanoTime();
        //System.out.println("Time to process whole: " + deltaMs(start,end));

//        Thread [] threads = new Thread[rows * cols];
//        int counter = 0;
//        for (Mat [] matRow : part) {
//            for (Mat mat : matRow) {
//                threads[counter] = new Thread(()->process(mat,""));
//                counter++;
//            }
//        }
//        start = System.nanoTime();
//        for (Thread thread : threads) {
//            thread.start();
//        }
//        for (Thread thread : threads) {
//            thread.join();
//        }
//        end = System.nanoTime();
        //System.out.println("Time to process threads: " + deltaMs(start,end));

    }

    public static Mat [][] partition (Mat input,int rows, int cols) {
        int rowPixel = 0;
        int colPixel = 0;
        int rowGap = input.height() / rows;
        int colGap = input.width() / cols;
        int rowNew;
        int colNew;
        Mat [][] toReturn = new Mat[rows][cols];
        for (int row = 0; row < rows ; row ++ ){
            if (row == rows - 1) {
                rowNew = input.height();
            }else {
                rowNew = rowPixel + rowGap;
            }
            for (int col = 0 ; col < cols ; col ++ ){
                if (col == cols - 1) {
                    colNew = input.width();
                }else {
                    colNew = colPixel + colGap;
                }
                toReturn[row][col] = input.submat(rowPixel,rowNew,colPixel,colNew);
                colPixel = colNew;
            }

            rowPixel = rowNew;
            colPixel = 0;
        }
        return toReturn;
    }

    public static double deltaMs(long start,long end){
        return (end - start) / 1000000.0;
    }

    public static void nConcurrent() throws InterruptedException {
        Thread [] concurrent = new Thread[concurrentThreads];
        for ( int i = 0 ; i < concurrentThreads ; i ++ ) {
                concurrent[i] = new Thread(()-> {
                    try {
                        testThreaded();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        long start = System.nanoTime();
        for (Thread thread : concurrent) {
            thread.start();
        }
        for (Thread thread : concurrent) {
            thread.join();
        }
        long end = System.nanoTime();
        //System.out.println("Time to process " + concurrentThreads + " images: " + deltaMs(start,end));
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.nanoTime();
        for ( int i = 0 ; i < frames ; i ++ ) {
            nConcurrent();
        }
        long end = System.nanoTime();
        System.out.println("Time to process "+ frames +" frames from " + concurrentThreads + " sources:  " + deltaMs(start,end) + "ms");
    }


}
