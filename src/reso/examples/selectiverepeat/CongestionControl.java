package reso.examples.selectiverepeat;

public  abstract class CongestionControl {

    public abstract void control();
    public SelectiveRepeatProtocol protocol;
    public int sstresh = 4;
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

    public void ACKDuplicate3Times(){
        System.out.println("ACK 3 DUPLI");
        this.sstresh = protocol.getReceiveWindow().size / 2;
        FifoWindow window = this.protocol.getReceiveWindow();
        this.protocol.switchToAdditiveIncrease();

        FifoBuffer buf = window.split();
        buf.fuse(protocol.getBuffer());
//        this.protocol.switchToSlowStart();
    }
    public void timeout(){
        // set size 1
        System.out.println("INFO BEFORE TIMEOUT EXECUTION \n SENDING WINDOW \n"+protocol.getSendingWindow()+"\n BUFFER \n"+protocol.getBuffer()+"\n");

        FifoBuffer tmp = new FifoBuffer();
        tmp.head = protocol.getSendingWindow().head.next;
        tmp.tail = protocol.getSendingWindow().tail;
//        System.out.println(tmp);

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
        this.protocol.switchToSlowStart();

    }

    public int getSstresh() { return sstresh; }
}
