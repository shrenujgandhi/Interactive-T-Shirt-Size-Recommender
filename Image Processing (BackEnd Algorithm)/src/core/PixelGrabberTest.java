package core;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PixelGrabberTest {

  public PixelGrabberTest() {}

  public void processImage() throws IOException {

    BufferedImage image = ImageIO.read(this.getClass().getResource("/image.jpg"));

    try {

      ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
      ColorConvertOp op = new ColorConvertOp(cs, null);
      BufferedImage imageop = op.filter(image, null);

      File outputfile = new File("saved.png");
      ImageIO.write(imageop, "png", outputfile);

      PixelGrabber grabber = new PixelGrabber(imageop, 0, 0, -1, -1, false);

      if (grabber.grabPixels()) {
        int width = grabber.getWidth();
        int height = grabber.getHeight();
        System.out.println(width + " " + height);
        if (isGreyscaleImage(grabber)) {
          byte[] data = (byte[]) grabber.getPixels();
          System.out.println("here1");
          // Process greyscale image ...

        } else {
          int[] data = (int[]) grabber.getPixels();
          System.out.println(data.length);
          System.out.println("here");
          // Process Color image

        }
      }
    } catch (

    Exception e1)

    {
      e1.printStackTrace();
    }

  }

  public static final boolean isGreyscaleImage(PixelGrabber pg) {
    return pg.getPixels() instanceof byte[];
  }

  public static void main(String args[]) throws IOException {

    new PixelGrabberTest().processImage();

  }
}
