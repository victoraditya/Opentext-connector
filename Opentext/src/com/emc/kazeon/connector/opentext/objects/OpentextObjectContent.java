package com.emc.kazeon.connector.opentext.objects;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import com.emc.kazeon.connector.websvc.connector.ObjectContent;
import com.emc.kazeon.connector.websvc.exception.connector.ConnectorException;
import com.emc.kazeon.csvc.stubs.CSVCFaultException;

public class OpentextObjectContent extends ObjectContent{
	private final InputStream ips;
	private final String mime;
	private final Charset cs;
	private final AutoCloseable disposableResources;
	private final String name;
	
	public OpentextObjectContent(InputStream is, String mime, Charset cs, AutoCloseable disposableResources, String name, Logger log) {
		super(log);
		this.ips = is;
		this.mime = mime;
		this.cs = cs;
		this.disposableResources = disposableResources;
		this.name = name;
	}

	public InputStream getIps() {
		return this.ips;
	}

	public String getMime() {
		return this.mime;
	}

	public Charset getCs() {
		return this.cs;
	}

	public AutoCloseable getDisposableResources() {
		return this.disposableResources;
	}

	

	

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String _getName() throws ConnectorException {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public Charset getCharset() throws ConnectorException, CSVCFaultException {
		// TODO Auto-generated method stub
		return this.cs;
	}

	@Override
	protected String _getContentType() throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getContent() throws ConnectorException {
		
		return this.ips;
	}

}
