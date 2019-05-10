package reso.examples.selectiverepeat;

/**
 * Classe qui implémente la strategie de congestion de controle SlowStart.
 */
public class SlowStart extends CongestionControl {

    /**
     * Méthode qui permet d'instancier la stratégie Slow Start.
     * @param srProto protocole sur lequel appliquer la stratégie.
     */
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