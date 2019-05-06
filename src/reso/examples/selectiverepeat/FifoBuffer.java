package reso.examples.selectiverepeat;

public class FifoBuffer {

    class Node{
        public SelectiveRepeatMessage data;
        public Node next;
        public boolean ack;
        public Node(SelectiveRepeatMessage data){
            this.data = data;
        }
    }

    public Node head;
    public Node tail;

    public FifoBuffer(){

    }

    public void add(SelectiveRepeatMessage msg){
        if(head == null){
            head = new Node(msg);
            tail = head;
        }else{
        tail.next = new Node(msg);
        tail = tail.next;
        }
    }

    public SelectiveRepeatMessage pop(){
        if(head !=null){
            SelectiveRepeatMessage msg = head.data;
            head = head.next;
            return msg;
        }
        return null;
    }

    public static void main(String[] args){
        FifoBuffer fif = new FifoBuffer();
        fif.add(new SelectiveRepeatMessage(1));
        fif.add(new SelectiveRepeatMessage(2));
        System.out.println(fif.head.data);
        System.out.println(fif.tail.data);

        System.out.println(fif.pop());

        System.out.println(fif.head.data);

    }




}
