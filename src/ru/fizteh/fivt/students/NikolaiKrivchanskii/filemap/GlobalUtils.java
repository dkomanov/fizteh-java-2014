package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.ShellState;

public class GlobalUtils {
	public static final Charset ENCODING = StandardCharsets.UTF_8;
	public static final int DIR_QUANTITY = 16;
    public static final int FILES_PER_DIR = 16;
	private static final Pattern DIR_PATTERN = Pattern.compile("([^\\.]+).dir");
    private static final Pattern FILE_PATTERN = Pattern.compile("([^\\.]+).dat");
	
    public static String uniteItems(Collection<?> items, String separator) {
        boolean isFirstIteration = true;
        StringBuilder joinBuilder = new StringBuilder();
        for (Object item: items) {
            if(isFirstIteration) {
                isFirstIteration = false;
            } else {
                joinBuilder.append(separator);
            }
            joinBuilder.append(item.toString());
        }
        return joinBuilder.toString();
    }
    
    public static void closeCalm(Closeable something) {
        try {
            if (something != null) {
                something.close();
            }
        } catch (IOException e) {
            System.err.println("Error aqcuired while trying to close " + e.getMessage());
        }
    }
    
    public static File getAbsoluteName(String fileName, ShellState state) {
        File file = new File(fileName);
        
        if (!file.isAbsolute()){
            file = new File(state.getCurDir(), fileName);
        }
        return file;
    }
    public static byte[] getBytes(String string, Charset encoding) {
        byte[] bytes = null;
        bytes = string.getBytes(encoding);
        return bytes;
    }
    
    public static byte[] bytesToArray(ByteArrayOutputStream bytes) {
        byte[] result = new byte[bytes.size()];
        result = bytes.toByteArray();
        return result;
    }
    
    public static boolean doesExist(String path) {
        File file = new File(path);
        return file.exists();
    }
    
    public static int countBytes(String string, Charset encoding) {
            byte[] bytes = string.getBytes(encoding);
            return bytes.length;
    }
    
    public static int getKeysLength(Set<String> keys, Charset charset) {
        int keysLength = 0;
        for (final String key : keys) {
            int keyLength = countBytes(key, charset);
            keysLength += keyLength + 5;
        }
        return keysLength;
    }
    
    public static boolean compare(Object key1, Object key2) {
        if (key1 == null && key2 == null) {
            return true;
        } else if (key1 == null || key2 == null) {
            return false;
        } else {
        	return key1.equals(key2);
        }
    }
    
    public static void deleteFile(File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }
        if (fileToDelete.isDirectory()) {
            for (final File file : fileToDelete.listFiles()) {
                deleteFile(file);
            }
        }
        fileToDelete.delete();
    }
    
    public static int parseDirNumber(File dir) {
        String name = dir.getName();
        Matcher matcher = DIR_PATTERN.matcher(name);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("incorrect dir name");
    }
    
    public static int parseFileNumber(File file) {
        String name = file.getName();
        Matcher matcher = FILE_PATTERN.matcher(name);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("incorrect file name");
    }
    
    public static void checkKeyPlacement(String key, int currentDir, int currentFile)
    {
        if (currentDir != getDirNumber(key) || currentFile != getFileNumber(key)) {
            throw new IllegalArgumentException("invalid key placement");
        }
    }
    
    public static String parseTableName(String params)
    {
        int index = params.indexOf(' ');
        if (index == -1)
        {
            return params;
        }
        return params.substring(0, index);
    }
    
    public static int getDirNumber(String key) {
        byte[] bytes = GlobalUtils.getBytes(key, GlobalUtils.ENCODING);
        return Math.abs(bytes[0] % DIR_QUANTITY);
    }

    public static int getFileNumber(String key) {
        byte[] bytes = GlobalUtils.getBytes(key, GlobalUtils.ENCODING);
        return Math.abs(bytes[0] / DIR_QUANTITY % FILES_PER_DIR);
    }
    
}