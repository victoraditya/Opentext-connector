/*package com.emc.kazeon.connector.opentext.map;

import java.io.InputStream;
import java.nio.charset.Charset;

import com.emc.kazeon.connector.websvc.connector.ObjectContent;
import com.emc.kazeon.connector.websvc.exception.connector.ConnectorException;
import com.emc.kazeon.csvc.stubs.CSVCFaultException;

public class OpentextDoc extends ObjectContent {

	public final String url;
	public final boolean isFile;
	public final boolean hasMeta;

	public OpentextDoc(String id, String name, String url, long creationDate, long lastModifDate, long lastAccessDate, long size, String version, boolean isFile, boolean hasMeta) {
		super(log);
		this.url = url;
		this.isFile = isFile;
		this.hasMeta = hasMeta;
	}

	public String getEntityUrl() {
		return url;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String _getName() throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Charset getCharset() throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String _getContentType() throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getContent() throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

}
*/