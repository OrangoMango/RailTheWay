package com.orangomango.railway;

public class Util{
	public static byte invertDirection(byte dir){
		if ((dir & 8) == 8){
			return (byte)2;
		} else if ((dir & 4) == 4){
			return (byte)1;
		} else if ((dir & 2) == 2){
			return (byte)8;
		} else if ((dir & 1) == 1){
			return (byte)4;
		} else {
			throw new IllegalStateException("Not a valid direction: "+dir);
		}
	}

	public static byte invertBits(byte dir){
		return (byte)(dir ^ 15);
	}
}