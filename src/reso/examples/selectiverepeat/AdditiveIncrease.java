package reso.examples.selectiverepeat;

public class AdditiveIncrease extends CongestionControl {

    double size = protocol.getReceiveWindow().size;
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
