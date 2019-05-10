package reso.examples.selectiverepeat;

/**
 * classe qui représente un buffer de type.
 * First In First Out.
 * @param <E>
 */
public class FifoBuffer <E>{

    /**
     * Inner class qui s'occupe de garder en mémoire un paquet ainsi que
     * l'état de acquittement.
     */
    class Node{
        public E data;
        public Node next;
        public boolean ack = false;
        public Node(E data){
            this.data = data;
        }
    }

    public Node head;
    public Node tail;

    public FifoBuffer(){

    }

    /**
     * Méthode qui permet d'ajouter un élément au buffer fifo
     * @param data
     */
    public void add(E data){
        if(head == null){
            head = new Node(data);
            tail = head;
        }else{
//            System.out.println(tail);
            tail.next = new Node(data);
            tail = tail.next;
        }
    }

    /**
     * permet de fusionner deux buffer fifo
     * en ajoutant le buffer passer en paramètre a
     * la fin du buffer actuel.
     * @param buf
     */
    public void fuse(FifoBuffer buf)
    {
        this.tail.next = buf.head;
        this.tail = buf.tail;
    }

    /**
     * Permet de détacher l'élément en tete du buffer fifo.
     * L'élément sera retirer du buffer.
     *
     * @return
     */
    public E pop(){
        if(head !=null){
            E data = head.data;
            head = head.next;
            return data;
        }
        return null;
    }

    public String toString() {
        String res = "{";
        Node tmp = head;
        while (tmp != null){
            res+= ( tmp.data +" " + tmp.ack + " , ");
            tmp = tmp.next;
        }
        res += "}";
        return res;
    }

    public static void main(String[] args){
        FifoBuffer<SelectiveRepeatMessage> fif = new FifoBuffer();
        fif.add(new SelectiveRepeatMessage(1));
        fif.add(new SelectiveRepeatMessage(2));

        System.out.println(fif.head.data); // 1
        System.out.println(fif.tail.data); // 2

        System.out.println(fif.pop());     // 1

        System.out.println(fif.head.data); // 2

    }




}
