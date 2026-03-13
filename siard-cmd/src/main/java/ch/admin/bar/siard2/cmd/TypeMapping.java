package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siard2.api.MetaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeMapping extends Mapping {
    private String _sMappedTypeName = null;

    String getMappedTypeName() {
        return _sMappedTypeName;
    }

    private Map<String, String> _mapAttributes = new HashMap<String, String>();

    String getMappedAttributeName(String sAttributeName) {
        return _mapAttributes.get(sAttributeName);
    }

    private TypeMapping(String sMappedTypeName, MetaType mt, int iMaxColumnNameLength) {
        _sMappedTypeName = sMappedTypeName;
        List<String> listAttributes = new ArrayList<String>();
        for (int iAttribute = 0; iAttribute < mt.getMetaAttributes(); iAttribute++) {
            MetaAttribute ma = mt.getMetaAttribute(iAttribute);
            listAttributes.add(ma.getName());
        }
        _mapAttributes = getDisambiguated(listAttributes, iMaxColumnNameLength);
    }

    public static TypeMapping newInstance(String sMappedTypeName, MetaType mt, int iMaxColumnNameLength) {
        return new TypeMapping(sMappedTypeName, mt, iMaxColumnNameLength);
    }

}
