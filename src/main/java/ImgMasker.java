import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
        process(input,"src/main/java/mona_lisa_out.jpg");
    }
}
