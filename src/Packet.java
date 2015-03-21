/**
 * Packet.java
 * 
 * @author Adiba Arif
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Packet {
	public static final byte PKT_Byte1=0;
	public static final byte PKT_Byte2 = 1;
	public static final byte ACK_Byte3 = 4;
	public static final byte PKT_Error = 5;
	public static final byte PKT_Data = 3;
	public byte PKT_Block1;
	public byte PKT_Block2;
	public byte PKT_ErrorCode1;
	public byte PKT_ErrorCode2;
	public String mode;
	public String filename;
	public static final int MaxBufferSize = 512;
	private int size;
	public byte[] setData;
	public byte[] setACK = new byte[4];
	File file;
	FileOutputStream fs;

	/**
	 * Packet constructor
	 * @param Default_filename
	 * @param Default_Mode
	 * @throws java.io.IOException
	 */
	public Packet(String Default_filename, String Default_Mode) throws IOException {
		size = 4+(Default_filename.getBytes().length)+(Default_Mode.getBytes().length);
		setData=new byte[size];
		filename = Default_filename;
		mode = Default_Mode;
		file = new File(filename);
		fs= new FileOutputStream (filename);
	}


	/**
	 * function which sends the packet
	 */
	public void MakePacket_Send() {
		int index =0;
		byte[] fn = filename.getBytes();
		byte[] newmode = mode.getBytes();
		setData[index++]=PKT_Byte1;
		setData[index++]=PKT_Byte2;
		for (int i=0; i<fn.length;i++){
			setData[index++]= fn[i];
		}
		setData[index++]=PKT_Byte1;
		for (int i=0; i<newmode.length;i++){
			setData[index++]= newmode[i];
		}
		setData[index++]=PKT_Byte1;
	}

	/**
	 * Packet_Break function which break the data from the packet
	 * @param ReceiveData
	 * @param length
	 * @throws java.io.IOException
	 */
	public void Packet_Break(byte[] ReceiveData, int length) throws IOException{
		if (ReceiveData[1]==PKT_Data){
			PKT_Block1=ReceiveData[2];
			PKT_Block2=ReceiveData[3];
			byte[] Data=new byte[(length)-4];
			for(int i=4; i<length; i++){
				Data[i-4]=ReceiveData[i];
			}
			fs.write(Data);
		}
		else if (ReceiveData[1]==PKT_Error){
			PKT_ErrorCode1=ReceiveData[2];
			PKT_ErrorCode2=ReceiveData[3];
			byte[] Data = new byte[(ReceiveData.length)-5];
			for (int j=4; j<ReceiveData.length-1;j++){
				Data[j-4]=ReceiveData[j];
			}
			String ErrorInPacket=new String(Data);
			System.out.println("Error Code "+PKT_ErrorCode1+" "+PKT_ErrorCode2+": "+ErrorInPacket);
		}
	}

	/*
	 * close function which closes the fileoutputstream
	 */
	public void close() throws IOException{
		fs.close();
	}

	/**
	 * ACK packet
	 */
	public void MakePacket_ACK() {
		int index =0;
		setACK[index++]=PKT_Byte1;
		setACK[index++]=ACK_Byte3;
		setACK[index++]=PKT_Block1;
		setACK[index++]=PKT_Block2;
	}
}




