package reso.examples.selectiverepeat;

public  abstract class CongestionControl {

    public abstract void control();
    public SelectiveRepeatProtocol protocol;
    public int sstresh = 6;
    public int count = 0;

    public CongestionControl(SelectiveRepeatProtocol srProto){
        protocol = srProto;
    }

    public void reset()
    {
        this.count = 0;
    }

    public void changeReceiveWindowSize()
    {

    }

    public void ACKDuplicate3Times(){
        this.sstresh = protocol.getReceiveWindow().size / 2;
        FifoWindow window = this.protocol.getReceiveWindow();
        int medium = window.size / 2;
        this.protocol.switchToAdditiveIncrease();
        FifoBuffer buf = window.split();
        buf.fuse(protocol.getBuffer());
        this.protocol.switchToSlowStart();
    }
    public void timeout(){

    }
}
