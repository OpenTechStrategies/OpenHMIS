package org.pathways.openciss.shared;
// check out http://jaxb.java.net/nonav/2.2.5-2/docs/ch05.html#prefixmapper
//import com.sun.xml.internal.bind.v2.WellKnownNamespace;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.bind.v2.WellKnownNamespace;

public class NamespacePrefixMap extends NamespacePrefixMapper {
        
	//@SuppressWarnings("deprecation")
	//@SuppressWarnings("deprecation")
	@Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { WellKnownNamespace.XML_SCHEMA_INSTANCE,  };
    }

	//@SuppressWarnings("deprecation")
	@Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        if (namespaceUri.equals(WellKnownNamespace.XML_SCHEMA_INSTANCE))
            return "xsi";
        if (namespaceUri.equals(WellKnownNamespace.XML_SCHEMA))
            return "xs";
        if (namespaceUri.equals(WellKnownNamespace.XML_MIME_URI))
            return "xmime";
        return suggestion;
    }
}
