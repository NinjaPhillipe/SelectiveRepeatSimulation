package reso.examples.selectiverepeat;

public class SlowStart extends CongestionControl {

    private int inc = 1;

    public SlowStart(SelectiveRepeatProtocol srProto){
        super(srProto);
    }

    @Override
    public void control() {
        inc = inc*2;
    }
}
