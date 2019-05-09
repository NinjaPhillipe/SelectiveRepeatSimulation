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
}
