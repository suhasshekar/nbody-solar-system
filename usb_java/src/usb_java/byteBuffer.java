package usb_java;

import java.nio.ByteBuffer;

class byteBuffer {
	
	public static void main(String args[]){
		ByteBuffer bybuf = ByteBuffer.allocate(32);
		bybuf.putFloat(1.2345e11f);
		bybuf.flip();
		System.out.println("ByteBuffer is: " + bybuf.getFloat());
		
	}
}