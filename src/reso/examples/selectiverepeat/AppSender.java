package reso.examples.selectiverepeat;

import reso.common.AbstractApplication;
import reso.ip.IPAddress;
import reso.ip.IPHost;
import reso.ip.IPLayer;

public class AppSender
    extends AbstractApplication
{ 
	
	private final IPLayer ip;
    private final IPAddress dst;

    private SelectiveRepeatProtocol proto;


    public AppSender(IPHost host, IPAddress dst, int num) {	
    	super(host, "sender");
    	this.dst= dst;
    	ip= host.getIPLayer();
    }

    public void start() throws Exception{
        proto = new SelectiveRepeatProtocol((IPHost) host);
        ip.addListener(SelectiveRepeatProtocol.IP_PROTO_SR, proto);

//        ip.sendData(IPAddress.ANY, dst, SelectiveRepeatProtocol.IP_PROTO_SR, new SelectiveRepeatMessage(0));
//        ip.sendData(IPAddress.ANY, dst, SelectiveRepeatProtocol.IP_PROTO_SR, new SelectiveRepeatMessage(1));

//        proto.sendData(dst,"Send with selective repeat protocol, endl");
        proto.sendData(dst,"La partie Selective Repeat doit permettre d’envoyer plusieurs paquets à la suite sans attendre\n" +
                "d’acquittement, afin d’optimiser l’utilisation de la bande passante. Elle reçoit les paquets à envoyer\n" +
                "de l’application et a pour responsabilité de vérifier l’ordre d’arrivée et l’acquittement de ces paquets.\n" +
                "Un noeud émetteur doit répondre à trois types d’évenements:\n");
//        proto.sendData(dst,"1--12--23");

//        proto.sendData(dst);
//        proto.sendData(dst);
    }

    
    public void stop() {}
    
}

