package com.emc.kazeon.connector.opentext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.io.FileUtils;

import com.opentext.ecm.api.OTAuthentication;
import com.opentext.livelink.service.core.ContentService;
import com.opentext.livelink.service.core.ContentService_Service;
import com.opentext.livelink.service.docman.DocumentManagement;
//import com.sun.xml.internal.ws.developer.StreamingDataHandler;
//import com.sun.xml.internal.org.jvnet.staxex.StreamingDataHandler;

public class NodeDownload {
	
	
	
	public static InputStream downloadContent(DocumentManagement docManClient, OTAuthentication otAuth,Long contextID,String FilePath,String AGENT_NAME) throws IOException{
		
		String contextIDString = null;
		String DownloadPath= "C:/_Kazeon/otdownload/";
		InputStream in = null ;
		String agent_name=AGENT_NAME;
		
		// Call the getVersionContentsContext() method to create the context ID
		try
		{
			System.out.print("context ID..."+contextID);
			contextIDString = docManClient.getVersionContentsContext(contextID, 0);
			System.out.println("SUCCESS!\n");
		}
		catch (SOAPFaultException e)
		{
			System.out.println("FAILED!\n");
			System.out.println(e.getFault().getFaultCode() + " : " + e.getMessage());
		//	return;
		}
		
		// Create the ContentService client
		// NOTE: ContentService is the only service that requires MTOM support
		URL url = new URL("http://${"+agent_name+"}/cws/ContentService.svc?wsdl");
		ContentService_Service contentService = new ContentService_Service();
		ContentService contentServiceClient = contentService.getBasicHttpBindingContentService(new MTOMFeature());
		
		((BindingProvider)contentServiceClient).getBinding().getHandlerChain().add(new ContSvcSOAPHeaderHandler(otAuth.getAuthenticationToken(), contextIDString));
		
		// Create a StreamingDataHandler to download the file with
	//	StreamingDataHandler downloadStream = null;
		byte[] byteArray = null;

		// Call the downloadContent() method
		try
		{
			System.out.print("Downloading file...");
		//	in = contentServiceClient.downloadContent(contextIDString).getInputStream();
			in = contentServiceClient.downloadContent(contextIDString).getInputStream();
		//	System.out.println(contentServiceClient.downloadContent(contextIDString).getContentType());
		/*	byteArray = org.apache.commons.io.IOUtils.toByteArray((InputStream) in);
			System.out.println(byteArray);
			File file = new File(FilePath);
			FileUtils.writeByteArrayToFile(new File(FilePath),byteArray);
			//downloadStream.moveTo(file);
			System.out.println("SUCCESS!\n");
			System.out.println("Downloaded " + file.length() + " bytes to " + FilePath.toString() + ".\n");*/
			return in;

			/*downloadStream=byteArray;
			downloadStream = (StreamingDataHandler) contentServiceClient.downloadContent(contextIDString).getInputStream();*/
		}
		catch (SOAPFaultException e)
		{
			System.out.println("FAILED!\n");
			System.out.println(e.getFault().getFaultCode() + " : " + e.getMessage());
			//return;
		}
		
		//copying
		/*try
		{
			File file = new File(FilePath);
			FileUtils.writeByteArrayToFile(new File(FilePath),byteArray);
			//downloadStream.moveTo(file);
			System.out.println("SUCCESS!\n");
			System.out.println("Downloaded " + file.length() + " bytes to " + FilePath.toString() + ".\n");
		}
		catch (Exception e)
		{
			System.out.println("Failed to download file!\n");
			System.out.println(e.getMessage());
		}*/
		/*finally
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
		}*/
		
		return in;
	
	}	

}
