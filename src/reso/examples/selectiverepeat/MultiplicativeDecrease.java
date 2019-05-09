package reso.examples.selectiverepeat;

import java.util.Stack;

public class MultiplicativeDecrease extends CongestionControl {

    public MultiplicativeDecrease(SelectiveRepeatProtocol srProto){
        super(srProto);
    }

    @Override
    public void control() {
        FifoWindow window = this.protocol.getReceiveWindow();
        int medium = window.size / 2;


        this.protocol.switchToAdditiveIncrease();
        for (int i= 0 ; i < window.size ; i++)
        {
            Stack stack = new Stack<>() ;
            if (i > medium)
            {
                // pas fini
            }

        }
    }
}
