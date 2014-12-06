package com.jobbrown.auction_room.thirdparty.gallen;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.rmi.RMISecurityManager;

public class SpaceUtils {
//  public static String defaultHostname = "JOBW8.home";
    public static String defaultHostname = "localhost";
    //public static String defaultHostname = "vb-job-ubuntu.home";
    private static JavaSpace instance = null;

    /*
    public static JavaSpace getSpace(String hostname) {
        JavaSpace js = null;

        try {
            LookupLocator l = new LookupLocator("jini://" + hostname);

            ServiceRegistrar sr = l.getRegistrar();

            Class c = Class.forName("net.jini.space.JavaSpace");
            Class[] classTemplate = {c};

            js = (JavaSpace) sr.lookup(new ServiceTemplate(null, classTemplate, null));

        } catch (Exception e) {
            System.err.println("Error: " + e);
        }

        return js;
    }
    */
    public static JavaSpace getSpace(String hostname) {
        if(instance == null) {
            synchronized (SpaceUtils.class) {
                JavaSpace js = null;

                try {
                    LookupLocator l = new LookupLocator("jini://" + hostname);

                    ServiceRegistrar sr = l.getRegistrar();

                    Class c = Class.forName("net.jini.space.JavaSpace");
                    Class[] classTemplate = {c};

                    js = (JavaSpace) sr.lookup(new ServiceTemplate(null, classTemplate, null));

                } catch (Exception e) {
                    System.err.println("Error: " + e);
                }
                instance = js;
            }
        }

        return instance;
    }

    public static JavaSpace getSpace() {
        return getSpace(SpaceUtils.defaultHostname);
    }


    public static TransactionManager getManager(String hostname) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        TransactionManager tm = null;
        try {
            LookupLocator l = new LookupLocator("jini://" + hostname);


            ServiceRegistrar sr = l.getRegistrar();

            Class c = Class.forName("net.jini.core.transaction.server.TransactionManager");
            Class[] classTemplate = {c};

            tm = (TransactionManager) sr.lookup(new ServiceTemplate(null, classTemplate, null));

        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
        return tm;
    }

    public static TransactionManager getManager() {
        return getManager(SpaceUtils.defaultHostname);
    }
}