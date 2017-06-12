/*package com.emc.kazeon.connector.opentext;
//package com.opentext.cws.samples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPFaultException;

import com.opentext.ecm.api.OTAuthentication;
import com.opentext.livelink.service.core.Authentication;
import com.opentext.livelink.service.core.Authentication_Service;
import com.opentext.livelink.service.core.ContentService;
import com.opentext.livelink.service.core.ContentService_Service;
import com.opentext.livelink.service.docman.DocumentManagement;
import com.opentext.livelink.service.docman.DocumentManagement_Service;
import com.opentext.livelink.service.docman.Node;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.developer.WSBindingProvider;

//@XmlRootElement(name="String")
public class Download
{
	// The user's credentials
	public final static String USERNAME = "Admin";
	public final static String PASSWORD = "livelink";

	// The ID of the document to download
	public final static long DOCUMENT_ID = 2326; //17838 //2326 2891 2328 2326

	// The local file path to download the document to
	 public final static String FILE_PATH = "C:/temp/download/testing.txt";

	// Namespaces for the SOAP headers
	public final static String ECM_API_NAMESPACE = "urn:api.ecm.opentext.com";
	public final static String CORE_NAMESPACE = "urn:Core.service.livelink.opentext.com";

	public static void main(String[] args)
	{
		prerequisite();
		

	}
	public void authenticate(String username, String password) {
		Download download = new Download();
		download.prerequisite();
	}
	
	public static void prerequisite() {
		// --------------------------------------------------------------------------
		// 1) Authenticate the user
		// --------------------------------------------------------------------------

		// Create the Authentication service client
		Authentication_Service authService = new Authentication_Service();
		Authentication authClient = authService.getBasicHttpBindingAuthentication();

		// Store the authentication token
		String authToken = null;

		// Call the AuthenticateUser() method to get an authentication token
		try
		{
			System.out.print("Authenticating User...");
			authToken = authClient.authenticateUser("Admin", "livelink");
			System.out.println("SUCCESS!\n");
		}
		catch (SOAPFaultException e)
		{
			System.out.println("FAILED!\n");
			System.out.println(e.getFault().getFaultCode() + " : " + e.getMessage());
			return;
		}

		// --------------------------------------------------------------------------
		// 2) Store the metadata for the download in a method context on the server
		// --------------------------------------------------------------------------

		// Create the DocumentManagement service client
		DocumentManagement_Service docManService = new DocumentManagement_Service();
		DocumentManagement docManClient = docManService.getBasicHttpBindingDocumentManagement();

		// Create the OTAuthentication object and set the authentication token
		OTAuthentication otAuth = new OTAuthentication();
		otAuth.setAuthenticationToken(authToken);

		// We need to manually set the SOAP header to include the authentication token
		try
		{
			// Create a SOAP header
			SOAPHeader header = MessageFactory.newInstance().createMessage().getSOAPPart().getEnvelope().getHeader();

			// Add the OTAuthentication SOAP header element
			SOAPHeaderElement otAuthElement = header.addHeaderElement(new QName(ECM_API_NAMESPACE, "OTAuthentication"));

			// Add the AuthenticationToken SOAP element
			SOAPElement authTokenElement = otAuthElement.addChildElement(new QName(ECM_API_NAMESPACE, "AuthenticationToken"));
			authTokenElement.addTextNode(otAuth.getAuthenticationToken());
		
			// Set the header on the binding provider
			((WSBindingProvider) docManClient).setOutboundHeaders(Headers.create(otAuthElement));
		}
		catch (SOAPException e)
		{
			System.out.println("Failed to set authentication SOAP header!\n");
			System.out.println(e.getMessage());
			return;
		}

		List<Long> contextIDList = new ArrayList<>();
		
		List<Node> nodeListRoot = docManClient.listNodes(2000, true);
		List<Node> nodeList = new ArrayList<>();
		for (Node node : nodeListRoot) {
			if(node.getName().equals("Aditya"))
				nodeList.addAll(docManClient.listNodes(node.getID(), true));
				//nodeList.add(node.get);
		}
		
		System.out.println(nodeList);
		
		for (Node node: nodeList) {
			contextIDList.add(node.getID());
		}
		
		for (Node node : nodeList) {
			if(node.getType().equals("Document"))
				System.out.println("call download");
		}
		
		contextIDList.add(2661L);
		contextIDList.add(2565L);
		List<String> FilePathList = new ArrayList<>();
		
		List<String> nodeName = new ArrayList<>();
		for (Node node : nodeList) {
			 nodeName.add(node.getName());
			 FilePathList.add("C:/temp/download/"+node.getName());
		}
		
	
		
		FilePathList.add("C:/temp/download/"+node.);
		FilePathList.add("C:/temp/download/testing1.doc");
		//String FilePath;
		int i=0;
		for (Long long1 : contextIDList) {
			long contextIDrec = long1;
			if(long1!=null){
			download(docManClient, otAuth, contextIDrec, FilePathList.get(i));
			}
			i++;
		}
	}

	private static void download(DocumentManagement docManClient, OTAuthentication otAuth, long contextIDrec, String FilePath) {
		// Store the context ID for the download
		Long contextID = contextIDrec;
		String contextIDString;

		// Call the getVersionContentsContext() method to create the context ID
		try
		{
			System.out.print("Generating context ID...");
			contextIDString = docManClient.getVersionContentsContext(contextID, 0);
			System.out.println("SUCCESS!\n");
		}
		catch (SOAPFaultException e)
		{
			System.out.println("FAILED!\n");
			System.out.println(e.getFault().getFaultCode() + " : " + e.getMessage());
			return;
		}

		// --------------------------------------------------------------------------
		// 3) Download the file
		// --------------------------------------------------------------------------

		// Create the ContentService client
		// NOTE: ContentService is the only service that requires MTOM support
		ContentService_Service contentService = new ContentService_Service();
		ContentService contentServiceClient = contentService.getBasicHttpBindingContentService(new MTOMFeature());

		// Adding the Auth token to the SOAP header
		((BindingProvider)docManClient).getBinding().getHandlerChain().add(new SOAPHeaderHandler(contextIDString));

		// Create a StreamingDataHandler to download the file with
		StreamingDataHandler downloadStream = null;

		// Call the downloadContent() method
		try
		{
			System.out.print("Downloading file...");
			downloadStream = (StreamingDataHandler) contentServiceClient.downloadContent(contextIDString);
		}
		catch (SOAPFaultException e)
		{
			System.out.println("FAILED!\n");
			System.out.println(e.getFault().getFaultCode() + " : " + e.getMessage());
			return;
		}

		// Stream the download to the local file path
		try
		{
			File file = new File(FilePath);
			downloadStream.moveTo(file);
			System.out.println("SUCCESS!\n");
			System.out.println("Downloaded " + file.length() + " bytes to " + FilePath.toString() + ".\n");
		}
		catch (Exception e)
		{
			System.out.println("Failed to download file!\n");
			System.out.println(e.getMessage());
		}
		finally
		{
			// Always close the streams
			try
			{
				downloadStream.close();
			}
			catch (IOException e)
			{
				System.out.println("Failed to close the download stream!\n");
				System.out.println(e.getMessage());
			}
		}
	}

}
*/