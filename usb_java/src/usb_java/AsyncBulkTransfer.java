package usb_java;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.usb4java.BufferUtils;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

public class AsyncBulkTransfer
{
  
    static class EventHandlingThread extends Thread
    {
        
        private volatile boolean abort;

        public void abort()
        {
            this.abort = true;
        }

        @Override
        public void run()
        {
            while (!this.abort)
            {
                int result = LibUsb.handleEventsTimeout(null, 500);
                if (result != LibUsb.SUCCESS)
                    throw new LibUsbException("Unable to handle events", result);
            }
        }
    }
    /** The vendor ID of the Tiva C series MCU. */
    private static final short VENDOR_ID = 0x1cbe;
    /** My Mobile */
    //private static final short VENDOR_ID = 0x1004;
    
    /** The vendor ID of the Tiva C series MCU. */
    private static final short PRODUCT_ID = 0x0003;
    /** My Mobile */
    //private static final short PRODUCT_ID = 0X6322;

    /** The ADB interface number of the Tiva C series MCU. */
    private static final byte INTERFACE = (byte) 0x00;

    /** The ADB input endpoint of the Tiva C series MCU. */
    private static final byte IN_ENDPOINT = (byte) 0x81;

    /** The ADB output endpoint of the Tiva C series MCU. */
    private static final byte OUT_ENDPOINT = 0x01;

    /** The communication timeout in milliseconds. */
    private static final int TIMEOUT = 5000;

   
    static volatile boolean exit = false;
	public static String input="";

	private static boolean TX_done;
	private static int count = 0;
	static byte [] bytebuffer = new byte[20];
	static Transfer transfer = LibUsb.allocTransfer();
	//private static byte[] specified;


    public static void write(DeviceHandle handle, String input2,
        TransferCallback callback)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(input2.length());
        buffer.put(input2.getBytes());
        Transfer transfer = LibUsb.allocTransfer();
        LibUsb.fillBulkTransfer(transfer, handle, OUT_ENDPOINT, buffer,
            callback, null, TIMEOUT);
        System.out.println("Sending " + input2.length() + " bytes to device\n");
        int result = LibUsb.submitTransfer(transfer);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to submit transfer", result);
        }
    }


    public static void read(DeviceHandle handle, int size,
        TransferCallback callback)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(ByteOrder.LITTLE_ENDIAN);
        transfer = LibUsb.allocTransfer();
        LibUsb.fillBulkTransfer(transfer, handle, IN_ENDPOINT, buffer,
            callback, null, TIMEOUT);
       // buffer.
        System.out.println("Reading " + buffer.position());
        System.out.println("Counter Value " + count++);
//        byte[] specified = new byte[20];
//        buffer.get(specified, 0 , 19);
//        String S = specified.toString();
//        System.out.println("Recieved Value: " + Arrays.toString(specified));
        int result = LibUsb.submitTransfer(transfer);

        System.out.println("Result of Submit Transfer" + result);
        if (result != LibUsb.SUCCESS)
        {
        	throw new LibUsbException("Unable to submit transfer", result);
        }
    }
    

    public static void main(String[] args) throws Exception
    {

    	String line;
		FileInputStream inputStream = new FileInputStream("C:\\Users\\Shekara\\workspace\\usb_java\\input_file.txt");
		DataInputStream in = new DataInputStream(inputStream);
		BufferedReader bf = new BufferedReader(new InputStreamReader(in));
		int i = 0;
		//final byte [] bytebuffer = new byte[20];
        // Initialize the libusb context
        int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Open test device
        final DeviceHandle handle = LibUsb.openDeviceWithVidPid(null, VENDOR_ID, PRODUCT_ID);
        
        if (handle == null)
        {
            System.err.println("Device not found.Please check the device and usb cable connection");
            System.exit(1);
        }

        // Start event handling thread
        //EventHandlingThread thread = new EventHandlingThread();
        //thread.start();

        // Claim the ADB interface
        result = LibUsb.claimInterface(handle, INTERFACE);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to claim interface", result);
        }

        // This callback is called after the ADB answer body has been
        // received. The asynchronous transfer chain ends here.
        final TransferCallback bodyReceived = new TransferCallback()
        {
        	
        	@Override
            public void processTransfer(Transfer transfer)
            {
                System.out.println(transfer.actualLength() + " bytes received");
                transfer.buffer().get(bytebuffer, 0, transfer.actualLength());
                System.out.println("Body Data Received: " + Arrays.toString(bytebuffer));
                
                //read(handle, 20, bodyReceived);
                LibUsb.freeTransfer(transfer);
                //System.out.println("Asynchronous communication finished");
                //exit = true;
                
            }
        };
        
    

        final TransferCallback bodySent = new TransferCallback()
        {
            @Override
            public void processTransfer(Transfer transfer)
            {
            	 //System.out.println(transfer.actualLength() + " bytes received");
            	 //ByteBuffer place = transfer.buffer();
            	// place.position(12);
             	// int dataSize = place.asIntBuffer().get();
                 //read(handle, dataSize, bodyReceived);
                 LibUsb.freeTransfer(transfer);
            }
        };

    
        
        
		while ((line = bf.readLine()) != null) {	
			try{
				input = line.trim();
				System.out.println("Input ["+i+"]  "+input);
				write(handle, input, bodySent);
				i++;
				
				
			}
			catch(Exception ex)
			{
				System.out.println(ex);
			}
		   }
		TX_done=true;
		
		while(true){
			if(TX_done == true){
			read(handle, 20, bodyReceived);
			LibUsb.handleEventsTimeout(null, 5);
			}else{
				break;
			}
		}
       
//        while (!exit)
//        {
//            Thread.yield();
//        }

        // Release the ADB interface
        result = LibUsb.releaseInterface(handle, INTERFACE);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to release interface", result);
        }

        // Close the device
        LibUsb.close(handle);

        // Stop event handling thread
        //thread.abort();
        //thread.join();

     
        LibUsb.exit(null);

        System.out.println("Program finished");
    }
    
    
    
}