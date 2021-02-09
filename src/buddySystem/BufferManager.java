package buddySystem;
/**
 * BufferManager class represents a Buddy Buffer system which gives out 
 * 	7 differently sized buffers from 6 to 510 from an initial memory pool 
 * 	of ten 510 word blocks.  2 words are used as the control word for each
 *  buffer.
 *  
 * @author Luke Newman 12/5/2020
 *
 */
public class BufferManager {
	// instance field that represents the total memory available to give out
	static int[] memorySpace;
	// free buffer count
	static int[] statusCounter;
	
	/**
	 * Method to initialize the memory pool.
	 */
	public static void initialize() {
		// buffers to be allocated from this fixed size area
		memorySpace = new int[5120];
		// an array to keep track of how many buffers of each size are free
		statusCounter = new int[7];
		// initialize ten 512 word buffers
		for (int i = 0; i < 10; i++) {
			// set control word to size of the buffers
			memorySpace[i * 512] = 512;
			// set other control word to indicate it is unused
			memorySpace[(i * 512) + 1] = 0;
		}
		// set the number of 510 word blocks available.
		statusCounter[6] = 10;
	}
	
	/**
	 * Method to request a buffer of a particular size.
	 * 
	 * @param bufferSize
	 * @return the address of the buffer given out.  -1 means not available, -2 means invalid request
	 */
	public static int request(int bufferSize) {
		
		if (bufferSize > 510) {
			return -2;
		}
		
		int powerOfBuffer;
		int sizeOfBuffer;
		int address = 0;
		
		if (bufferSize > 2) {
			powerOfBuffer = (int) Math.ceil(Math.log(bufferSize + 2)/Math.log(2));
			sizeOfBuffer = (int) Math.pow(2, powerOfBuffer);
		} else if (bufferSize > 0) {
			powerOfBuffer = 3;
			sizeOfBuffer = 8;
		} else {
			return -2;
		}
		
		/*
		 * check status of free blocks first to see if their are blocks of this size available
		 */
		if (statusCounter[powerOfBuffer - 3] > 0) {
			
			while(address < memorySpace.length) {
				// if the address is not being used already
				if (memorySpace[address + 1] == 0) {
					if (memorySpace[address] == sizeOfBuffer) {
						// mark as used and return address
						memorySpace[address + 1] = 1;
						--statusCounter[powerOfBuffer - 3];
						return address + 2;
					} else {
						address += memorySpace[address];
					}
				} else {
					address += memorySpace[address];
				}
			}
		}
		
		address = 0;
		
		while (address < memorySpace.length) {
			
			// if the address is not being used already
			if (memorySpace[address + 1] == 0) {
				/*
				if (memorySpace[address] == sizeOfBuffer) {
					// mark as used and return address
					memorySpace[address + 1] = 1;
					--statusCounter[powerOfBuffer - 3];
					return address + 2;
				}
				*/
				if (memorySpace[address] > sizeOfBuffer) {
					--statusCounter[(int) Math.ceil(Math.log(memorySpace[address])/Math.log(2) - 3)];
					while (memorySpace[address] > sizeOfBuffer) {
						// create a pair of smaller size buffers
						memorySpace[address] = memorySpace[address]/2;
						memorySpace[address + memorySpace[address]] = memorySpace[address];
						++statusCounter[(int) Math.ceil(Math.log(memorySpace[address])/Math.log(2) - 3)];
					}
					// mark as used and return address
					memorySpace[address + 1] = 1;
					return address + 2;
				} else {
					// size of buffer in address must be smaller than the size of the buffer requests
					// any equal sized buffers at this point in the code are already used
					// keep moving along the address space
					address += memorySpace[address];
				}
			} else {
				// this address is being used 
				// keep moving along the address space
				address += memorySpace[address];
			}
				
		}
		return -1;
	}
	
	/**
	 * Method to return address to memory pool.  
	 * 
	 * @param address
	 */
	public static void returnAddress(int address) {
		// get address of control word
		address = address - 2;
		// mark as unused
		memorySpace[address + 1] = 0;
		// get the size of the buffer
		int size = memorySpace[address];
		// get the power
		int power = (int) Math.ceil(Math.log(size)/Math.log(2));
		
		// recombine
		boolean recombining = true;
		while (size < 512 && recombining) {
			if (address % (size * 2) == 0) {
				// we are in lower bound of larger block size
				// if upper bound block is unused and the same size cascade up
				if (memorySpace[address + size] == size && memorySpace[address + size + 1] == 0) {
					memorySpace[address] = size * 2;
					--statusCounter[power - 3];
					size = size * 2;
					power++;
					
				} else { // else we leave as is
					recombining = false;
					++statusCounter[power - 3];
				}
			} else {
				// we are in upper bound of larger block size
				// if lower bound block is unused and the same size cascade up
				if (memorySpace[address - size] == size && memorySpace[address - size + 1] == 0) {
					memorySpace[address - size] = size * 2;
					memorySpace[address] = 0;
					address = address - size;
					--statusCounter[power - 3];
					size = size * 2;
					power++;
					
				} else { // else we leave as is
					recombining = false;
					++statusCounter[power - 3];
				}
			}
			
		}
		// increment 510 word blocks if we've reached the top while recombining
		if (size == 512) {
			++statusCounter[power - 3];
		}
	}
	
	/**
	 * Method to return status of free buffer count.  
	 * 
	 * @return true if tight, false if not
	 */
	public static boolean tight() {
		return statusCounter[6] < 2;
	}
	
	/**
	 * Method to return a list of free buffers in the memory pool.
	 * @return String
	 */
	public static String status() {
		return statusCounter[6] + " 510 size buffers\n" + statusCounter[5] + " 254 size buffers\n" +
				statusCounter[4] + " 126 size buffers\n" + statusCounter[3] + " 62 size buffers\n" + 
				statusCounter[2] + " 30 size buffers\n" + statusCounter[1] + " 14 size buffers\n" + 
				statusCounter[0] + " 6 size buffers\n";
	}
}
