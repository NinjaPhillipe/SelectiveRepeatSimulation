package reso.examples.selectiverepeat;

import reso.ip.*;

public class SelectiveRepeatProtocol implements IPInterfaceListener {

	public static final int IP_PROTO_SR = Datagram.allocateProtocolNumber("SelectiveRepeat");
	
	private final IPHost host;

	private boolean allPcktSendedOnce;

	private IPAddress dst;

	private int size = 4;

	// SR sender
	private int send_base = 0;
	private int next_seq_num = 0;
	private FifoWindow<SelectiveRepeatMessage> sendingWindow = new FifoWindow();

	// SR receiver
	private int recv_base = 0;
	private FifoWindow<SelectiveRepeatMessage> receiveWindow = new FifoWindow();


	// BUFFER de paquets
	private FifoBuffer<SelectiveRepeatMessage> buffer = new FifoBuffer();

	public SelectiveRepeatProtocol(IPHost host) {
		this.host= host;
		for(int i = 0 ; i < size ; i++ ){
			receiveWindow.add(null);
		}
	}
	
	@Override
	public void receive(IPInterfaceAdapter src, Datagram datagram) throws Exception {

    	SelectiveRepeatMessage msg= (SelectiveRepeatMessage) datagram.getPayload();
		System.out.println("receive" + msg);

		if(!msg.isAck){ // c'est le receveur qui recoit un packet

			///////////////////////////////////////////////////////////////////////////////////////////////////
			//                             Si le receveur recoit un paquet                                   //
			///////////////////////////////////////////////////////////////////////////////////////////////////
			if(recv_base <= msg.num && msg.num <= recv_base+size-1 ){
				host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_SR, new SelectiveRepeatMessage(msg.num,true));
//				String data = msg.data;
				receiveWindow.setData(msg,msg.num-recv_base);
//				System.out.println("reception du package "+msg);
				if(recv_base == msg.num ){
					System.out.println("\nRECEIVE WINDOW: \n"+ receiveWindow +"\n");

					// delivrer
					AppReceiver.recv+=msg.data;
					System.out.println("Data delivered : " + msg.data);
					System.out.println("ReceiverData : " + AppReceiver.recv);

					receiveWindow.pop();
					receiveWindow.add(null);
					recv_base++;

					while (receiveWindow.head.data!=null){

						System.out.println("\nRECEIVE WINDOW: \n"+ receiveWindow +"\n");

						// delivrer
						AppReceiver.recv+=msg.data;
						System.out.println("Data delivered : " + msg.data);
						System.out.println("ReceiverData: " + AppReceiver.recv);

						receiveWindow.pop();
						receiveWindow.add(null);
						recv_base++;


					}
				}else {
					receiveWindow.setData(msg,msg.num);
				}
			}else if(recv_base-size <= msg.num && msg.num <= recv_base-1){
				// renvoi un ACK qui a du etre perdu
				host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_SR, new SelectiveRepeatMessage(msg.num,true));
			}
//			System.out.println("\n is not ack " + msg.num);
		}else { // si c'est un ACK

			///////////////////////////////////////////////////////////////////////////////////////////////////
			//                             Si l'envoyeur recoit un ACK                                       //
			///////////////////////////////////////////////////////////////////////////////////////////////////
			if(msg.num >=send_base && msg.num <= send_base+size-1) { // si le packet est bien dans la fenetre d'envoi
				// stop timer

				// Ajout d'un facteur aléatoire de perte de paquets
				if (Math.random() < 0) { //
					System.out.println("ACK not received");

					// reenvoie le paquet
//					host.getIPLayer().sendData(IPAddress.ANY, datagram.src, IP_PROTO_SR, new SelectiveRepeatMessage(msg.num, true));
				} else {

                    // si le ack n'est pas corrompu
					sendingWindow.setAck(msg.num-send_base,true);
                    System.out.println("accepted ACK " + msg.num);
					if(msg.num == send_base)
						while (sendingWindow.head!=null && sendingWindow.head.ack){

							send_base++;
							sendingWindow.pop();

							// on rajoute le prochain message du buffer a la fenetre
							if(buffer.head!=null)
								sendingWindow.add(buffer.pop());


                            if(!allPcktSendedOnce) // si tout les paquets n'ont pas encore ete envoyer envoyer le suivant
                                send(sendingWindow.size+send_base-1);
							if(buffer.head == null )
							    allPcktSendedOnce = true;

							System.out.println("\nSENDER WINDOW: \n"+ sendingWindow +"\n");
						}
				}
			}
		}
	}

	public void sendData(IPAddress dst_, String data) throws  Exception {
		dst = dst_;

		int id = next_seq_num;

		final int sizeData = 4;

		int nbPackets = data.length()/sizeData;
		if(data.length()%sizeData > 0 )
			nbPackets++;

		int j = 0;
		for(int i = 0 ; i < nbPackets ; i++ ){
			String tmp = "";
			for(int k = 0 ; k < sizeData && j<data.length() ; k++){
				tmp+= data.toCharArray()[j];
				j++;
			}

			SelectiveRepeatMessage msg = new SelectiveRepeatMessage(id,tmp);
			id++;

			if(next_seq_num < send_base + size){
//				sendingWindow[next_seq_num-recv_base] = msg;
				sendingWindow.add(msg);
				host.getIPLayer().send(IPAddress.ANY, dst_, IP_PROTO_SR, msg);
				System.out.println("Packet send : "+msg);
				// start timer
				next_seq_num++;
			}else{
				buffer.add(msg);
			}
			// ajouter les paquets a un buffer pour pouvoir les envoyer quand la fenetre se decalera
		}
	}

	public void  send(int n) throws Exception{
		if(send_base <= n && n <= send_base+size){
//			System.out.println("SEND BFFF" + sendingWindow.head.data+ "    " + send_base);
//			System.out.println("SEND MSG" + sendingWindow.get(n-send_base));
			host.getIPLayer().send(IPAddress.ANY, dst, IP_PROTO_SR, sendingWindow.get(n-send_base));
			System.out.println("Packet send : "+sendingWindow.get(n-send_base));
			if(n == next_seq_num+1)
				next_seq_num++;
		}
	}
	public void init(IPAddress dst) throws  Exception{
		SelectiveRepeatMessage msg = new SelectiveRepeatMessage(next_seq_num,false,true);
		host.getIPLayer().send(IPAddress.ANY, dst, IP_PROTO_SR, msg);
	}

}
