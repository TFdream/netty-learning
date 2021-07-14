package com.mindflow.netty4.common.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ricky Fung
 */
public class FileUtils {

    public static final long ONE_KB = 1024;

    public static final long ONE_MB = ONE_KB * ONE_KB;

    public static final long ONE_GB = ONE_KB * ONE_MB;

    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        forceDelete(directory);

        if (!directory.delete()) {
            throw new IOException("Unable to delete directory " + directory + ".");
        }
    }

    public static boolean deleteQuietly(File file) {
        if (file==null || !file.exists()) {
            return true;
        }
        try {
            if (file.isDirectory()) {
                forceDelete(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.delete();
    }

    private static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File child: files){
                forceDelete(child);
            }
            deleteFile(file);
        } else {
            deleteFile(file);
        }
    }

    public static void deleteFile(File file) throws IOException {
        if (file==null) {
            return;
        }
        if (!file.delete()) {
            throw new IOException("Unable to delete file: " + file);
        }
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcFile.exists() == false) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false) {
            if (destFile.getParentFile().mkdirs() == false) {
                throw new IOException("Destination '" + destFile + "' directory cannot be created");
            }
        }

        doCopyFile(srcFile, destFile, true);
    }

    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }
        FileInputStream in = new FileInputStream(srcFile);
        try {
            FileOutputStream out = new FileOutputStream(destFile);
            try{
                IoUtils.copy(in, out);
            }finally {
                IoUtils.closeQuietly(out);
            }
        }finally {
            IoUtils.closeQuietly(in);
        }
        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcDir.exists() == false) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (srcDir.isDirectory() == false) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }

        doCopyDirectory(srcDir, destDir, true);
    }

    private static void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (destDir.mkdirs() == false) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
            if (preserveFileDate) {
                destDir.setLastModified(srcDir.lastModified());
            }
        }
        File[] files = srcDir.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + srcDir);
        }
        for (int i = 0; i < files.length; i++) {
            File copiedFile = new File(destDir, files[i].getName());
            if (files[i].isDirectory()) {
                doCopyDirectory(files[i], copiedFile, preserveFileDate);
            } else {
                doCopyFile(files[i], copiedFile, preserveFileDate);
            }
        }
    }

    public static String readClassPathFile(String filename)
            throws IOException {

        return readClassPathFile(filename, "UTF-8");
    }

    public static String readClassPathFile(String filename, String charset)
            throws IOException {
        BufferedReader br = null;
        try{
            InputStream in = FileUtils.class.getResourceAsStream(filename);
            br = new BufferedReader(new InputStreamReader(in, charset));

            String line = null;
            StringBuilder sb = new StringBuilder(2048);
            while((line=br.readLine())!=null){
                sb.append(line);
            }
            return sb.toString();
        }finally {
            IoUtils.closeQuietly(br);
        }
    }

    public static String readFullText(File file, String charset) throws IOException {
        BufferedReader br = null;
        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            StringBuilder sb = new StringBuilder(2048);
            String line = null;
            while((line=br.readLine())!=null){
                sb.append(line);
            }
            return sb.toString();
        }finally {
            IoUtils.closeQuietly(br);
        }
    }

    public static List<String> readLines(InputStream input, String charset) throws IOException {
        BufferedReader br = null;
        try{
            br = new BufferedReader(new InputStreamReader(input, charset));
            List<String> stringList = new ArrayList<>();
            String line = null;
            while((line=br.readLine())!=null){
                stringList.add(line);
            }
            return stringList;
        }finally {
            IoUtils.closeQuietly(br);
        }
    }

    public static byte[] readBytes(File file) throws IOException {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            in = new FileInputStream(file);
            IoUtils.copy(in, out);
            return out.toByteArray();
        }finally{
            IoUtils.closeQuietly(out);
            IoUtils.closeQuietly(in);
        }
    }

    public static byte[] readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            IoUtils.copy(in, baos);
            return baos.toByteArray();
        }finally{
            IoUtils.closeQuietly(baos);
        }
    }

}
