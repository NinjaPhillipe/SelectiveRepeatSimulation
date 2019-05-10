package reso.examples.selectiverepeat;

public  abstract class CongestionControl {

    public abstract void control();
    public SelectiveRepeatProtocol protocol;
    public static int sstresh = 4;
    public int count = 0;

    public CongestionControl(SelectiveRepeatProtocol srProto){
        protocol = srProto;
    }

    public void reset()
    {
        this.count = 0;
    }

    public void changeReceiveWindowSize()
    {

    }

    /**
     * S'occupe de gérer le cas lorsqu'on a 3 ACK dupliquer.
     * (Utilisation de SACK car il n'y a pas d'ack dupliquer avec Selective Repeat)
     *
     * La méthode va diviser la taille de la fenetre d'envoi par deux.
     * Et de replacer les packets en trop dans le buffer.
     */
    public void ACKDuplicate3Times(){
        System.out.println("ACK 3 DUPLI");
        sstresh = protocol.getSendingWindow().size / 2;
        FifoWindow window = this.protocol.getSendingWindow();
        this.protocol.switchToAdditiveIncrease();

        FifoBuffer buf = window.split();
        buf.fuse(protocol.getBuffer());
        protocol.setBuffer(buf);

        FifoWindow<TimeoutEvent> timer = protocol.getTimeoutBuffer().split();
        FifoBuffer<TimeoutEvent>.Node tmp = timer.head;
        while(tmp!=null){
            if(tmp.data!=null)
                tmp.data.stop();
            tmp=tmp.next;
        }

        this.protocol.switchToAdditiveIncrease();
        protocol.logSize();
    }

    /**
     *
     */
    public void timeout(){
        // set size 1
        System.out.println("INFO BEFORE TIMEOUT EXECUTION \n SENDING WINDOW \n"+protocol.getSendingWindow()+"\n BUFFER \n"+protocol.getBuffer()+"\n");

        // on mets les packets en trop pour la nouvelle taille de fenetre dans le buffer
        FifoBuffer tmp = new FifoBuffer();
        tmp.head = protocol.getSendingWindow().head.next;
        tmp.tail = protocol.getSendingWindow().tail;
        if(tmp.head !=null) {
            tmp.fuse(protocol.getBuffer());
            System.out.println("TIMEOUT BUFFER \n " + tmp);
            protocol.setBuffer(tmp);
        }


        FifoWindow<SelectiveRepeatMessage> pp = new FifoWindow<>();
/*
        protocol.getSendingWindow().head.next = null;
*/
        pp.add(protocol.getSendingWindow().head.data);
        System.out.println("TIMEOUT SENDING WINDOW \n "+pp);
        protocol.setSendingWindow(pp);
        //protocol.getSendingWindow().tail = protocol.getSendingWindow().head;

        FifoWindow<TimeoutEvent> tim = protocol.getTimeoutBuffer();
        FifoBuffer<TimeoutEvent>.Node tmp2 = tim.head;
        for(int i = 0 ; i< tim.size ; i++){
            tmp2 = tmp2.next;
            if(tmp2!=null && tmp2.data!=null) {
                tmp2.data.stop();
//                tmp2.data = null;
            }
        }
        tim.tail = tim.head;
        tim.head.next = null;
        tim.size=1;

//        SelectiveRepeatProtocol.setCwnd(1);
        try{
            this.protocol.reSend(tim.head.data.id);
        }catch (Exception e){
            e.printStackTrace();
        }
        protocol.logSize();
        this.protocol.switchToSlowStart();

    }
}
