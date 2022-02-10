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

    public static Scalar low = new Scalar(0,0,0);
    public static Scalar high = new Scalar(100,150,150);

    public static Mat imageToMat (String filename) {
        return Imgcodecs.imread(filename);
    }

    public static void process(Mat frame,String out) {
        Mat frameHSV = new Mat();
        Imgproc.cvtColor(frame, frameHSV, Imgproc.COLOR_BGR2HSV);
        Mat thresh = new Mat();
        Core.inRange(frameHSV, low,
                high, thresh);
        Imgcodecs.imwrite(out, thresh);
    }

    public static void main(String[] args) {
        Mat input = imageToMat("src/main/java/mona_lisa.jpg");
        Mat [][] part = partition(input,3,3);
        for (Mat [] matRow : part) {
            System.out.println(Arrays.toString(matRow));
        }
        process(input,"src/main/java/mona_lisa_out.jpg");
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


}
