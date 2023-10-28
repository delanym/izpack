package com.izforge.izpack.core.variable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConfigFileValueTest
{
    @TempDir
    static Path folder;

    private File properties;
    private File zipFile;
    private File jarFile;

    @BeforeEach
    public void setUp() throws Exception
    {
        properties = folder.resolve("test.properties").toFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(properties));
        out.write("test.path = C:\\mypath\\myfile\n");
        out.write("test.path2 = C:\\\\mypath\\\\myfile\n");
        out.close();

        byte[] buf = new byte[1024];

        try {
            zipFile = folder.resolve("test.zip").toFile();
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));
            FileInputStream in = new FileInputStream(properties);
            zout.putNextEntry(new ZipEntry("test.properties"));
            int len;
            while ((len = in.read(buf)) > 0) {
                zout.write(buf, 0, len);
            }
            zout.closeEntry();
            in.close();
            zout.close();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        try {
            jarFile = folder.resolve("test.jar").toFile();
            JarOutputStream jout = new JarOutputStream(new FileOutputStream(jarFile));
            FileInputStream in = new FileInputStream(properties);
            jout.putNextEntry(new JarEntry("test.properties"));
            int len;
            while ((len = in.read(buf)) > 0) {
                jout.write(buf, 0, len);
            }
            jout.closeEntry();
            in.close();
            jout.close();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPlainConfigFileValue()
    {
        PlainConfigFileValue value = new PlainConfigFileValue(properties.getPath(), ConfigFileValue.CONFIGFILE_TYPE_OPTIONS, null, "test.path", false);
        PlainConfigFileValue value2 = new PlainConfigFileValue(properties.getPath(), ConfigFileValue.CONFIGFILE_TYPE_OPTIONS, null, "test.path2", true);
        try
        {
            assertEquals("C:\\mypath\\myfile", value.resolve());
            assertEquals("C:\\mypath\\myfile", value2.resolve());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testZipConfigFileValue()
    {
        ZipEntryConfigFileValue value = new ZipEntryConfigFileValue(zipFile.getPath(), "test.properties", ConfigFileValue.CONFIGFILE_TYPE_OPTIONS, null, "test.path", false);
        ZipEntryConfigFileValue value2 = new ZipEntryConfigFileValue(zipFile.getPath(), "test.properties", ConfigFileValue.CONFIGFILE_TYPE_OPTIONS, null, "test.path2", true);
        try
        {
            assertEquals("C:\\mypath\\myfile", value.resolve());
            assertEquals("C:\\mypath\\myfile", value2.resolve());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testJarConfigFileValue()
    {
        JarEntryConfigValue value = new JarEntryConfigValue(zipFile.getPath(), "test.properties", ConfigFileValue.CONFIGFILE_TYPE_OPTIONS, null, "test.path", false);
        JarEntryConfigValue value2 = new JarEntryConfigValue(zipFile.getPath(), "test.properties", ConfigFileValue.CONFIGFILE_TYPE_OPTIONS, null, "test.path2", true);
        try
        {
            assertEquals("C:\\mypath\\myfile", value.resolve());
            assertEquals("C:\\mypath\\myfile", value2.resolve());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @AfterEach
    public void cleanUp() {
       assertTrue(properties.exists());
       assertTrue(zipFile.exists());
       assertTrue(jarFile.exists());
    }
}
