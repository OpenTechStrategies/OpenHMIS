package org.pathways.openciss.client;

import org.pathways.openciss.shared.HUDXML3Blob;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("blobservice")
public interface BlobService extends RemoteService {

  String getBlobStoreUploadUrl();
  
  HUDXML3Blob getHUDXML3BlobMetadata(String id);

}
