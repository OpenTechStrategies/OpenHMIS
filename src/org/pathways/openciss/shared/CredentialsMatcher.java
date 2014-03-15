package org.pathways.openciss.shared;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
//import org.apache.shiro.crypto.hash.Sha256Hash;
//import org.apache.shiro.util.StringUtils;
//import org.apache.commons.lang.StringUtils;

public class CredentialsMatcher extends SimpleCredentialsMatcher {
	 
    @Override
    public boolean doCredentialsMatch(AuthenticationToken tok, AuthenticationInfo info)
    {
        String tokenCredentials = charArrayToString(tok.getCredentials());
        System.out.println("tokenCredentials is: '" + tokenCredentials + "'");
        //String reverseTokenCredentials = StringUtils.reverse(tokenCredentials);
        //String reverseEncryptedTokenCredentials = new Sha256Hash(reverseTokenCredentials).toString();
        //String encryptedTokenCredentials = new Sha256Hash(tokenCredentials).toString();
        String accountCredentials = charArrayToString(info.getCredentials());
        System.out.println("accountCredentials is: '" + accountCredentials + "'");
        //System.out.println("reverse encrypted tokenCredentials: " + reverseEncryptedTokenCredentials);
        //System.out.println("encrypted tokenCredentials: '" + encryptedTokenCredentials + "'");
        boolean result = accountCredentials.equals(tokenCredentials);
        //boolean result = accountCredentials.equals(encryptedTokenCredentials);
        System.out.println("Do credentials match?: " + result);
        //System.out.println("Do encrypted token credentials match? " + accountCredentials.equals(encryptedTokenCredentials));
        //System.out.println("Returned result is: " + result);
        return result;
    }
 
    private String charArrayToString(Object credentials) {
        return new String((char[]) credentials);
    }
}
