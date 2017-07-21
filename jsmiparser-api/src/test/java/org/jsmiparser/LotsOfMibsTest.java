package org.jsmiparser;

import org.jsmiparser.parser.SmiDefaultParser;
import org.jsmiparser.parser.SmiParser;
import org.jsmiparser.phase.xref.XRefFallbackResolver;
import org.jsmiparser.smi.SmiMib;
import org.jsmiparser.smi.SmiModule;
import org.jsmiparser.smi.SmiSymbol;
import org.jsmiparser.smi.SmiVersion;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LotsOfMibsTest extends AbstractMibTestCase {
    public LotsOfMibsTest() {
        super(SmiVersion.V2);
    }

    public void testParser() {
        getMib();
    }

    @Override
    protected SmiParser createParser() throws Exception {

        LoggerFactory.getLogger(getClass()).info("Starting test");

        List<URL> urls = new ArrayList<URL>();
        addMibs(urls, new File("/Users/lbayer/dev/lvi/master/assets/mibs"));
//        File[] folders = new File("/Users/lbayer/dev/netdisco-mibs").listFiles();
//        if (folders != null) {
//            for (File folder : folders) {
//                if (folder.isDirectory()) {
//                    addMibs(urls, folder);
//                }
//            }
//        }

        SmiDefaultParser parser = (SmiDefaultParser) super.createParser();
        parser.getXRefPhase().setFallbackResolver(new FallbackResolver());
        parser.getFileParserPhase().setInputUrls(urls);
        return parser;
    }

    private void addMibs(List<URL> list, File file) throws IOException {
        String name = file.getName();
        if (name.startsWith(".") || name.endsWith("~") || name.equals("EXTRAS") || name.equals("README")) {
            return;
        }

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addMibs(list, child);
                }
            }
        }
//        else if (file.isFile() && !file.getName().startsWith(".") && file.getName().contains("CISCO-HSRP-CAPABILITY.my")) {
//        else if (file.isFile() && !file.getName().startsWith(".") && file.getName().contains("MPLS-LDP-CAPABILITY.my")) {
//        else if (file.isFile() && !file.getName().startsWith(".") && matches(file, "SNMPv2-SMI", "SNMPv2-SMI.my", "CISCO-CABLE-LICENSE-MIB.my", "SNMPv2-TC", "SNMPv2-CONF", "SNMP-FRAMEWORK-MIB", "CISCO-SMI")) {
        else if (file.isFile()) {
            list.add(file.getCanonicalFile().toURI().toURL());
        }
    }

    private boolean matches(File file, String...names) {
        String name = file.getName();
        for (String pattern : names) {
            if (name.equals(pattern)) {
                return true;
            }
        }

        return false;
    }

    private static class FallbackResolver implements XRefFallbackResolver {
        private final Map<String, String[]> SMI_FALLBACKS;
        private final Map<String, String> DEFAULT_IMPORT_MIB;

        FallbackResolver() {
            SMI_FALLBACKS = new HashMap<String, String[]>();
            SMI_FALLBACKS.put("SNMPv2-SMI", new String[] {"SNMPv2-CONF", "SNMPv2-TC", "RFC1155-SMI"});
            SMI_FALLBACKS.put("RFC-1213", new String[]{"RFC1213-MIB"});
            SMI_FALLBACKS.put("RFC1212", new String[]{"RFC-1212"});
            SMI_FALLBACKS.put("SNMPv2-SMI-v1", new String[]{"SNMPv2-SMI"});

            DEFAULT_IMPORT_MIB = new HashMap<String, String>();
            DEFAULT_IMPORT_MIB.put("InetAddress", "INET-ADDRESS-MIB");
            DEFAULT_IMPORT_MIB.put("InetAddressType", "INET-ADDRESS-MIB");
            DEFAULT_IMPORT_MIB.put("InterfaceIndex", "IF-MIB");
        }

        public SmiModule findModule(SmiMib mib, String id) {
            String[] fallbacks = SMI_FALLBACKS.get(id);
            if (fallbacks != null) {
                for (String fallback : fallbacks) {
                    SmiModule fallbackMib = mib.findModule(fallback);
                    if (fallbackMib != null) {
                        return fallbackMib;
                    }
                }
            }

            return null;
        }

        public SmiSymbol findSymbol(SmiMib mib, String id) {
            String importMib = DEFAULT_IMPORT_MIB.get(id);
            if (importMib != null) {
                return getSymbol(mib, importMib, id);
            }

            return getSymbol(mib, "SNMPv2-SMI", id);
        }

        public SmiSymbol findSymbol(SmiMib mib, String moduleId, String symbolId) {
            if (symbolId.equals("UInteger32")) {
                return getSymbol(mib, "SNMPv2-SMI", "Gauge32");
            }

            String[] fallbacks = SMI_FALLBACKS.get(moduleId);
            if (fallbacks != null) {
                for (String fallback : fallbacks) {
                    SmiSymbol symbol = getSymbol(mib, fallback, symbolId);
                    if (symbol != null) {
                        return symbol;
                    }
                }
            }

            return getSymbol(mib, "SNMPv2-SMI", symbolId);
        }

        private SmiSymbol getSymbol(SmiMib mib, String moduleId, String symbolId) {
            SmiModule module = mib.findModule(moduleId);
            if (module == null) {
                return null;
            }

            return module.findSymbol(symbolId);
        }
    }
}
