package com.emc.kazeon.connector.opentext.tests;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3.xmlmime.Base64Binary;
import org.w3.xmlmime.ContentType_type0;

import com.emc.kazeon.connector.opentext.Connector;
import com.emc.kazeon.connector.websvc.ObjectCreationSynchronizer;
import com.emc.kazeon.connector.websvc.conf.ConnectorDefinition;
import com.emc.kazeon.connector.websvc.connector.ConnectorInstance;
import com.emc.kazeon.connector.websvc.connector.IConnector;
import com.emc.kazeon.connector.websvc.connector.ObjectContent;
import com.emc.kazeon.connector.websvc.exception.connector.ObjectDoesNotExist;
import com.emc.kazeon.connector.websvc.exception.connector.UnknownInternalError;
import com.emc.kazeon.connector.websvc.logging.Loggers;
import com.emc.kazeon.connector.websvc.session.ListObjectsCache;
import com.emc.kazeon.connector.websvc.session.ListObjectsCache.ErrorBO;
import com.emc.kazeon.connector.websvc.trees.BaseObject;
import com.emc.kazeon.csvc.stubs.CreateObjectResponse;
import com.emc.kazeon.csvc.stubs.MetadataEntry;
import com.emc.kazeon.csvc.stubs.ReadObjectResponse;
import com.emc.kazeon.csvc.stubs.RepositoryEntry;
import com.emc.kazeon.tests.WSClient;
import com.emc.kazeon.tests.junit.EndToEndTest;
import com.emc.misc.MimeType;

public class DirectTests {

	private static final int TEST_DELETE_CUR_VERS = 	0b00000000000000100000;
	private static final int TEST_NON_EXISTING_ID = 	0b00000000000001000000;
	private static final int TEST_METADATA_2010 = 		0b00000000000010000000;
	private static final int TEST_CREATE_DIR_2010 = 	0b00000000000100000000;
	private static final int TEST_COPY_SP_SP = 			0b00000000001000000000;
	private static final int TEST_LIST_OBJ_2010 = 		0b00000000010000000000;
	private static final int TEST_CREATE_ARBO_2010 = 	0b00000000100000000000;
	private static final int TESTLOC = 					0b00000001000000000000;
	private static final int TEST_READ_OBJECT_2010 = 	0b00000010000000000000;
	private static final int TEST_WEBPARTS_META_2010 = 	0b00000100000000000000;
	private static final int TEST_LIST_DOCS_TIMEOUT = 	0b00001000000000000000;
	private static final int TEST_LIST_ALL = 			0b00010000000000000000;
	private static final int TEST_FEED_SP = 			0b00100000000000000000;
	private static final int TEST_DLOAD = 				0b01000000000000000000;
	private static final int TEST_STRANGE_LIST = 		0b10000000000000000000;

	private static int TEST = -1;

	@BeforeClass
	public static void init() {
		TEST = TEST_DELETE_CUR_VERS ; //TEST_DELETE_CUR_VERS; TEST_DLOAD
	}

	@Test
	public void testDelCurVer() {

		if ((TEST & TEST_DELETE_CUR_VERS) == TEST_DELETE_CUR_VERS) {

			Logger root = EndToEndTest.configureLogger();

			try {

				/*Download download = new Download();
				download.prerequisite();
*/				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\Opentext\\dist\\Opentext"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;
					//IConnector opentextConnector = new Connector(re, null);
					//opentextConnector.checkAuthentication();
				//	opentextConnector.checkAuthentication();
					c.checkAuthentication();
					((Connector)c).download();
					BaseObject testDir = c.getBaseObject(BaseObject.buildPartialBO("/SharePoint - 80/SwordConnect/Shared Documents/TEST"), root);
					List<BaseObject> children = new ArrayList<>();
					c.getChildren(testDir, children , null);
					for (BaseObject child: children) {
						if (child.name().startsWith("SomeTextDoc") && child.version.equals("2.0")) {
							c.deleteObject(child, false);
							break;
						}
					}
				}

			} catch (Exception e) {
				System.out.println("Failed at " + System.currentTimeMillis());
				e.printStackTrace();
			}
			
		}
		
	}

	@Test
	public void testNonExistId() {

		if ((TEST & TEST_NON_EXISTING_ID) == TEST_NON_EXISTING_ID) {

			Logger root = EndToEndTest.configureLogger();///SharePoint - 80/SwordConnect/Shared Documents/_1939_er.html

//			try {
//
//				Properties p = EndToEndTest.loadconnectorProps();
//				RepositoryEntry re = WSClient.getRepEntry(p);
//
//				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {
//
//					ConnectorInstance ci = cd.createInstance(re);
//					IConnector c = ci.instance;
//
//					long start = System.currentTimeMillis();
//					BaseObject bo = c.getBaseObject(BaseObject.buildPartialBO("/SharePoint - 80/SwordConnect/Shared Documents/Tata.txt", "e9cdecfb-8831-404c-be20-123456789641", null), root);
//					System.out.println(bo);
//					System.out.println("completed in " + (System.currentTimeMillis() - start));
//				}
//
//			} catch (Exception e) {
//				System.out.println("Failed at " + System.currentTimeMillis());
//				e.printStackTrace();
//			}

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					long start = System.currentTimeMillis();
					BaseObject bo = c.getBaseObject(BaseObject.buildPartialBO("/SWP/GDT/Shared Documents/Tata.txt", "e9cdecfb-8831-404c-be20-123456789641", null), root);
					System.out.println(bo);
					System.out.println("completed in " + (System.currentTimeMillis() - start));
				}

			} catch (Exception e) {
				System.out.println("Failed at " + System.currentTimeMillis());
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testWeardList() {
		if ((TEST & TEST_STRANGE_LIST) == TEST_STRANGE_LIST) {

			Logger root = EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					final ConnectorInstance ci = cd.createInstance(re);
					final IConnector c = ci.instance;
					
					BaseObject bo = c.getBaseObject(BaseObject.buildPartialBO("/SharePoint - 80/Global Community A/Reporting Metadata", null, null), root);

					List<BaseObject> children = new ArrayList<>();;
					c.getChildren(bo, children , null);
					System.out.println(children);

				}

			} catch (Throwable e) {
				System.out.println("Failed at " + System.currentTimeMillis());
				e.printStackTrace();
			}
			
		}
	}

	@Test
	public void testHttpDownload() {
		if ((TEST & TEST_DLOAD) == TEST_DLOAD) {

			Logger root = EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					final ConnectorInstance ci = cd.createInstance(re);
					final IConnector c = ci.instance;
					
					BaseObject bo = c.getBaseObject(BaseObject.buildPartialBO("/SharePoint - 80/Global Community A/User Information List/Administrator", "9ef99af8-4a52-468d-89e1-00de2f072d87", "1.0"), root);

					c.getObjectContent(bo, root);

				}

			} catch (Throwable e) {
				System.out.println("Failed at " + System.currentTimeMillis());
				e.printStackTrace();
			}
			
		}
	}

	@Test
	public void testFeedSP() {

		if ((TEST & TEST_FEED_SP) == TEST_FEED_SP) {

			Logger root = EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					final ConnectorInstance ci = cd.createInstance(re);
					final IConnector c = ci.instance;
					
					String path = "/SWP/GDT/Shared Documents/TestBigDir";
					BaseObject bo = c.getBaseObject(BaseObject.buildPartialBO(path), root);

					ArrayList<MetadataEntry> emptyMetaList = new ArrayList<MetadataEntry>();
					if (bo == null) bo = c.createFolder(path, emptyMetaList, root);
					
					for (int i=5083; i<10_000; i++) {
						if ((i%100)==0) root.warn("Processed " + i + " documents");
						try {
							c.createDocument(bo, "TestFile"+i+".txt", MimeType.TXT, new ByteArrayInputStream(("I am test text file #" + i).getBytes()), emptyMetaList, root);
						} catch (UnknownInternalError e) {
							Throwable c1 = null, c2 = null;
							if (((c1=e.getCause()) != null) && (c1 instanceof AxisFault) && ((c2=c1.getCause()) != null) && (c2 instanceof SocketException)) {
								Thread.sleep(30L);
								c.createDocument(bo, "TestFile"+i+".txt", MimeType.TXT, new ByteArrayInputStream(("I am test text file #" + i).getBytes()), emptyMetaList, root);
							} else throw e;
						}
					}

				}

			} catch (Throwable e) {
				System.out.println("Failed at " + System.currentTimeMillis());
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testListAll() {

		if ((TEST & TEST_LIST_ALL) == TEST_LIST_ALL) {

			EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					try (ListObjectsCache loCache = new ListObjectsCache("/SharePoint - 80/SwordConnect/Shared Documents", null, 50_000, 0, c, null)) {

						BaseObject bo;
						while (true) {
							bo = loCache.queue.take();
							System.out.println(bo);
							if (bo == ListObjectsCache.STOP_MARKER) {
								break;
							} else if (bo instanceof ErrorBO) {
								throw ((ErrorBO)bo).exception;
							}
						}

					}

				}

			} catch (Throwable e) {
				System.out.println("Failed at " + System.currentTimeMillis());
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testListDocsTimeout() {

		if ((TEST & TEST_LIST_DOCS_TIMEOUT) == TEST_LIST_DOCS_TIMEOUT) {

			Logger root = EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					long start = System.currentTimeMillis();
					BaseObject bo = c.getBaseObject(BaseObject.buildPartialBO("/SwordConnect/Shared Documents/Tata.txt", "e9cdecfb-8831-404c-be20-d3ed1882b641", null), root);//e9cdecfb-8831-404c-be20-d3ed1882b641
					System.out.println(bo);
					System.out.println("completed in " + (System.currentTimeMillis() - start));
				}

			} catch (Exception e) {
				System.out.println("Failed at " + System.currentTimeMillis());
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testWebpartsMeta2010() {

		if ((TEST & TEST_WEBPARTS_META_2010) == TEST_WEBPARTS_META_2010) {

			Logger root = EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					List<BaseObject> children = new ArrayList<>();
					c.getChildren(BaseObject.buildPartialBO("/SwordConnect/Web Part Gallery", null, null), children , null);
					BaseObject bo = c.getBaseObject(BaseObject.buildPartialBO("/SwordConnect/Web Part Gallery/AuthoredListFilter.webpart", "a8821ba0-cb36-4adc-a018-f9fd94b3b856", null), root);
					List<MetadataEntry> meta = c.getMetadata(bo);
					System.out.println("Meta");
					for (MetadataEntry me : meta) {
						System.out.println("\t- "+me.getName()+"="+me.getValue());
					}
					meta.clear();
					System.out.println("Basic meta");
					c.getBasicMetadata(bo, meta, root);
					for (MetadataEntry me : meta) {
						System.out.println("\t- "+me.getName()+"="+me.getValue());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Test
	public void testReadObj2010() {

		if ((TEST & TEST_READ_OBJECT_2010) == TEST_READ_OBJECT_2010) {

			Logger log = EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					BaseObject bo = c.getBaseObject(BaseObject.buildPartialBO("/SharePoint - 80/SwordConnect/Shared Documents/_2611_ServletException.html", "491260de-3cfd-48bd-89ba-c79f293602a2 @version-2", "0.2"), log);
					if (bo == null) throw new ObjectDoesNotExist("/SharePoint - 80/SwordConnect/Shared Documents/_2611_ServletException.html");

					List<MetadataEntry> mll = new ArrayList<>();
					c.getBasicMetadata(bo, mll , log);
					System.out.println("Basic meta");
					for (MetadataEntry me : mll) {
						System.out.println("\t- "+me.getName()+"="+me.getValue());
					}
					System.out.println("Meta");
					mll = c.getMetadata(bo);
					for (MetadataEntry me : mll) {
						System.out.println("\t- "+me.getName()+"="+me.getValue());
					}
					System.out.println("end meta");

					ObjectContent oc = c.getObjectContent(bo, log);
					System.out.println(oc.getContentType());
					System.out.println(oc.getName());
					System.out.println(oc.getCharset());

					Base64Binary bb = new Base64Binary();
					DataHandler dh = new DataHandler(oc);
					bb.setBase64Binary(dh);
					ContentType_type0 ct = new ContentType_type0();
					ct.setContentType_type0("base64Binary");
					bb.setContentType(ct);

					ReadObjectResponse ror = new ReadObjectResponse();
					ror.setFilePath("/SharePoint - 80/SwordConnect/Shared Documents/TEST/TheNewName.xlsx");
					ror.setObject(bb);

					System.out.println(WSClient.toString(ror));

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testListObjCache2007() {

		if ((TEST & TESTLOC) == TESTLOC) {

			Logger root = EndToEndTest.configureLogger();

			Logger hcl = Logger.getLogger("httpclient.wire");
			hcl.removeAllAppenders();
			Enumeration<?> enumer = root.getAllAppenders();
			while (enumer.hasMoreElements()) {
				hcl.addAppender((Appender) enumer.nextElement());
			}
			hcl.setLevel(Level.INFO);

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					c.listSubRepositories();

					int cont = 0;
					int docs = 0;
					try (ListObjectsCache l = new ListObjectsCache("/", null, 1000, 0, c, null)) {
						BaseObject bo = null;
						while (true) {
							bo = l.queue.take();
							if (bo == ListObjectsCache.STOP_MARKER) {
								break;
							} else if (bo instanceof ErrorBO) {
								throw ((ErrorBO)bo).exception;
							} else {
								System.out.println(bo);
								if (bo.isLeafNode()) docs++;
								else cont++;
							}
						}
					}
					System.out.println("Found " + docs + " docs and " + cont + " containers");

				}


			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
	}

	@Test
	public void testMetadata2010() {

		if ((TEST & TEST_METADATA_2010) == TEST_METADATA_2010) {

			Logger root = EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					c.listSubRepositories();

					//Site

					BaseObject swordConnectSiteBO = c.getBaseObject(BaseObject.buildPartialBO("/SharePoint - 80/SwordConnect", null, null), root);
					if (swordConnectSiteBO == null) throw new ObjectDoesNotExist("/SharePoint - 80/SwordConnect");
					List<MetadataEntry> scMeta = c.getMetadata(swordConnectSiteBO);
					for (MetadataEntry me : scMeta) {
						System.out.println(me.getName()+"="+me.getValue());
					}
					System.out.println("###########################");
					scMeta.clear();
					c.getBasicMetadata(swordConnectSiteBO, scMeta, root);
					for (MetadataEntry me : scMeta) {
						System.out.println(me.getName()+"="+me.getValue());
					}

					BaseObject swordConnectLunchSiteBO = c.getBaseObject(BaseObject.buildPartialBO("/SharePoint - 80/SwordConnect/Lunch", null, null), root);
					if (swordConnectLunchSiteBO == null) throw new ObjectDoesNotExist("/SharePoint - 80/SwordConnect/Lunch");

					List<MetadataEntry> sclMeta = new ArrayList<>();
					c.getBasicMetadata(swordConnectLunchSiteBO, sclMeta, root);
					System.out.println("###########################");
					for (MetadataEntry me : sclMeta) {
						System.out.println(me.getName()+"="+me.getValue());
					}

					BaseObject bo = c.getBaseObject(BaseObject.buildPartialBO("/SharePoint - 80/Global Community A/Reporting Metadata", "f1a1a826-f434-4b65-943f-01f4608ee97a", null), root);
					if (bo == null) throw new ObjectDoesNotExist("/SharePoint - 80/SwordConnect/Lunch");

					sclMeta.clear();
					c.getBasicMetadata(bo, sclMeta, root);
					System.out.println("###########################");
					for (MetadataEntry me : sclMeta) {
						System.out.println(me.getName()+"="+me.getValue());
					}
					
					
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testCreateArbo2010() {

		if ((TEST & TEST_CREATE_ARBO_2010) == TEST_CREATE_ARBO_2010) {

			Logger log = EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					c.createFolder("/SharePoint - 80/SwordConnect/EmptyDocLib/FolderA/FolderB/FolderC/FolderD", null, log);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testListObj2010() {

		if ((TEST & TEST_LIST_OBJ_2010) == TEST_LIST_OBJ_2010) {

			EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					List<BaseObject> children = new ArrayList<>();
					c.getChildren(BaseObject.buildPartialBO("/SharePoint - 80", null, null), children , null);
					children.clear();
					c.getChildren(BaseObject.buildPartialBO("/SharePoint - 80/SwordConnect", null, null), children , null);
					for (BaseObject bo: children) {
						if (bo.kazPath.contains("/SwordConnect/Kazéon")) {
							children.clear();
							c.getChildren(bo, children , null);
							System.out.println(children);
							break;
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testCreateDir2010() {

		if ((TEST & TEST_CREATE_DIR_2010) == TEST_CREATE_DIR_2010) {

			Logger root = EndToEndTest.configureLogger();

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					c.createFolder("/SharePoint - 80/Home/test site spaces/Shared Documents/TestFoldForBackSlash/Test Backslash \\ Hummm", null, root);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Test
	public void testCopySp2Sp() {

		if ((TEST & TEST_COPY_SP_SP) == TEST_COPY_SP_SP) {

			EndToEndTest.configureLogger(Level.DEBUG);

			try {

				Properties p = EndToEndTest.loadconnectorProps();
				RepositoryEntry re = WSClient.getRepEntry(p);

				try (ConnectorDefinition cd = new ConnectorDefinition(new File("connectors\\SharePoint\\dist\\SharePoint"))) {

					ConnectorInstance ci = cd.createInstance(re);
					IConnector c = ci.instance;

					String filePath = "/SharePoint - 80/SwordConnect/Shared Documents/newtest_copy1/SharePoint - 80/EXCHDOMAIN\\kazAdmin/Theme Gallery/HelloWorld.txt";
					BaseObject newFileBO = BaseObject.buildPartialBO(filePath);
					filePath = newFileBO.kazPath;

					String newFileName = newFileBO.name();

					String parentPath = BaseObject.getParentPath(newFileBO);
					BaseObject parentBo = BaseObject.buildPartialBO(parentPath);

					try (ObjectCreationSynchronizer sync = new ObjectCreationSynchronizer(parentPath)) {
						parentBo = c.getBaseObject(parentBo, Loggers.CSVC_OPER_CREATE_OBJECT);
						boolean destExists = parentBo != null;

						Loggers.CSVC_OPER_CREATE_OBJECT.debug("Destination folder " + (destExists ? "exists" : "does not exist"));
						if (!destExists) parentBo = c.createFolder(parentPath, null, Loggers.CSVC_OPER_CREATE_OBJECT);
					}


					try (ObjectCreationSynchronizer sync = new ObjectCreationSynchronizer(filePath)) {
						newFileBO = c.getBaseObject(newFileBO, Loggers.CSVC_OPER_CREATE_OBJECT);
						boolean fileExists = newFileBO != null;
						Loggers.CSVC_OPER_CREATE_OBJECT.debug("Destination object " + (fileExists ? "exists" : "does not exist"));
						//if (fileExists && !createNewVersion) throw NonRetryableError.INVALID_OBJECT_PATH_SPECIFIED.buildCSVCError("An object already exists at the selected location", null);

						InputStream is = new ByteArrayInputStream("This is a test file with dummy content".getBytes(StandardCharsets.UTF_8));
						MimeType mime = MimeType.TXT;
						
						Loggers.CSVC_OPER_CREATE_OBJECT.debug("Updating content with mime: " + mime);
						try {
							List<MetadataEntry> ml = new ArrayList<>();
							if (fileExists) c.updateObject(newFileBO, mime, is, ml , true, Loggers.CSVC_OPER_CREATE_OBJECT);
							else newFileBO = c.createDocument(parentBo, newFileName, mime, is, ml, Loggers.CSVC_OPER_CREATE_OBJECT);
						} finally {
							if (is != null) is.close();
						}
					}

					CreateObjectResponse cor = new CreateObjectResponse();
					cor.setFilePath(newFileBO.kazPath);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
