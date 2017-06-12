package com.emc.kazeon.connector.opentext;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/*
 *  This is a handler which inject the authentication token to the soap header.
 */
public class ContSvcSOAPHeaderHandler implements SOAPHandler<SOAPMessageContext> {

    private final String authenticatedToken;
    private final String contextIDString;
    
    private static final String ECM_API_NAMESPACE = "urn:api.ecm.opentext.com";
    public final static String CORE_NAMESPACE = "urn:Core.service.livelink.opentext.com";

    public ContSvcSOAPHeaderHandler(String authenticatedToken,String contextIDString) {
        this.authenticatedToken = authenticatedToken;
        this.contextIDString = contextIDString;
    }

    public boolean handleMessage(SOAPMessageContext context) {
    	
        Boolean outboundProperty =
                (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outboundProperty.booleanValue()) {
        	
            try {
            	SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader header = envelope.addHeader();
       
                SOAPElement security = header.addChildElement(new QName(ECM_API_NAMESPACE, "OTAuthentication"));
                SOAPElement token = security.addChildElement(new QName(ECM_API_NAMESPACE, "AuthenticationToken"));
                token.addTextNode(authenticatedToken);
                
                SOAPElement contextIDElement = header.addChildElement(new QName(ECM_API_NAMESPACE, "contextID"));
                contextIDElement.addTextNode(contextIDString);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // inbound
        }
        return true;
    }

    public Set<QName> getHeaders() {
        return new TreeSet<QName>();
    }

    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    public void close(MessageContext context) {
        //
    }
    
    
}
