package reso.examples.selectiverepeat;

/**
 * Fifo windows est une extension de FifoBuffer qui permet une gestion de la taille de la fenetre.
 * @param <E>
 */
public class FifoWindow<E> extends FifoBuffer<E> {


    public int size = 0;

    /**
     * Permet de séparer une fenetre en deux partie.
     * La partie de gauche restera dans cette objet
     * tandis que la partie de droite sera retournée
     * dans un nouveau fifo
     * @return
     */
    FifoWindow<E> split()
    {
        int medium = this.size/ 2;
        FifoWindow<E> window = new FifoWindow<>();
        int j = 0;
        Node tmp = head;
        while ( j < medium -1)
        {
            tmp= tmp.next;
            j++;
        }
        window.head = tmp.next;
        window.tail = this.tail;
        this.tail = tmp;
        tail.next = null;
        int windowsize =  this.size - this.size/2 ;
        this.size = this.size /2;
        window.size = windowsize;
        return window;
    }

    /**
     * Idem que le split au dessus mais avec un paramètre
     * @param index index a partir du quel on doit separer la fifo window
     * @return
     */
    public FifoWindow<E> split(int index)
    {
        FifoWindow<E> window = new FifoWindow<>();
        int j = 0;
        Node tmp = head;
        while ( j < index -1)
        {
            tmp= tmp.next;
            j++;
        }
        window.head = tmp.next;
        window.tail = this.tail;
        this.tail = tmp;
        tail.next = null;
        window.size = this.size - index;
        this.size -= index;

        return window;

    }

    @Override
    public void add(E data){
        super.add(data);
        size++;
    }

    @Override
    public E pop(){
        if(head !=null){
            E data = head.data;
            head = head.next;
            size--;
            return data;
        }
        return null;
    }

    /**
     * Permet de mettre une valeur a un ack d'un node en fonction de sa position.
     * @param i position dans la fifoWindow
     * @param isAck valeur de l'ack a attribuer
     */
    void setAck(int i, boolean isAck){
        Node tmp = head;
        if(i<size){
            for(int j = 0 ; j<i ; j++){
                tmp = tmp.next;
            }
            tmp.ack = isAck;
        }
    }

    public boolean isAck(int i){
        Node tmp = head;
        if(i<size){
            for(int j = 0 ; j<i ; j++){
                tmp = tmp.next;
            }
            return tmp.ack;
        }
        return false;
    }

    void setData(E data, int i){
        if(head == null) {
            head = new Node(data);
            tail = head;
        }else {
        Node tmp = head;

        while( !(i<size) ){
            this.add(null); // ajoute un message null
        }

        for(int j = 0 ; j<i ; j++){
            tmp = tmp.next;
        }
//        if(tmp!=null)
            tmp.data = data;
        }

    }

    public E get(int i ){
        if(i> size)
            return null;
        Node tmp = head;
        for(int j = 0 ; j < i ; j++ ){
//            System.out.println(tmp.data);
            tmp = tmp.next;
        }
        if(tmp !=null)
            return tmp.data;
        return null;
    }


    public static void main ( String [] args)
    {
        FifoWindow<String> pp = new FifoWindow<>();
        pp.add("UN");
        pp.add("deux");
        pp.add("trois");
        pp.add("quatre");
            pp.add("cinq");
            pp.add("six");
        System.out.println(pp);
        FifoWindow<String> ok =pp.split(2);
        System.out.println(pp);
        System.out.println(ok);
    }
}
