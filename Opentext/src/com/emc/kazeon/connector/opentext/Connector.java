package com.emc.kazeon.connector.opentext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.kohsuke.rngom.parse.host.Base;

import com.emc.kazeon.connector.opentext.objects.OpentextObjectContent;
import com.emc.kazeon.connector.websvc.conf.ConnectorDefinition;
import com.emc.kazeon.connector.websvc.connector.ConnectorBase;
import com.emc.kazeon.connector.websvc.connector.IConnector;
import com.emc.kazeon.connector.websvc.connector.ObjectContent;
import com.emc.kazeon.connector.websvc.exception.connector.AuthenticationException;
import com.emc.kazeon.connector.websvc.exception.connector.ConnectorException;
import com.emc.kazeon.connector.websvc.trees.BaseObject;
import com.emc.kazeon.connector.websvc.trees.BaseObject.IsLeafNode;
import com.emc.kazeon.csvc.stubs.CSVCFaultException;
import com.emc.kazeon.csvc.stubs.DeleteMetadataResponse;
import com.emc.kazeon.csvc.stubs.DeleteObjectResponse;
import com.emc.kazeon.csvc.stubs.FilterList;
import com.emc.kazeon.csvc.stubs.MetadataEntry;
import com.emc.kazeon.csvc.stubs.MetadataTags;
import com.emc.kazeon.csvc.stubs.MetadataType;
import com.emc.kazeon.csvc.stubs.RemoveLegalHoldResponse;
import com.emc.kazeon.csvc.stubs.RepositoryEntry;
import com.emc.kazeon.csvc.stubs.RetentionClass;
import com.emc.kazeon.csvc.stubs.SetLegalHoldResponse;
import com.emc.kazeon.csvc.stubs.SetMetadataResponse;
import com.emc.kazeon.csvc.stubs.SetRetentionResponse;
import com.emc.kazeon.csvc.stubs.VersionEntry;
import com.emc.misc.MimeType;
import com.opentext.ecm.api.OTAuthentication;
import com.opentext.livelink.service.core.Authentication;
import com.opentext.livelink.service.core.Authentication_Service;
import com.opentext.livelink.service.docman.DocumentManagement;
import com.opentext.livelink.service.docman.DocumentManagement_Service;
import com.opentext.livelink.service.docman.Node;
import com.opentext.livelink.service.docman.NodeVersionInfo;

import sun.util.logging.resources.logging;

/**
 * The original opentext connector
 */
public class Connector extends ConnectorBase<OpentextConfig>{
	
	public DocumentManagement docManClient;
	private OTAuthentication otAuth;
	
	public static final String VATTR_NAME_IS_UNDER_RETENTION = "IsUnderRetention";
	public static final String SIZE = "Size";
	public static String AGENT_NAME = "";
	
	/**
	 * Method that is automatically called prior to creating an instance of an {@link IConnector}.
	 * For the Documentum connector it will alter the base dfc.properties file to setup information specific to this instance.
	 */
	public static Object init(Path connectorDir) throws FileNotFoundException, IOException {
		Properties dfcProps = new Properties();
		File dfcPropsFile = connectorDir.resolve(ConnectorDefinition.CLASSES_FOLDER_NAME).resolve("opentext.properties").toFile();
		try (FileInputStream fis = new FileInputStream(dfcPropsFile)) { dfcProps.load(fis); }
		AGENT_NAME  = dfcProps.getProperty("opentext.agent");
		
		
		return null;
	}
	
	
	public Connector(RepositoryEntry re, Path connectorHome) throws CSVCFaultException {
		super(re, connectorHome);
		Properties props = new Properties();
		URL url = null;
		try {
			url = new URL("http://"+AGENT_NAME+"/cws/DocumentManagement.svc?wsdl");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DocumentManagement_Service docManService = new DocumentManagement_Service();
		docManClient = docManService.getBasicHttpBindingDocumentManagement();
	}

	@Override
	public void setInitObject(Object object) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  This function does the authentication to the OT content engine
	 */
	@Override
	public void checkAuthentication() throws AuthenticationException {
		
		URL url = null;
		try {
			url = new URL("http://"+AGENT_NAME+"/cws/Authentication.svc?wsdl"); // http://jeetotcs/cws/Authentication.svc?wsdl // http://10.10.140.127/cws/Authentication.svc?wsdl
			System.out.println("url : " +url.toString());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//Authentication_Service authService = new Authentication_Service();
		Authentication_Service authService = new Authentication_Service();
		
		Authentication authClient = authService.getBasicHttpBindingAuthentication();
		((BindingProvider)authClient).getBinding().getHandlerChain().add(new SOAPLoggingHandler());
		String authToken = null;
		
		try {
			// Authenticating the user
			authToken = authClient.authenticateUser(conf.userName, conf.userPassword);
		} catch (SOAPFaultException e) {
			
			System.out.println("Authentication failed");
			System.out.println(e.getFault().getFaultCode() + " : " + e.getMessage());
			throw new AuthenticationException(e);
		}
		
		otAuth = new OTAuthentication();
		otAuth.setAuthenticationToken(authToken);
		
		// Adding the Auth token to the SOAP header
		((BindingProvider)docManClient).getBinding().getHandlerChain().add(new SOAPHeaderHandler(authToken));
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////
public InputStream download(BaseObject bo) throws IOException{
		
		InputStream in = null;
		List<Long> contextIDList = new ArrayList<>();
		
		Node downloadNode = getNodeFromBaseObject(bo);
		if(downloadNode.isIsContainer()){
			System.out.println("Download conatainer is Folder!");
		}
		else{
			in = NodeDownload.downloadContent(docManClient, otAuth, downloadNode.getID(), null,AGENT_NAME);
		}
	/*	
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
		
		List<String> FilePathList = new ArrayList<>();
		
		List<String> nodeName = new ArrayList<>();
		for (Node node : nodeList) {
			 nodeName.add(node.getName());
			 FilePathList.add("C:/_Kazeon/otdownload/"+node.getName());
		}

		int i=0;
		for (Long long1 : contextIDList) {
			long contextIDrec = long1;
			if(long1!=null){
				in = NodeDownload.downloadContent(docManClient, otAuth, contextIDrec, FilePathList.get(i),AGENT_NAME);
			}
			i++;
		}*/
		return in;
	}
	
	///////////////////////////////////////////////////////////////////////////////////

	@Override
	public BaseObject createDocument(BaseObject parentObject, String objectName, MimeType mime, InputStream is,
			List<MetadataEntry> ml, Logger log) throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseObject createFolder(String path, List<MetadataEntry> ml, Logger log)
			throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VersionEntry createVersion(BaseObject bo) throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeleteMetadataResponse deleteMetaTags(BaseObject bo, MetadataTags metadataTags)
			throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeleteObjectResponse deleteObject(BaseObject bo, boolean allVersions)
			throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Node getNodeFromBaseObject(BaseObject bo){
		
		if(bo.kazPath == null) throw new IllegalArgumentException("Please provide a valid input baseObject");
		
		List<String> rootNodeType = docManClient.getRootNodeTypes();
		Node curNode = null; 
		OpentextPath path = new OpentextPath(bo);
		// Handling the root Baseobject. Root base object is not attached any Node.
		if(path.isRoot){
			return null;
		}
		else{
			//Check for valid workspace name
			if(rootNodeType.contains(path.workSpaceName)){
				
				if(path.isWorkSpace ){
					curNode = docManClient.getRootNode(path.workSpaceName);
				}
				else{
					List<String> pathComp = new ArrayList<String>(Arrays.asList(path.filePathArray));
					Node workSpaceNode = docManClient.getRootNode(path.workSpaceName);
					curNode = docManClient.getNodeByPath(workSpaceNode.getID(),pathComp);
				}
			}
		}
		//curNode == Null means either invalid path or root 
		return curNode;
	}
	
	private Long convertGregorianDateToLong(XMLGregorianCalendar xmlcalendar){
		
		Date inDate = xmlcalendar.toGregorianCalendar().getTime();
		return inDate.getTime();
	}
	

	@Override
	public BaseObject getBaseObject(BaseObject bo, Logger log) throws ConnectorException, CSVCFaultException {
		
		if(bo.kazPath == null || bo.kazPath == "") throw new IllegalArgumentException("Please provide a valid input baseObject");
		
		if(bo.kazPath.equals("/")){
			return BaseObject.ROOT;
		}
		else{
			
			String parentPath = BaseObject.getParentPath(bo);
			BaseObject parentBO = BaseObject.buildPartialBO(parentPath);
			Node currentNode = getNodeFromBaseObject(bo);
			
			if(currentNode != null){
				
				String name = currentNode.getName();
				String id = Long.toString(currentNode.getID());
				IsLeafNode isLeafNode = currentNode.isIsContainer() ? IsLeafNode.NO :IsLeafNode.YES;
				long createDate = convertGregorianDateToLong(currentNode.getCreateDate());
				long modifydate = convertGregorianDateToLong(currentNode.getModifyDate());
				long accessdate = convertGregorianDateToLong(currentNode.getModifyDate());
				
				long size = (long) 0;
				String version = "";
				// version will valid only on leaf nodes
				if(isLeafNode == BaseObject.IsLeafNode.YES){
					NodeVersionInfo versionInfo = currentNode.getVersionInfo();
					size = versionInfo.getFileDataSize();
					version = Long.toString(versionInfo.getVersionNum());
				}
				
				
				return new BaseObject(parentBO, name, id, isLeafNode, createDate, modifydate,accessdate, size, version);
			}
			else{   
				//BaseObject having invalid path
				throw new IllegalArgumentException("Invalid path");
			}
		}
	}
	

	@Override
	public void getBasicMetadata(BaseObject bo, List<MetadataEntry> mll, Logger log)
			throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getChildren(BaseObject parentbo, List<BaseObject> children, FilterList filters)
			throws ConnectorException, CSVCFaultException {
		
		
		//TODO need to replace Illegal with connector exception instance
		if(parentbo.kazPath == null || parentbo.kazPath == "") throw new IllegalArgumentException("Please provide a valid input baseObject");
		
		Node parentNode = getNodeFromBaseObject(parentbo);
		
		if(parentNode == null && BaseObject.isRoot(parentbo)){
			// dealing with root node
			List<String> rootNodeType = docManClient.getRootNodeTypes();
			for(String nodeType : rootNodeType){
				
				Node rootNode = docManClient.getRootNode(nodeType);
				IsLeafNode isLeafNode = rootNode.isIsContainer() ? IsLeafNode.NO :IsLeafNode.YES;
				String version = null;
				if(rootNode.getVersionInfo() != null){
					version = Long.toString(rootNode.getVersionInfo().getVersionNum());
				}
				
				BaseObject bo = new BaseObject(parentbo, nodeType, nodeType, isLeafNode, version);
				children.add(bo);
			}
		}
		else if(parentNode == null){
			// Invalid Baseobject//TODO
			throw new IllegalArgumentException("Please provide a valid input baseObject");
		}
		else{
			// Dealing with Proper Node 
			List<Node> childNodes = docManClient.listNodes(parentNode.getID(), true);
			
			for(Node childNode : childNodes){
				
				String name = childNode.getName();
				String id = Long.toString(childNode.getID());
				IsLeafNode isLeafNode = childNode.isIsContainer() ? IsLeafNode.NO :IsLeafNode.YES;
				long createDate = convertGregorianDateToLong(childNode.getCreateDate());
				long modifydate = convertGregorianDateToLong(childNode.getModifyDate());
				long accessdate = convertGregorianDateToLong(childNode.getModifyDate());
				
				String version = "";
				long size = (long) 0;
				/* version will valid only on leaf nodes*/
				if(isLeafNode == BaseObject.IsLeafNode.YES){
					NodeVersionInfo versionInfo = childNode.getVersionInfo();
					if(versionInfo != null){
						size = versionInfo.getFileDataSize();
						version = Long.toString(versionInfo.getVersionNum());
					}
					
				}
			
				BaseObject bo = new BaseObject(parentbo, name, name, isLeafNode, createDate, modifydate,accessdate, size, version);
				children.add(bo);
			}
		}
	}

	@Override
    public List<MetadataEntry> getMetadata(BaseObject bo) throws ConnectorException, CSVCFaultException {
          
		List<MetadataEntry> metadata = new ArrayList<>();
		MetadataEntry rme = new MetadataEntry();
		rme.setName(VATTR_NAME_IS_UNDER_RETENTION);
		rme.setType(MetadataType.BOOLEAN);
		rme.setValue(Boolean.toString(true));
		metadata.add(rme);
		return metadata;
    }


	@Override
	public ObjectContent getObjectContent(BaseObject bo, Logger log) throws ConnectorException, CSVCFaultException {
		
		InputStream content = null;
		try {
			content = download(bo);
			//log.info("content -> " +content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Problem with download method");
			e.printStackTrace();
		}
		String mime = "application/xml";
		Charset cs = Charset.forName("UTF-8");
		AutoCloseable disposableResources = null;
		String name = null;
		return new OpentextObjectContent(content, mime, cs, disposableResources, name, log);
		
		
		
	/*	if (bo instanceof SPDoc) {
			SPDoc sbd = (SPDoc)bo;
//			if (sbd.isDownloadableFile) {
				String url = sbd.getEntityUrl();
				log.info("Retrieving object content from: " + url);
				Object[] o = null;
//				
				InputStream content = (InputStream) o[0];
				String mime = (String) o[1];
				Charset charset = (Charset) o[2];
				AutoCloseable disposableResources = (AutoCloseable) o[3];
				return new SPObjectContent(content, mime, charset, disposableResources, sbd.name(), log);
//			} else {
//				String xml = driver.getObjectXMLContent(sbd);
//				return new SPObjectContent(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), "text/xml", StandardCharsets.UTF_8, null, sbd.name(), log);
//				
//			}
		} 
		throw NonRetryableError.INVALID_ARGUMENTS.buildCSVCError("Only leaf objects have a content: " + bo.kazPath, null);*/
	}

	@Override
	public VersionEntry[] getVersionList(BaseObject bo) throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> listSubRepositories() {
		// this needs only the top level directories.
		List<String> rootNodeType = docManClient.getRootNodeTypes();
		return rootNodeType;
	}

	@Override
	public void loadRetentionClasses(List<RetentionClass> rcl) throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RemoveLegalHoldResponse removeHold(BaseObject bo, String holdId)
			throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void renameObject(BaseObject sourceBaseObj, String newName) throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SetLegalHoldResponse setLegalHold(BaseObject bo, String holdId, String caseId, String description)
			throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetMetadataResponse setMetadata(BaseObject bo, List<MetadataEntry> ml)
			throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetRetentionResponse setRetention(BaseObject bo, String retentionClass, long retentionPeriod)
			throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void testConnectivity(String subRepository) throws ConnectorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BaseObject updateObject(BaseObject bo, MimeType mime, InputStream is, List<MetadataEntry> ml,
			boolean incrementVersion, Logger log) throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected OpentextConfig parseConfiguration(RepositoryEntry re) throws Exception {
		
		OpentextConfig otconfig = new OpentextConfig(re);
		if(otconfig.userName == null || otconfig.userPassword == null){
			throw new Exception("Invalid configuarion entries from Repository entry");
		}
		return otconfig;
	}

	
	
}
