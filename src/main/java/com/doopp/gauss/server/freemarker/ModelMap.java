package com.doopp.gauss.server.freemarker;

import java.util.LinkedHashMap;

public class ModelMap extends LinkedHashMap<String, Object> {

    public ModelMap() {
    }

    public void addAttribute(String attributeName, Object attributeValue) {
        this.put(attributeName, attributeValue);
    }
}
