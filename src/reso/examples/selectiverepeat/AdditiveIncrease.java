package reso.examples.selectiverepeat;

public class AdditiveIncrease extends CongestionControl {

    double size = protocol.getSendingWindow().size;
    public AdditiveIncrease(SelectiveRepeatProtocol srProto){
        super(srProto);
    }

    @Override
    public void control() {
        if (protocol.getSendingWindow().size == 0)
            protocol.switchToSlowStart();
        else {
            double tmp = 1 / ((float)protocol.getSendingWindow().size);
            size += tmp;
            System.out.println("ADDITIVE IN _CREASE size :" + size);
            if ((int) size > protocol.getSendingWindow().size) {
                protocol.incrSize();
            }
            protocol.logSize(size);

        }
    }
}
