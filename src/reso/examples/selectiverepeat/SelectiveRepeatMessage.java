package reso.examples.selectiverepeat;

import reso.common.Message;

public class SelectiveRepeatMessage
implements Message {

	private final int num;
	boolean isAck = false;


	int expected = 0;

	String data;


	SelectiveRepeatMessage(int num) {
		this.num= num;
	}
	SelectiveRepeatMessage(int num, String data_) {
		this(num);
		data = data_;
	}
//	public SelectiveRepeatMessage(int num , boolean isAck_) {
//		this(num);
//		isAck = isAck_;
//	}
	SelectiveRepeatMessage(int num, int exceptedSeq_) {
		this(num);
		isAck = true;
		expected = exceptedSeq_;
	}

	
	public String toString() {
		return "SR [num=" + num + " ACK=" + isAck + " EXPECTED="+expected +" DATA=" +data + "]";
	}

	@Override
	public int getByteLength() {
		// The ping-pong message carries a single 'int'
		return Integer.SIZE / 8;
	}

	int getNum() {
		return num;
	}
}
