package org.geosdi.geoplatform.cxf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSPasswordCallback;

public class ClientKeystorePasswordCallback implements CallbackHandler {

    private Log logger = LogFactory.getLog(this.getClass());
    //    
    private Map<String, String> passwords = new HashMap<String, String>();

    public ClientKeystorePasswordCallback() {
        passwords.put("Alice", "ecilA");
        passwords.put("abcd", "dcba");
        passwords.put("clientx509v1", "storepassword");
        passwords.put("serverx509v1", "storepassword");
//        passwords.put("client", "clientpwd");
//        passwords.put("server", "serverstorepwd");
////        passwords.put("alice", "password");
////        passwords.put("bob", "password");
    }

    /**
     * Add an alias/password pair to the callback mechanism.
     */
    public void setAliasPassword(String alias, String password) {
        passwords.put(alias, password);
    }

    /**
     * It attempts to get the password from the private 
     * alias/passwords map.
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

//            logger.info("########### Alias client: " + pc.getIdentifier());
//            logger.info("########### pc.getUsage(): " + pc.getUsage());
//
////            logger.info("########### WSPasswordCallback.SECRET_KEY: " + WSPasswordCallback.SECRET_KEY);
//            logger.info("########### WSPasswordCallback.CUSTOM_TOKEN: " + WSPasswordCallback.CUSTOM_TOKEN);
//            logger.info("########### WSPasswordCallback.SECURITY_CONTEXT_TOKEN: " + WSPasswordCallback.SECURITY_CONTEXT_TOKEN);
//            logger.info("########### WSPasswordCallback.SIGNATURE: " + WSPasswordCallback.SIGNATURE);
//            logger.info("########### WSPasswordCallback.USERNAME_TOKEN: " + WSPasswordCallback.USERNAME_TOKEN);
//            logger.info("########### WSPasswordCallback.DECRYPT: " + WSPasswordCallback.DECRYPT);
//            logger.info("########### WSPasswordCallback.ENCRYPTED_KEY_TOKEN: " + WSPasswordCallback.ENCRYPTED_KEY_TOKEN);
//            logger.info("########### WSPasswordCallback.USERNAME_TOKEN_UNKNOWN: " + WSPasswordCallback.USERNAME_TOKEN_UNKNOWN);
//            logger.info("########### WSPasswordCallback.KEY_NAME: " + WSPasswordCallback.KEY_NAME);

            String pass = passwords.get(pc.getIdentifier());
            if (pass != null) {
                pc.setPassword(pass);
                return;
            }
        }
    }
}
