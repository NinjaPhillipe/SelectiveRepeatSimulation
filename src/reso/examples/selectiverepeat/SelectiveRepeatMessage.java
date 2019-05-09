package reso.examples.selectiverepeat;

import reso.common.Message;

public class SelectiveRepeatMessage
implements Message {

	public final int num;
	public boolean isAck = false;


	public int exceptedSeq = 0;

	public String data;


	public SelectiveRepeatMessage(int num) {
		this.num= num;
	}
	public SelectiveRepeatMessage(int num , String data_) {
		this(num);
		data = data_;
	}
	public SelectiveRepeatMessage(int num , boolean isAck_) {
		this(num);
		isAck = isAck_;
	}
	public SelectiveRepeatMessage(int num ,int exceptedSeq_) {
		this(num,true);
		exceptedSeq = exceptedSeq_;
	}

	
	public String toString() {
		return "SR [num=" + num + " ACK=" + isAck +" DATA=" +data + "]";
	}

	@Override
	public int getByteLength() {
		// The ping-pong message carries a single 'int'
		return Integer.SIZE / 8;
	}

}
