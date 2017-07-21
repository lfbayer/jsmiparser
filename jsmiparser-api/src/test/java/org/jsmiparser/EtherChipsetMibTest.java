package org.jsmiparser;

import org.jsmiparser.smi.SmiOidMacro;
import org.jsmiparser.smi.SmiOidNode;
import org.jsmiparser.smi.SmiVersion;

public class EtherChipsetMibTest extends AbstractMibTestCase {
    public EtherChipsetMibTest() {
        super(SmiVersion.V2,
                LIBSMI_MIBS_URL + "/ietf/ETHER-CHIPSET-MIB",
                LIBSMI_MIBS_URL + "/iana/IANAifType-MIB",
                LIBSMI_MIBS_URL + "/ietf/IF-MIB",
                LIBSMI_MIBS_URL + "/ietf/EtherLike-MIB");
    }

    public void testObjectIdentity() {
        SmiOidNode dot3ChipSetAMD7990 = getMib().findByOid(1, 3, 6, 1, 2, 1, 10, 7, 8, 1, 1);
        assertNotNull(dot3ChipSetAMD7990);

        SmiOidMacro macro = (SmiOidMacro) dot3ChipSetAMD7990.getSingleValue();
        assertEquals("dot3ChipSetAMD7990", macro.getId());
        assertEquals("The authoritative identifier for the Advanced\n"
                + "                    Micro Devices Am7990 Local Area Network\n"
                + "                    Controller for Ethernet (LANCE).", macro.getDescription());
    }
}
