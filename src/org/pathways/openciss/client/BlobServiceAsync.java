package org.pathways.openciss.client;

import org.pathways.openciss.shared.HUDXML3Blob;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BlobServiceAsync {

	void getBlobStoreUploadUrl(AsyncCallback<String> callback);
	void getHUDXML3BlobMetadata(String id, AsyncCallback<HUDXML3Blob> callback);
	
}