package com.doopp.gauss.server.application;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ApplicationProperties extends Properties {

    public ApplicationProperties() {
        String propertiesConfig = System.getProperty("applicationPropertiesConfig");
        try {
            FileReader fileReader = new FileReader(propertiesConfig);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            this.load(bufferedReader);
        }
        catch(IOException e) {
            System.out.print("\n Can not load properties file -> " + propertiesConfig);
        }
    }

    public FileInputStream r(String key) throws FileNotFoundException {
        return new FileInputStream(this.getProperty(key));
    }

    public String s(String key) {
        return this.getProperty(key);
    }

    public int i(String key) {
        return Integer.valueOf(this.getProperty(key));
    }

    public boolean b(String key) {
        return Boolean.valueOf(this.getProperty(key));
    }
}
