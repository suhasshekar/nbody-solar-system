package usb_java;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import org.usb4java.BufferUtils;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 * Demonstrates how to do synchronous bulk transfers. This demo sends some
 * hardcoded data to an Android Device (Samsung Galaxy Nexus) and receives some
 * data from it.
 * 
 * If you have a different Android device then you can get this demo working by
 * changing the vendor/product id, the interface number and the endpoint
 * addresses.
 * 
 * @author Klaus Reimer <k@ailis.de>
 */
public class SyncBulkTransfer
{

    /** The vendor ID of the Samsung Galaxy Nexus. */
    private static final short VENDOR_ID = 0x1cbe;

    /** The vendor ID of the Samsung Galaxy Nexus. */
    private static final short PRODUCT_ID = 0x0003;

    /** The ADB interface number of the Samsung Galaxy Nexus. */
    private static final byte INTERFACE = 0x00;

    /** The ADB input endpoint of the Samsung Galaxy Nexus. */
    private static final byte IN_ENDPOINT = (byte) 0x81;

    /** The ADB output endpoint of the Samsung Galaxy Nexus. */
    private static final byte OUT_ENDPOINT = 0x01;

    /** The communication timeout in milliseconds. */
    private static final int TIMEOUT = 2000;
    
	public static String input="";

	private static boolean TX_done;
	
	public static int numOfPlanets;

    /**
     * Writes some data to the device.
     * 
     * @param handle
     *            The device handle.
     * @param data
     *            The data to send to the device.
     */
    public static void write(DeviceHandle handle, String data)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length());
        buffer.put(data.getBytes());
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, OUT_ENDPOINT, buffer,
            transferred, TIMEOUT);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to send data", result);
        }
        System.out.println(transferred.get() + " bytes sent to device");
    }

    /**
     * Reads some data from the device.
     * 
     * @param handle
     *            The device handle.
     * @param size
     *            The number of bytes to read from the device.
     * @return The read data.
     */
    public static ByteBuffer read(DeviceHandle handle, int size)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(
            ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, IN_ENDPOINT, buffer,
            transferred, TIMEOUT);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to read data", result);
        }
        System.out.println(transferred.get() + " bytes read from device");
        return buffer;
    }

    /**
     * Main method.
     * 
     * @param args
     *            Command-line arguments (Ignored)
     * @throws Exception
     *             When something goes wrong.
     */
    public static void main(String[] args) throws Exception
    {
        
    	//Thread jogl = new Thread(new nBody());
    	//jogl.start();
    	
    	String line;
		FileInputStream inputStream = new FileInputStream("C:\\Users\\Shekara\\workspace\\usb_java\\input_file.txt");
		DataInputStream in = new DataInputStream(inputStream);
		BufferedReader bf = new BufferedReader(new InputStreamReader(in));
		int i = 0;
    	// Initialize the libusb context
        int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Open test device (Samsung Galaxy Nexus)
        DeviceHandle handle = LibUsb.openDeviceWithVidPid(null, VENDOR_ID,
            PRODUCT_ID);
        if (handle == null)
        {
            System.err.println("Test device not found.");
            System.exit(1);
        }

        // Claim the ADB interface
        result = LibUsb.claimInterface(handle, INTERFACE);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to claim interface", result);
        }

        // Send ADB CONNECT message
        //write(handle, CONNECT_HEADER);
        //write(handle, CONNECT_BODY);
        
		while ((line = bf.readLine()) != null) {	
			try{
				input = line.trim();
				System.out.println("Input ["+i+"]  " + input);
				write(handle, input);
				i++;
				
				if(i == 0){
					numOfPlanets = Integer.parseInt(input);
				}
				
				
			}
			catch(Exception ex)
			{
				System.out.println(ex);
			}
		   }
		TX_done=true;

        // Receive the header of the ADB answer (Most likely an AUTH message)
        
//        ByteBuffer header = read(handle, 24);
//        header.position(12);
//        int dataSize = header.asIntBuffer().get();

        // Receive the body of the ADB answer
//        @SuppressWarnings("unused")
//        ByteBuffer data = read(handle, dataSize);
        
        while(TX_done == true){
        try{
        	write(handle, "1");
        }catch(Exception ex){
        	
        }
        Thread.sleep(200);
        System.out.println("Issued Read");
        byte[] bytArray = new byte[64];	
        ByteBuffer data = ByteBuffer.allocate(64);
        try{
        data = read(handle, 64);
        }catch(Exception ex){
        	System.out.println("Timeout Exception: " + ex);
        }
 //       System.out.println("Data Received: " + data);
        data.get(bytArray, 0, bytArray.length);
//        
        String sdata = new String(bytArray);
//        float fdata = Float.parseFloat(sdata);
//        System.out.println("Finished Get Operation");
        System.out.println("Data Recieved: " + sdata);// + " " + fdata);
        Thread.sleep(200);
        }

        // Release the ADB interface
        result = LibUsb.releaseInterface(handle, INTERFACE);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to release interface", result);
        }

        // Close the device
        LibUsb.close(handle);

        // Deinitialize the libusb context
        LibUsb.exit(null);
    }
}