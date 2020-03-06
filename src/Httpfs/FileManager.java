package Httpfs;

import kotlin.jvm.Synchronized;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileManager {
    Path directory;
    HashMap<String, ReadWriteLock> fileAccessHashMap = new HashMap<String, ReadWriteLock>();


    FileManager(Path directory) {
        this.directory = directory;
    }

    /**
     * Ensure path is within directory
     */
    public boolean isFilePathValid(String path){
        String[] pathNodes = path.split("\r");
        int movement = 0;
        for (String node : pathNodes) {
            if (node.equals("..")) {
                movement--;
            } else {
                movement++;
            }
            if (movement < 0) {
                return false;
            }
        }
        return true;
    }

    public void writeToFile(String path, String body) throws IOException {
        path = directory.toString() + path;
        File file = new File(path);
        ReadWriteLock lock = getFileLock(path);
        Lock writeLock = lock.writeLock();
        // Potentially used if overwrite control is used
        boolean fileCreated = file.createNewFile();

        try {
            writeLock.lock();
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(body);
            writer.close();
        }
        finally {
            writeLock.unlock();
        }
    }

    public String readFile(String path) throws IOException {
        path = directory.toString() + path;
        File file = new File(path);
        ReadWriteLock lock = getFileLock(path);
        Lock readLock = lock.readLock();

        try {
            readLock.lock();
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder fileStringBuilder = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    fileStringBuilder.append(line).append("\n");
                }

                return fileStringBuilder.toString();
            }
        } finally {
            readLock.unlock();
        }
        return null;
    }

    //Map currently not cleaned
    @Synchronized
    private ReadWriteLock getFileLock(String filePath) {
        if (fileAccessHashMap.containsKey(filePath)) {
            return fileAccessHashMap.get(filePath);
        }

        ReadWriteLock lock = new ReentrantReadWriteLock();
        fileAccessHashMap.put(filePath, lock);
        return lock;
    }

    public String listFilesInDirectory(File folder, int indents) throws IOException{
        StringBuilder sb = new StringBuilder();
        for (final File fileEntry : folder.listFiles()){
            if (fileEntry.isDirectory()) {
                sb.append("\t".repeat(Math.max(0, indents)));
                sb.append(fileEntry.getName()).append("\n");
                sb.append(listFilesInDirectory(fileEntry, indents + 1));
            }
            else {
                sb.append("\t".repeat(Math.max(0, indents)));
                sb.append(fileEntry.getName()).append("\n");
            }
        }
        return sb.toString();
    }

    private void canRead(File file) {
    }

    private void canWrite(File file) {

    }
}
