package buddySystem;

import java.io.FileWriter;
import java.io.IOException;
/**
 * This class is a test driver for the BufferManager class. 
 * 	6 tests are run.
 * @author Luke Newman 12/5/2020
 *
 */
public class Test {
	
	static int[] memorySpace = new int[5120];
	static int[] statusCounter = new int[7];
	static FileWriter fw = null;
	
	public static void main(String[] args) {
		
		try {
			// open a file and write a header to it
			fw = new FileWriter("Asg7Output.txt");
			fw.write("Luke Newman, 12.3.2020   Assignment 7\n\n\n");
			fw.write("Initializing Buffers.\n");
			
			// Initialize the BufferManager
			BufferManager.initialize();
			
			
			// First Test (Initialization)
			expectedValues(new int[] {10,0,0,0,0,0,0});
			fw.write("Free Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			
			// Second Test (Invalid Request)
			fw.write("Requesting 700 word block.\n");
			int assignedAddress = BufferManager.request(700);
			fw.write("Expected: -2\n");
			fw.write("Actual:   " + assignedAddress + "\n\n");
			
			// Third Test (Request smallest sized block - 6 words)
			fw.write("Requesting 6 word block.\n");
			assignedAddress = BufferManager.request(6);
			expectedValues(new int[] {9,1,1,1,1,1,1});
			fw.write("Actual Assigned Address: " + assignedAddress + "\n\n");
			fw.write("Free Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n": "Status: OK\n\n");
			fw.write("Returning 6 word block.\n");
			BufferManager.returnAddress(assignedAddress);
			expectedValues(new int[] {10,0,0,0,0,0,0});
			fw.write("Free Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			
			// Fourth Test (Ten 510 word blocks)
			fw.write("Requesting Ten 510 word blocks.\n");
			int[] addresses = new int[10];
			for (int i = 0; i < 10; i++) {
				addresses[i] = BufferManager.request(510);
			}
			fw.write("Expected Values: 0 free buffers\n\n");
			for (int i : addresses) {
				fw.write("Actual Assigned Address: " + i + "\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Request an Additional Buffer of size 6.\n");
			fw.write("Expected Value: -1\n");
			assignedAddress = BufferManager.request(6);
			fw.write("Actual Assigned Address: " + assignedAddress + "\n\n");
			fw.write("Free Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Returning all ten 510 word buffers.\n");
			expectedValues(new int[] {10,0,0,0,0,0,0});
			for (int i = 0; i < 10; i++) {
				BufferManager.returnAddress(addresses[i]);
			}
			fw.write("Free Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			
			
			
			// Fifth Test
			// 40 126 sized buffers, return two that shouldn't recombine, 
			// then return two more that should combine with the other two 
			// that will eventually get your 510 sized buffer back
			fw.write("Requesting Forty 126 word blocks.\n");
			int[] fortyAddresses = new int[40];
			for (int i = 0; i < 40; i++) {
				fortyAddresses[i] = BufferManager.request(126);
			}
			fw.write("Expected Values: 0 free buffers\n\n");
			for (int i : fortyAddresses) {
				fw.write("Actual Assigned Address: " + i + "\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Request an Additional Buffer of size 6.\n");
			fw.write("Expected Value: -1\n");
			assignedAddress = BufferManager.request(6);
			fw.write("Actual Assigned Address: " + assignedAddress + "\n\n");
			fw.write("Free Buffer Count:\n" + BufferManager.status() + "\n\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n");
			fw.write("Returning 126 sized buffers with addresses " + fortyAddresses[16] + " and " + 
					fortyAddresses[19] + ".\n");
			BufferManager.returnAddress(fortyAddresses[16]);
			BufferManager.returnAddress(fortyAddresses[19]);
			expectedValues(new int[] {0,0,2,0,0,0,0});
			fw.write("Free Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Returning 126 sized buffers with addresses " + fortyAddresses[17] + " and " + 
					fortyAddresses[18] + ".\n");
			BufferManager.returnAddress(fortyAddresses[17]);
			BufferManager.returnAddress(fortyAddresses[18]);
			expectedValues(new int[] {1,0,0,0,0,0,0});
			fw.write("Free Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			// return the rest of the 126 sized buffers
			fw.write("Returning the rest of the 126 sized buffers.\n");
			for (int i = 0; i < 40; i++) {
				if (!(i > 15 && i < 20)) {
					BufferManager.returnAddress(fortyAddresses[i]);
				}
			}
			expectedValues(new int[] {10,0,0,0,0,0,0});
			fw.write("Free Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			
			// sixth test - Testing multiple various sized buffers
			// 1 510, 3 254, 5 126, 8 62, 5 30, 10 14, 12 6
			fw.write("Requesting 1 510, 3 254, 5 126, 8 62, 5 30, 10 14, and 12 6 sized buffers.\n\n");
			expectedValues(new int[] {4,0,1,1,1,0,0});
			int addressFor512 = BufferManager.request(510);
			fw.write("Actual Assigned Address for 510: " + addressFor512 + "\n");
			int[] addressesFor254 = new int[3];
			for (int i = 0; i < 3; i++) {
				addressesFor254[i] = BufferManager.request(254);
				fw.write("Actual Assigned Address for 254: " + addressesFor254[i] + "\n");
			}
			int[] addressesFor126 = new int[5];
			for (int i = 0; i < 5; i++) {
				addressesFor126[i] = BufferManager.request(126);
				fw.write("Actual Assigned Address for 126: " + addressesFor126[i] + "\n");
			}
			int[] addressesFor62 = new int[8];
			for (int i = 0; i < 8; i++) {
				addressesFor62[i] = BufferManager.request(62);
				fw.write("Actual Assigned Address for 62: " + addressesFor62[i] + "\n");
			}
			int[] addressesFor30 = new int[5];
			for (int i = 0; i < 5; i++) {
				addressesFor30[i] = BufferManager.request(30);
				fw.write("Actual Assigned Address for 30: " + addressesFor30[i] + "\n");
			}
			int[] addressesFor14 = new int[10];
			for (int i = 0; i < 10; i++) {
				addressesFor14[i] = BufferManager.request(14);
				fw.write("Actual Assigned Address for 14: " + addressesFor14[i] + "\n");
			}
			int[] addressesFor6 = new int[12];
			for (int i = 0; i < 12; i++) {
				addressesFor6[i] = BufferManager.request(6);
				fw.write("Actual Assigned Address for 6: " + addressesFor6[i] + "\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			// requesting 3 more 512 sized buffers so that the buffer pool memory should say tight
			fw.write("Requesting 3 more 512 sized buffers.\n");
			expectedValues(new int[] {1,0,1,1,1,0,0});
			int[] additionalAddresses = new int[3];
			for (int i = 0; i < 3; i++) {
				additionalAddresses[i] = BufferManager.request(510);
				fw.write("Actual Assigned Address for 512: " + additionalAddresses[i] + "\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Returning all buffers slowly to watch them recombine.\n");
			// return all 6 sized buffers
			fw.write("Return all 6 sized buffers.\n");
			for (int i = 0; i < 12; i++) {
				BufferManager.returnAddress(addressesFor6[i]);
				fw.write("Returning 6 sized buffer at address " + addressesFor6[i] + ".\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Return all 14 sized buffers.\n");
			for (int i = 0; i < 10; i++) {
					BufferManager.returnAddress(addressesFor14[i]);
					fw.write("Returning 14 sized buffer at address " + addressesFor14[i] + ".\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Return all 30 sized buffers.\n");
			for (int i = 0; i < 5; i++) {
				BufferManager.returnAddress(addressesFor30[i]);
				fw.write("Returning 30 sized buffer at address " + addressesFor30[i] + ".\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Return all 62 sized buffers.\n");
			for (int i = 0; i < 8; i++) {
				BufferManager.returnAddress(addressesFor62[i]);
				fw.write("Returning 62 sized buffer at address " + addressesFor62[i] + ".\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Return all 126 sized buffers.\n");
			for (int i = 0; i < 5; i++) {
				BufferManager.returnAddress(addressesFor126[i]);
				fw.write("Returning 126 sized buffer at address " + addressesFor126[i] + ".\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Return all 254 sized buffers.\n");
			for (int i = 0; i < 3; i++) {
				BufferManager.returnAddress(addressesFor254[i]);
				fw.write("Returning 254 sized buffer at address " + addressesFor254[i] + ".\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.write("Return all 510 sized buffers.\n");
			BufferManager.returnAddress(addressFor512);
			fw.write("Returning 510 sized buffer at address " + addressFor512 + ".\n");
			for (int i = 0; i < 3; i++) {
				BufferManager.returnAddress(additionalAddresses[i]);
				fw.write("Returning 510 sized buffer at address " + additionalAddresses[i] + ".\n");
			}
			fw.write("\nFree Buffer Count:\n" + BufferManager.status() + "\n");
			fw.write(BufferManager.tight()? "Status: Tight\n\n": "Status: OK\n\n");
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
				
	}
	
	public static void expectedValues(int[] expectedValues) {
		if (expectedValues.length != 7) {
			return;
		}
		try {
			fw.write("Expected Values:\n" + expectedValues[0] + " 510 size buffers, " + expectedValues[1] + " 254 size buffers, " 
					+ expectedValues[2] + " 126 size buffers, " + expectedValues[3] + " 62 size buffers, "
					+ expectedValues[4] + " 30 size buffers, " + expectedValues[5] + " 14 size buffers, and " 
					+ expectedValues[6] + " 6 size buffers.\n\n");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
}
