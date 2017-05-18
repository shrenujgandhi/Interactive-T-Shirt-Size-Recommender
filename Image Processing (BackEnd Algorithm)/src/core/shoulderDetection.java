package core;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_highgui.cvShowImage;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

public class shoulderDetection {

	
	public static void main(String[] args){

		// Load file
		IplImage src = cvLoadImage("abcd1.jpg");
		IplImage gray = cvCreateImage(cvGetSize(src), 8, 1);
		cvCvtColor(src, gray, CV_BGR2GRAY);  
		cvSmooth(gray, gray);//, CV_GAUSSIAN, 3);
		
		// Load Upper_body cascade (.xml) file
		CvMemStorage mem = CvMemStorage.create();
		CascadeClassifier upper_body_cascade = new CascadeClassifier("haarcascade_upperbody.xml");

		if (upper_body_cascade.empty()) {
		  System.out.println("--(!)Error loading A\n");
		}
		   
		// Detect Upper Bodies
		Mat mat = new Mat(gray);
		RectVector bodies = new RectVector();

		// Draw Rectangles
	    for (int i = 0; i < bodies.size(); i++)
	    {
	        //cvRectangle(src, bodies.get(i));
	    }		
		   
	    //-- detect body */
	    
		cvShowImage("Result",gray);
		cvWaitKey(0);
	}
}
