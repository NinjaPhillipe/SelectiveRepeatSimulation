package reso.examples.selectiverepeat;

public class AdditiveIncrease extends CongestionControl {

    double size = protocol.getSendingWindow().size;
    public AdditiveIncrease(SelectiveRepeatProtocol srProto){
        super(srProto);
    }

    @Override
    public void control() {
        double tmp = 1/size;
        size += tmp;
        System.out.println("ADDITIVE IN _CREASE size :"+size);
        if ((int) size  >protocol.getReceiveWindow().size )
        {
            protocol.incrSize();
            protocol.logSize(size);
        }
    }
}
