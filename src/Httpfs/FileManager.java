package Httpfs;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    Path directory;



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

        // Potentially used if overwrite control is used

        boolean fileCreated = file.createNewFile();


        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(body);
        writer.close();
    }

    public String readFile(String path) throws IOException {
        if (path.equals("/")) {
            return listFilesInDirectory(new File(directory.toString()), 0);
        }
        path = directory.toString() + path;
        File file = new File(path);
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder fileStringBuilder = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                fileStringBuilder.append(line).append("\n");
            }

            return fileStringBuilder.toString();
        }
        return null;
    }

    public String listFilesInDirectory(File folder, int indents) throws IOException{
        StringBuilder sb = new StringBuilder();
        for (final File fileEntry : folder.listFiles()){
            if (fileEntry.isDirectory()) {
                sb.append("\n").append("\t".repeat(Math.max(0, indents)));
                sb.append("dir: ").append(fileEntry.getName()).append("\n");
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
