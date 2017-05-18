package core;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPointFrom32f;
import static org.bytedeco.javacpp.opencv_highgui.cvShowImage;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.javacpp.opencv_imgproc.cvCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughCircles;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.widget.ScrollingImagePanel;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvPoint3D32f;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class circleDetection {
  public static void main(String[] args) throws InterruptedException, IOException {
    IplImage src = cvLoadImage("abcd.jpg");
    IplImage gray = cvCreateImage(cvGetSize(src), 8, 1);
    cvCvtColor(src, gray, CV_BGR2GRAY);
    cvSmooth(gray, gray);
    CvMemStorage mem = CvMemStorage.create();
    int leftCount = 0, rightCount = 0;
    PlanarImage im0 = (PlanarImage) JAI.create("fileload", "abcd.jpg");
    CvSeq circles = cvHoughCircles(gray, // Input image
        mem, // Memory Storage
        CV_HOUGH_GRADIENT, // Detection method
        1, // Inverse ratio
        100, // Minimum distance between the centers of the detected circles
        15, // Higher threshold for canny edge detector
        100, // Threshold at the center detection stage
        10, // min radius
        1000 // max radius
    );

    int temp = circles.total();
    int cdTopHeight = 0;
    int cdTopWidth = 0;
    int diameter = 0;
    if (temp != 0) {
      for (int i = 0; i < 1; i++) {
        CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
        CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
        int radius = Math.round(circle.z());
        System.out.println(radius);
        cdTopWidth = center.x();
        cdTopHeight = center.y() - radius;
        diameter = radius * 2;
        cvCircle(src, center, radius, CvScalar.GREEN, 2, CV_AA, 0);
      }
    }

    System.out.println("height " + cdTopHeight + " width " + cdTopWidth);

    float data_h[] = new float[] {1.0F, 0.0F, -1.0F, 1.414F, 0.0F, -1.414F, 1.0F, 0.0F, -1.0F};
    float data_v[] = new float[] {-1.0F, -1.414F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.414F, 1.0F};
    KernelJAI kern_h = new KernelJAI(3, 3, data_h);
    KernelJAI kern_v = new KernelJAI(3, 3, data_v);
    PlanarImage im1 = (PlanarImage) JAI.create("gradientmagnitude", im0, kern_h, kern_v);
    BufferedImage imageop1 = convertRenderedImage(im1);

    BufferedImage imageop = new BufferedImage(imageop1.getWidth(), imageop1.getHeight(),
        BufferedImage.TYPE_BYTE_BINARY);
    imageop.getGraphics().drawImage(imageop1, 0, 0, null);

    PixelGrabber grabber = new PixelGrabber(imageop, 0, 0, -1, -1, false);
    if (grabber.grabPixels()) {
      byte[] data = (byte[]) grabber.getPixels();
      byte[][] pixelData = new byte[grabber.getHeight()][grabber.getWidth()];
      for (int i = 0; i < grabber.getHeight(); i++) {
        for (int j = 0; j < grabber.getWidth(); j++) {
          pixelData[i][j] = data[i * grabber.getWidth() + j];
        }
      }
      for (int i = 0; i < cdTopWidth; i++) {
        if (pixelData[cdTopHeight][i] != 0) {
          leftCount = i;
          break;
        }
      }
      for (int i = grabber.getWidth() - 1; i > cdTopWidth; i--) {
        if (pixelData[cdTopHeight][i] != 0) {
          rightCount = i;
          break;
        }
      }
    }

    System.out.println("Shoulder width: " + (rightCount - leftCount));
    double shoulderSize = 4.72 * (rightCount - leftCount) / diameter;
    System.out.println("Shouldersize in inches : " + shoulderSize);

    int width = im1.getWidth();
    int height = im1.getHeight();
    ScrollingImagePanel panel = new ScrollingImagePanel(imageop, width, height);
    //Frame window = new Frame("JAI Sample Program");
    //window.add(panel);
    //window.pack();
    //window.show();

    cvShowImage("Result", src);
    cvWaitKey(0);
  }

  public static BufferedImage convertRenderedImage(RenderedImage img) {
    if (img instanceof BufferedImage) {

      return (BufferedImage) img;
    }
    ColorModel cm = img.getColorModel();
    int width = img.getWidth();
    int height = img.getHeight();
    WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
    Hashtable<String, Object> properties = new Hashtable<String, Object>();
    String[] keys = img.getPropertyNames();
    if (keys != null) {
      for (int i = 0; i < keys.length; i++) {
        properties.put(keys[i], img.getProperty(keys[i]));
      }
    }
    BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
    img.copyData(raster);
    return result;
  }

}
