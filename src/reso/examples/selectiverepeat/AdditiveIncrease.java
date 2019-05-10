package reso.examples.selectiverepeat;

/**
 * Classe qui implémente la stratégie Additive Increase
 */
public class AdditiveIncrease extends CongestionControl {

    private double size = protocol.getSendingWindow().size;

    /**
     * Constructeur de la strategie Additive Repeat
     * @param srProto protocol sur lequel appliquer la congestion
     */
    AdditiveIncrease(SelectiveRepeatProtocol srProto){
        super(srProto);
    }

    @Override
    public void control() {
        double tmp = 1 /size; //((float)protocol.getSendingWindow().size);
        size += tmp;
        System.out.println("ADDITIVE IN _CREASE size :" + size);
        if ((int) size > protocol.getSendingWindow().size) {
            protocol.incrSize();
        }
        protocol.logSize(size);
    }
}
