package com.hansolinticube.gateway.utility;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class IniLoader {
    public static Ini getIni(String iniRelPath) throws IOException {
        String iniAbsPath = new File(iniRelPath).getAbsolutePath();
        return new Ini(new File(iniAbsPath));
    }
}
