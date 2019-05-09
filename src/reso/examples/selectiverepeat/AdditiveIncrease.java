package reso.examples.selectiverepeat;

public class AdditiveIncrease extends CongestionControl {

    double size = protocol.getReceiveWindow().size;
    public AdditiveIncrease(SelectiveRepeatProtocol srProto){
        super(srProto);
    }

    @Override
    public void control() {
        double tmp = 1/size;
        size += tmp;
        if ((int) size  >protocol.getReceiveWindow().size )
        {
            protocol.getReceiveWindow().size = (int )size;
        }
    }
}
