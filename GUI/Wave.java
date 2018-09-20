package GUI;

import java.io.*;

public class Wave {
	
	public int numSamples; 
	public int time; //the duration of the audio
	public int maxvalue; //maximum sample value in the data
	//BUFFERS;
	public byte[] input; //buffer for the 4 bytes input
	public byte[] smallinput; //buffer for the 2 bytes input
	
	
	//HEADER:
	//chunk descriptor:
	public int chunkSize; //5-8
	//fmt subchunk:
	public int pcmFlag = 1; //21-22 - assume its pcm format
	public int numChannels = 1; //23-24 - assume its mono file
	public int sampleRate; //25-28
	public int byteRate; //29-32
	public int blockAlign; //33-34
	public int bitsPerSample; //35-36
	//data subchunk:
	public int dataSize; //41-44
	
	
	//DATA:
	public byte[] data; //array to hold the converted sample values 
	
	//constructor:
	public Wave(){
		numSamples = 0;
		input = new byte[4]; //buffer for 4 bytes
		smallinput = new byte[2]; //buffer for 2 bytes
	}
	
	//methods:
	public void readWaveHead(String filename) throws IOException {
		
		File file = new File(filename);
		FileInputStream fileInput =  null;
		BufferedInputStream buffer = null;
	
		try{
			fileInput = new FileInputStream(file);
			buffer = new BufferedInputStream(fileInput, 65580);
			//CHUNKSIZE
			buffer.skip(4); //skip the riff header
			buffer.read(input, 0, 4); //store data in the buffer
			chunkSize = 
					(
							((int) input[0] & 0xFF) 
							| (((int) input[1] & 0xFF) << 8) 
							| (((int) input[2] & 0xFF) << 16) 
							| (((int) input[3] & 0xFF) << 24)
							); //convert little endian to big endian
			System.out.println("chunkSize: " + chunkSize);
			//PCMFLAG
			buffer.skip(12); //skip the riff, wave headers and fmtsize
			buffer.read(smallinput, 0, 2); //store data in the small buffer
			int pcm = 
					(
							((int) smallinput[0] & 0xFF) 
							| (((int) smallinput[1] & 0xFF) << 8) 
							);
			if(pcm != pcmFlag) {
				System.out.println("The file is not in PCM, pcm is: " + pcm);
			}
			System.out.println("pcmFlag: " + pcm);
			//NUMCHANNELS
			smallinput = new byte[2]; //reinitialize buffer for 2 bytes
			buffer.read(smallinput, 0, 2); //store data in the small buffer
			int channels = 
					(
							((int) smallinput[0] & 0xFF) 
							| (((int) smallinput[1] & 0xFF) << 8) 
							);
			if(channels != numChannels) {
				System.out.println("The file is not mono, channels are: " + channels);
			}
			System.out.println("numChannels: " + channels);
			//SAMPLERATE
			input = new byte[4]; //reinitialize buffer for 4 bytes
			buffer.read(input, 0, 4); //store data in the buffer
			sampleRate = 
					(
							((int) input[0] & 0xFF) 
							| (((int) input[1] & 0xFF) << 8) 
							| (((int) input[2] & 0xFF) << 16) 
							| (((int) input[3] & 0xFF) << 24)
							); //convert little endian to big endian
			System.out.println("sampleRate: " + sampleRate);
			//BYTERATE
			input = new byte[4]; //reinitialize buffer for 4 bytes
			buffer.read(input, 0, 4); //store data in the buffer
			byteRate = 
					(
							((int) input[0] & 0xFF) 
							| (((int) input[1] & 0xFF) << 8) 
							| (((int) input[2] & 0xFF) << 16) 
							| (((int) input[3] & 0xFF) << 24)
							); //convert little endian to big endian
			System.out.println("byteRate: " + byteRate + " bit rate:" + (byteRate*8));
			//BLOCKALIGN = # of bytes per sample
			smallinput = new byte[2]; //reinitialize buffer for 4 bytes
			buffer.read(smallinput, 0, 2); //store data in the buffer
			blockAlign = 
					(
							((int) smallinput[0] & 0xFF) 
							| (((int) smallinput[1] & 0xFF) << 8) 
							); //convert little endian to big endian
			System.out.println("blockAlign: " + blockAlign);
			//BITSPERSAMPLE
			smallinput = new byte[2]; //reinitialize buffer for 4 bytes
			buffer.read(smallinput, 0, 2); //store data in the buffer
			bitsPerSample = 
					(
							((int) smallinput[0] & 0xFF) 
							| (((int) smallinput[1] & 0xFF) << 8) 
							); //convert little endian to big endian
			System.out.println("bitsPerSample: " + bitsPerSample);
			//DATASIZE
			buffer.skip(4); //skip the data headers
			input = new byte[4]; //reinitialize buffer for 4 bytes
			buffer.read(input, 0, 4); //store data in the buffer
			dataSize = 
					(
							((int) input[0] & 0xFF) 
							| (((int) input[1] & 0xFF) << 8) 
							| (((int) input[2] & 0xFF) << 16) 
							| (((int) input[3] & 0xFF) << 24)
							); //convert little endian to big endian
			System.out.println("dataSize: " + dataSize);
			
			//SAMPLES DATA
			buffer.skip(1);
			maxvalue = 0;
			numSamples = (8 * dataSize) / bitsPerSample;
			System.out.println("number of samples: " + numSamples);
			data = new byte[numSamples];
			
			int low = 0; //lowest allowed value
			int high = 0;//highest allowed value
			//check the quantization rate
			if(bitsPerSample == 8) {//assign the relevant range of values
				System.out.println("Case 1");
				low = -128;
				high = 127;
			}else if (bitsPerSample == 16) {
				System.out.println("Case 2");
				low = -32760;
				high = 32760;
			}
			System.out.println("Valid range of values: " + low + " - " + high);
			
			byte a = 0;
			byte b = 0;
			
			int value = 0; //tmp for holding converted sample
			int i = 0;
			
			
			while((buffer.available() > 0) && numSamples < 65536) { //check each sample
				
				if(bitsPerSample == 16)
				//read 2 bytes:
				a = (byte) buffer.read(); 
				b = (byte) buffer.read(); 
				
				value = (b << 8) | a;
				
				data[i] = (byte)value; //store that value in the data array
				i++;
				if(value > maxvalue) {
					maxvalue = value; 
				}
			}
			System.out.println("The data was read successfully!");
			System.out.println("Maximum value: " + maxvalue);
			
		}catch(IOException e) {
			//do something
			e.printStackTrace();
		}finally {
			System.out.println("closing file...");
			if(fileInput!=null) fileInput.close();
			if(buffer!=null) buffer.close();
			}
	}
	
	
	//calculte the total duration of the wave file:
	public void duration() {
		time = chunkSize / byteRate;
	}

}


