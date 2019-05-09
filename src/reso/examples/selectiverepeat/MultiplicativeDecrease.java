package reso.examples.selectiverepeat;

import java.util.Stack;

public class MultiplicativeDecrease extends CongestionControl {

    public MultiplicativeDecrease(SelectiveRepeatProtocol srProto){
        super(srProto);
    }

    @Override
    public void control() {
        this.sstresh = protocol.getReceiveWindow().size / 2;
        FifoWindow window = this.protocol.getReceiveWindow();
        int medium = window.size / 2;
        this.protocol.switchToAdditiveIncrease();
        FifoBuffer buf = window.split(medium);
        buf.fuse(protocol.getBuffer());
        this.protocol.switchToSlowStart();
    }
}
