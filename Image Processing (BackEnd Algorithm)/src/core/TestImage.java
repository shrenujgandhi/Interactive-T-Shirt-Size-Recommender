package core;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.widget.ScrollingImagePanel;

import com.sun.media.jai.codec.FileSeekableStream;

/**
 * This program decodes an image file of any JAI supported formats, such as GIF, JPEG, TIFF, BMP,
 * PNM, PNG, into a RenderedImage, scales the image by 2X with bilinear interpolation, and then
 * displays the result of the scale operation.
 */
public class TestImage {


  /**
   * The main method.
   * 
   * @throws InterruptedException
   * @throws IOException
   */
  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws InterruptedException, IOException {

    File file = new File("op1.txt");
    FileWriter fw = new FileWriter(file.getAbsoluteFile());
    BufferedWriter bw = new BufferedWriter(fw);
    /* Validate input. */
    /*
     * Create an input stream from the specified file name to be used with the file decoding
     * operator.
     */
    FileSeekableStream stream = null;
    try {
      stream = new FileSeekableStream("86479345.jpg");
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }
    /* Create an operator to decode the image file. */

    PlanarImage im0 = (PlanarImage) JAI.create("fileload", "abcd1.jpg");
    // Create the two kernels.
    float data_h[] = new float[] {1.0F, 0.0F, -1.0F, 1.414F, 0.0F, -1.414F, 1.0F, 0.0F, -1.0F};
    float data_v[] = new float[] {-1.0F, -1.414F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.414F, 1.0F};
    // float[] data_h = {0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    // float[] data_v = {-1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F};


    KernelJAI kern_h = new KernelJAI(3, 3, data_h);
    KernelJAI kern_v = new KernelJAI(3, 3, data_v);
    // Create the Gradient operation.
    PlanarImage im1 = (PlanarImage) JAI.create("gradientmagnitude", im0, kern_h, kern_v);
    RenderedOp op1 = JAI.create("gradientmagnitude", im1, kern_h, kern_v);

    BufferedImage imageop1 = convertRenderedImage(im1);

    BufferedImage imageop = new BufferedImage(imageop1.getWidth(), imageop1.getHeight(),
        BufferedImage.TYPE_BYTE_BINARY);
    imageop.getGraphics().drawImage(imageop1, 0, 0, null);

    // AffineTransform at = new AffineTransform();
    // at.scale(2.0, 2.0);
    // AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    // imageop = scaleOp.filter(imageop1, imageop);

    // File outputfile = new File("saved.png");
    // ImageIO.write(imageop, "png", outputfile);


    PixelGrabber grabber = new PixelGrabber(imageop, 0, 0, -1, -1, false);
    if (grabber.grabPixels()) {
      int width = grabber.getWidth();
      int height = grabber.getHeight();
      System.out.println(width + " " + height);
      byte[] data = (byte[]) grabber.getPixels();
      System.out.println("here1");
      byte[][] pixelData = new byte[grabber.getHeight()][grabber.getWidth()];
      for (int i = 0; i < grabber.getHeight(); i++) {
        for (int j = 0; j < grabber.getWidth(); j++) {
          // System.out.print(data[i * grabber.getWidth() + j]);
          pixelData[i][j] = data[i * grabber.getWidth() + j];
        }
        // System.out.println();
      }
      int x = 0, y = 0, foundRadius = 0;
      for (int i = 100; i < grabber.getHeight() - 100; i++) {
        int found = 0;
        for (int j = 100; j < grabber.getWidth() - 100; j++) {
          foundRadius = hasCircle(i, j, pixelData, bw);
          if (foundRadius != -1) {
            found = 1;
            y = j;
            break;
          }
        }
        if (found == 1) {
          x = i;
          break;
        }
      }

      System.out.println(x + " " + y + " " + foundRadius);

      // Process greyscale image ...
    }
    // Display the image.
    // RenderedImage im2 = im1.get
    // ROI roi = new ROI(im1);
    // Shape s = roi.getAsShape();
    // Rectangle rect = roi.getBounds();
    int width = im1.getWidth();
    int height = im1.getHeight();
    /* Attach image2 to a scrolling panel to be displayed. */
    ScrollingImagePanel panel = new ScrollingImagePanel(imageop, width, height);
    Frame window = new Frame("JAI Sample Program");
    window.add(panel);
    window.pack();
    window.show();

  }

  public static int hasCircle(int x, int y, byte[][] pixels, BufferedWriter bw) throws IOException {
    int index = 0;
    int found = 0;
    int maxCount = 0;
    for (int i = 50; i < 100; i++) {
      int count = 0;
      int testCount = 0;
      int x1 = x;
      int y1 = y + i;
      int x2 = x + i;
      int y2 = y;
      int x3 = x;
      int y3 = y - i;
      int x4 = x - i;
      int y4 = y;
      while (!(x1 == x + i && y1 == y)) {
        int[] co_ord = getNextPixelOnCircle(x, y, x1, y1, i, "FIRST");
        x1 = co_ord[0];
        y1 = co_ord[1];
        testCount++;
        if (pixels[x1][y1] == 1)
          count++;
      }

      while (!(x2 == x && y2 == y - i)) {
        int[] co_ord = getNextPixelOnCircle(x, y, x2, y2, i, "SECOND");
        x2 = co_ord[0];
        y2 = co_ord[1];
        testCount++;
        if (pixels[x2][y2] == 1)
          count++;
      }

      while (!(x3 == x - i && y3 == y)) {
        int[] co_ord = getNextPixelOnCircle(x, y, x3, y3, i, "THIRD");
        x3 = co_ord[0];
        y3 = co_ord[1];
        testCount++;
        if (pixels[x3][y3] == 1)
          count++;
      }

      while (!(x4 == x && y4 == y + i)) {
        int[] co_ord = getNextPixelOnCircle(x, y, x4, y4, i, "FOURTH");
        x4 = co_ord[0];
        y4 = co_ord[1];
        testCount++;
        if (pixels[x4][y4] == 1)
          count++;
      }
      // System.out.println(x + " " + y + " " + count);
      // for (int j = 0; j < i; j++) {
      // if (pixels[x + j][y + i - j] == 1)
      // count++;
      // if (pixels[x + i - j][y - j] == 1)
      // count++;
      // if (pixels[x - j][y - i + j] == 1)
      // count++;
      // if (pixels[x - i + j][y + j] == 1)
      // count++;
      // }
      if (count >= testCount * 0.6) {
        // testCount = count;
        found = 1;
        index = i;
        break;
      }
      if (count > maxCount) {
        maxCount = count;
      }
    }
    bw.write(index + " " + x + " " + y + " " + maxCount + "\n");
    if (found == 1)
      return index;
    return -1;
  }



  private static int[] getNextPixelOnCircle(int x, int y, int x1, int y1, int i, String type) {
    double difference1;
    double difference2;
    double difference3;
    int ret;
    switch (type) {
      case "FIRST":
        difference1 = onCircleDiff(x, y, x1 + 1, y1, i);
        difference2 = onCircleDiff(x, y, x1 + 1, y1 - 1, i);
        difference3 = onCircleDiff(x, y, x1, y1 - 1, i);
        ret = difference1 < difference2 ? (difference1 < difference3 ? 1 : 3)
            : (difference2 < difference3 ? 2 : 3);
        switch (ret) {
          case 1:
            int[] val1 = {x1 + 1, y1};
            return val1;
          case 2:
            int[] val2 = {x1 + 1, y1 - 1};
            return val2;
          case 3:
            int[] val3 = {x1, y1 - 1};
            return val3;
          default:
            break;
        }
        break;

      case "SECOND":
        difference1 = onCircleDiff(x, y, x1, y1 - 1, i);
        difference2 = onCircleDiff(x, y, x1 - 1, y1 - 1, i);
        difference3 = onCircleDiff(x, y, x1 - 1, y1, i);
        ret = difference1 < difference2 ? (difference1 < difference3 ? 1 : 3)
            : (difference2 < difference3 ? 2 : 3);
        switch (ret) {
          case 1:
            int[] val1 = {x1, y1 - 1};
            return val1;
          case 2:
            int[] val2 = {x1 - 1, y1 - 1};
            return val2;
          case 3:
            int[] val3 = {x1 - 1, y1};
            return val3;
          default:
            break;
        }
        break;

      case "THIRD":
        difference1 = onCircleDiff(x, y, x1 - 1, y1, i);
        difference2 = onCircleDiff(x, y, x1 - 1, y1 + 1, i);
        difference3 = onCircleDiff(x, y, x1, y1 + 1, i);
        ret = difference1 < difference2 ? (difference1 < difference3 ? 1 : 3)
            : (difference2 < difference3 ? 2 : 3);
        switch (ret) {
          case 1:
            int[] val1 = {x1 - 1, y1};
            return val1;
          case 2:
            int[] val2 = {x1 - 1, y1 + 1};
            return val2;
          case 3:
            int[] val3 = {x1, y1 + 1};
            return val3;
          default:
            break;
        }
        break;
      case "FOURTH":
        difference1 = onCircleDiff(x, y, x1, y1 + 1, i);
        difference2 = onCircleDiff(x, y, x1 + 1, y1 + 1, i);
        difference3 = onCircleDiff(x, y, x1 + 1, y1, i);
        ret = difference1 < difference2 ? (difference1 < difference3 ? 1 : 3)
            : (difference2 < difference3 ? 2 : 3);
        switch (ret) {
          case 1:
            int[] val1 = {x1, y1 + 1};
            return val1;
          case 2:
            int[] val2 = {x1 + 1, y1 + 1};
            return val2;
          case 3:
            int[] val3 = {x1 + 1, y1};
            return val3;
          default:
            break;
        }
        break;
      default:
        break;
    }
    return null;
  }

  private static double onCircleDiff(int x, int y, int x1, int y1, int i) {
    double val1 = Math.pow(x - x1, 2) + Math.pow(y - y1, 2);
    double val2 = Math.pow(i, 2);
    return Math.abs(val1 - val2);
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
