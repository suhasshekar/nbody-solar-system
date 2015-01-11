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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SimpleSyncTransfer
{

    private static final short VENDOR_ID = 0x1cbe;
    private static final short PRODUCT_ID = 0x0003;
    private static final byte INTERFACE = 0x00;
    private static final byte IN_ENDPOINT = (byte) 0x81;
    private static final byte OUT_ENDPOINT = 0x01;
    private static final int TIMEOUT = 3600000;
	
    public static String input="";
	private static boolean TX_done;
	public static int numOfPlanets;
	public static int mode;
	public static int numOfWeeks;
	public static int numOfDays;
	public static int frameRate = 15;
	
	public static float [] m = new float[40];
	
	public static float [] pX = new float[40];
	public static float [] pY = new float[40];
	
	public static float [] vX = new float[40];
	public static float [] vY = new float[40];
	

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
        

    	String line;
		FileInputStream inputStream = new FileInputStream("C:\\Users\\Shekara\\workspace\\usb_java\\input_file.txt");
		DataInputStream in = new DataInputStream(inputStream);
		
		int i = 0, k = 0, l = 0;
		
   	
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
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
    	PrintWriter writer;
    	writer = new PrintWriter("C:\\Users\\Shekara\\workspace\\usb_java\\output_time_solar.txt", "UTF-8");
        
		while ((line = bf.readLine()) != null) {	
			try{
				input = line.trim();
				//System.out.println("Input ["+ i +"]  " + input);
				if (input.isEmpty() || input.equals("") || input.equals("\n")) {
	                 
					//System.out.println("Blank space detected");                     
	               
				}
	            else{
	                    System.out.println("Input ["+ i +"]  " + input);
	                    write(handle, input);
	                     
				//write(handle, input);
				
				
				if(i == 0){
				//	mode = 1;
					mode = Integer.parseInt(input);
					System.out.println("Mode: " + mode);
					writer.println(mode + "  //Mode  ");
				}else if(i == 1){
					numOfPlanets = Integer.parseInt(input);
					System.out.println("Number of Planets: " + numOfPlanets);
					writer.println(numOfPlanets + " //Number of bodies " );
				}else if(i == 2){
				    numOfWeeks = Integer.parseInt(input);
				    System.out.println("Number of Weeks: " + numOfWeeks);
					writer.println(numOfWeeks + "  //Time period in number of weeks " );
				}else if(i == 3){
				    numOfDays = Integer.parseInt(input);
				    System.out.println("Number of Days: " + numOfDays);
					writer.println(numOfDays + "  //Time period in number of days ");
					writer.println("");
				}
				else if(l == 0){
					m[k] = Float.parseFloat(input);
					l++;
				} else if(l == 1){
					pX[k] = Float.parseFloat(input);
					System.out.println("Position X of Planet: " + k + ": " + pX[k]);
					l++;
				} else if(l == 2){
					pY[k] = Float.parseFloat(input);
					System.out.println("Position Y of Planet: " + k + ": " + pY[k]);
					l++;
				} else if(l == 3){
					vX[k] = Float.parseFloat(input);
					l++;
				} else if(l == 4){
					vY[k] = Float.parseFloat(input);
					l++;
				}
				
				if(l == 5){
					l = 0;
					k++;
				}
				
				
				i++;
	            }
				
			}
			catch(NumberFormatException nex)
			{
				System.out.println(nex);
				
			}
			catch(Exception nex)
			{
				System.out.println(nex);
				
			}
		   }
		
		bf.close();
   		int pass = numOfPlanets/8;
    	int passrem = numOfPlanets%8;
        int numOfPlanetsInThisRound = 0;
        int planetNumber = 0;
		   
		TX_done=true;
		
        int count = 1;
        
        if( mode == 1){
        	//Visualization Mode
        	
    		Thread jogl = new Thread(new nBody());
    		jogl.start();
    		
    		Thread.sleep(3000);
    		        	
	        while(TX_done == true){

	        try{
	        	write(handle, Integer.toString(count));
	        }catch(Exception ex){
	        	
	        }
		        
	        Thread.sleep(5);
	        System.out.println("Issued Read");
	        
            numOfPlanetsInThisRound = 0;
            planetNumber = 0;
	        for(int itr3 = 0; itr3 <= pass; itr3++){
	        	
        		if(itr3 == pass && passrem == 0 ){
        			
        		}else{
			        byte[] bytArray = new byte[64];	
			        byte[] smallArray = new byte[4];
			        ByteBuffer data = ByteBuffer.allocate(64);
			        try{
			        data = read(handle, 64);
			        }catch(Exception ex){
			        	System.out.println("Timeout Exception: " + ex);
			        }
			        
				        
			        if(itr3 < pass){
	    	        	data.get(bytArray, 0, (64));
	    	        } else if(itr3 == pass){
	    	        	data.get(bytArray, 0, (passrem*8));	
	    	        }
	    	        
	    	        int xpos = 0;

	                if(itr3 < pass){
	                	numOfPlanetsInThisRound = 8;
	                }else if(itr3 == pass){
	                	numOfPlanetsInThisRound = passrem;
	                }
			       			        
			        	for(int itr = 0; itr < (numOfPlanetsInThisRound * 2); itr++){
			                 for(int itr2 = 0; itr2 < 4; itr2++){
			                	 smallArray[itr2] = bytArray[(itr * 4) + itr2];
			                 }
			                                    
			                float f = ByteBuffer.wrap(smallArray).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					       // System.out.println("Data Recieved: " + itr + " : " + f);// + " " + fdata);
					        
					        if(xpos == 0){
					        	float opgl = f + 0.0f;
					        	pX[planetNumber] = opgl;
					        	//JOGLQuad_4.movex[planetNumber] = Float.parseFloat(f+"f");
					        }else{
					        	float opgl = f + 0.0f;
					        	pY[planetNumber] = opgl;
					        	//JOGLQuad_4.movey[planetNumber] = Float.parseFloat(f+"f");
					        }
					        
					        xpos++;
					        if(xpos == 2){
					        	xpos = 0;
					        	System.out.println("Planet (" + planetNumber + ") Position- x: " + pX[planetNumber] + " -y: " + pY[planetNumber]);// + " " + fdata);
					        	planetNumber++;
					        	
					        }
				        
			        	}
        		 }
	        }
	        Thread.sleep(5);
	      //  System.out.println("Counter Value: " + counter++);
	        
	        }
        }else if(mode == 0){
        	//Computation Mode
        	
        	//Send the Start Signal
        	try{
	        	write(handle, Integer.toString(count));
	        }catch(Exception ex){
	        	
	        }
        	long startTime = System.nanoTime();

        	
        	//Poll for data from the USB
        	//Iterate the read procedure to capture all the data
        	
            numOfPlanetsInThisRound = 0;
            planetNumber = 0;
        	for(int itr3 = 0; itr3 <= pass; itr3++){
        		
        		if(itr3 == pass && passrem == 0 ){
        			
        		}else{
        		byte[] bytArray = new byte[64];	
    	        byte[] smallArray = new byte[4];
    	        ByteBuffer data = ByteBuffer.allocate(64);
    	        try{
    	        data = read(handle, 64);
    	        }catch(Exception ex){
    	        	System.out.println("Timeout Exception: " + ex);
    	        }
    	        
    	        if(itr3 < pass){
    	        	data.get(bytArray, 0, (64));
    	        } else if(itr3 == pass){
    	        	data.get(bytArray, 0, (passrem*8));	
    	        }
    	        
    	        int xpos = 0;

                if(itr3 < pass){
                	numOfPlanetsInThisRound = 8;
                }else if(itr3 == pass){
                	numOfPlanetsInThisRound = passrem;
                }
    	        
	    	        for(int itr = 0; itr < (numOfPlanetsInThisRound * 2); itr++){
	                     for(int itr2 = 0; itr2 < 4; itr2++){
	                    	 smallArray[itr2] = bytArray[(itr * 4) + itr2];
	                     }
	                                        
	                    float f = ByteBuffer.wrap(smallArray).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	    		       // System.out.println("Data Recieved: " + itr + " : " + f);// + " " + fdata);
	    		        
	    		        if(xpos == 0){
	    		        	//writer.println("Planet[" + planetNumber + "] Position-X: " + f);
	    		        	pX[planetNumber] = f + 0.0f;
	    		        }else{
	    		        	//writer.println("Planet[" + planetNumber + "] Position-Y: " + f);
	    		        	//writer.println("");
	    		        	pY[planetNumber] = f + 0.0f;
	    		        }
	    		        
	    		        xpos++;
	    		        if(xpos == 2){
	    		        	xpos = 0;
	    		        	System.out.println("Planet (" + planetNumber + ") Position- x: " + pX[planetNumber] + " -y: " + pY[planetNumber]);// + " " + fdata);
	    		        	planetNumber++;
	    		        	
	    		        }
	        		
	    	        }
        	        	
        		}
        	}
        	
        	long endTime = System.nanoTime();
        	System.out.println("Took "+(endTime - startTime) + " ns"); 
        	//**********************************************
        	
        	
        	//Send the Start Signal
        	try{
	        	write(handle, Integer.toString(count));
	        }catch(Exception ex){
	        	
	        }
        	

        	
        	//Poll for data from the USB
        	//Iterate the read procedure to capture all the data
        	
            numOfPlanetsInThisRound = 0;
            planetNumber = 0;
        	for(int itr3 = 0; itr3 <= pass; itr3++){
        		
        		if(itr3 == pass && passrem == 0 ){
        			
        		}else{
        		byte[] bytArray = new byte[64];	
    	        byte[] smallArray = new byte[4];
    	        ByteBuffer data = ByteBuffer.allocate(64);
    	        try{
    	        data = read(handle, 64);
    	        }catch(Exception ex){
    	        	System.out.println("Timeout Exception: " + ex);
    	        }
    	        
    	        if(itr3 < pass){
    	        	data.get(bytArray, 0, (64));
    	        } else if(itr3 == pass){
    	        	data.get(bytArray, 0, (passrem*8));	
    	        }
    	        
    	        int xpos = 0;

                if(itr3 < pass){
                	numOfPlanetsInThisRound = 8;
                }else if(itr3 == pass){
                	numOfPlanetsInThisRound = passrem;
                }
    	        
	    	        for(int itr = 0; itr < (numOfPlanetsInThisRound * 2); itr++){
	                     for(int itr2 = 0; itr2 < 4; itr2++){
	                    	 smallArray[itr2] = bytArray[(itr * 4) + itr2];
	                     }
	                                        
	                    float f = ByteBuffer.wrap(smallArray).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	    		       // System.out.println("Data Recieved: " + itr + " : " + f);// + " " + fdata);
	    		        
	    		        if(xpos == 0){
	    		        	//writer.println("Planet[" + planetNumber + "] Position-X: " + f);
	    		        	vX[planetNumber] = f + 0.0f;
	    		        }else{
	    		        	//writer.println("Planet[" + planetNumber + "] Position-Y: " + f);
	    		        	//writer.println("");
	    		        	vY[planetNumber] = f + 0.0f;
	    		        }
	    		        
	    		        xpos++;
	    		        if(xpos == 2){
	    		        	xpos = 0;
	    		        	System.out.println("Planet (" + planetNumber + ") Position- x: " + vX[planetNumber] + " -y: " + vY[planetNumber]);// + " " + fdata);
	    		        	planetNumber++;
	    		        	
	    		        }
	        		
	    	        }
        	        	
        		}
        	}
        	
        	
        
        	//*********************************************
        	
        	for (int u = 0; u < numOfPlanets; u++)
            {
        	 writer.println(m[u]);
        	 writer.println(pX[u]);
             writer.println(pY[u]);          
             writer.println(vX[u]);
             writer.println(vY[u]);
             writer.println("");
              
            }
        	  	
        	
        	
        	
        	writer.close();
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

