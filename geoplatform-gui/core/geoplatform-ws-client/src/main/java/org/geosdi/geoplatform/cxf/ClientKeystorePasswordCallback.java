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
    
    private Map<String, String> passwords = 
        new HashMap<String, String>();
    
    public ClientKeystorePasswordCallback() {
        passwords.put("client", "clientpwd");
        passwords.put("server", "serverstorepwd");
//        passwords.put("alice", "password");
//        passwords.put("bob", "password");
    }

    /**
     * It attempts to get the password from the private 
     * alias/passwords map.
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
            
            logger.info("########### Alias client: " + pc.getIdentifier());

            String pass = passwords.get(pc.getIdentifier());
            if (pass != null) {
                pc.setPassword(pass);
                return;
            }
        }
    }
    
    /**
     * Add an alias/password pair to the callback mechanism.
     */
    public void setAliasPassword(String alias, String password) {
        passwords.put(alias, password);
    }
    
}
