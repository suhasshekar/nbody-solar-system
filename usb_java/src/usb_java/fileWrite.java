package usb_java;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class fileWrite{
	
	public static void main(String [] args){

		float[][] pX = { {1}, {2}, {3}, {4}, {5}, {6} };
		float[][] pY = { {7}, {8}, {9}, {10}, {11}, {12} };
			
		
		PrintWriter writer;
		try {
			writer = new PrintWriter("C:\\Users\\Shekara\\workspace\\usb_java\\output_file.txt", "UTF-8");
			for(int i = 0; i < 6; i++){
			writer.println("X-Position of " + i + ": " + pX[i][0]);
			writer.println("Y-Position of " + i + ": " + pY[i][0]);
			writer.println("");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//writer.println("The first line");
	    //writer.println("The second line");
		//writer.close();
	}
}