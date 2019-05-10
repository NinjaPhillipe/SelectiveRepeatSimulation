package reso.examples.selectiverepeat;

public class SlowStart extends CongestionControl {


    public SlowStart(SelectiveRepeatProtocol srProto){
        super(srProto);
    }

    @Override
    public void control() {
        if( protocol.getSendingWindow().size<CongestionControl.sstresh ){
            protocol.incrSize();
            protocol.logSize();
        }
        else{
            System.out.println("SWITCH FROM SLOW START TO ADDITiVE INCREASE");
            protocol.switchToAdditiveIncrease();
        }
    }
}