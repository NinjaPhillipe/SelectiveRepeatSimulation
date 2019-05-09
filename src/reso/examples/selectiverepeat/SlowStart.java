package reso.examples.selectiverepeat;

public class SlowStart extends CongestionControl {

    private int inc = 1;

    public SlowStart(SelectiveRepeatProtocol srProto){
        super(srProto);
    }

    @Override
    public void control() {
        if( protocol.getSendingWindow().size<this.getSstresh() ){
            for(int j = 0 ; j < inc ; j++ ){
                protocol.incrSize();
            }
        }
//        else {
//            System.exit(1);
//        }
        inc = inc*2;
    }
}
