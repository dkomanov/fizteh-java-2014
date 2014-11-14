package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap2;

import java.io.IOException;
import java.nio.file.*;

/**
 * Created by Aliaksei Semchankau on 14.11.2014.
 */
public class DeleteFunctions {

    public static void deleteTable(Path pathTable) {
        try {
            DirectoryStream<Path> listOfDirs = Files.newDirectoryStream(pathTable);
            for (Path innerDirection : listOfDirs) {
                deleteDirectory(innerDirection);
            }
            try {
                listOfDirs.close();
            } catch (IOException ioexc) {
                throw new DatabaseException("cant close a list of dirs in " + pathTable.toString());
            }
        } catch (IOException ioexc) {
            throw new DatabaseException("can't open list of directories in " + pathTable.toString());
        }

        try {
            Files.delete(pathTable);
        } catch (IOException ioexc) {
            throw new DatabaseException("can't delete table " + pathTable.toString());
        }

    }

    public static void deleteDirectory(Path innerDirection) {

        //System.out.println("gonna delete: " + innerDirection.toString());

        try {
            DirectoryStream<Path> listOfFiles = Files.newDirectoryStream(innerDirection);


            for (Path innerFile : listOfFiles) {
                //System.out.println("gonna delete file: " + innerFile.toString());
                Files.delete(innerFile);
            }

            try {
                listOfFiles.close();
            } catch (IOException ioexc) {
                throw new DatabaseException("can't closw a DirectoreStream<Path> " + innerDirection.toString());

            }
        } catch (IOException ioexc) {
            throw new DatabaseException("cant open a list of files " + innerDirection);
        }
       /*File curDir;
        try {
            System.out.println(innerDirection.toString() + " would like to delete");
            curDir = new File(innerDirection.toString());
            File[] listOfFiles = curDir.listFiles();
            for (File file : listOfFiles) {
                System.out.println(file.toString());
                if (Files.isDirectory(file.toPath())) {
                    throw new DatabaseException("great mistake! " + file.toString());
                }
                Files.delete(file.toPath());
                System.out.println("success " + file.toString());
            }
        }  catch (NoSuchFileException e) {
            throw new DatabaseException("no such file: " + innerDirection.toString());
        } catch (DirectoryNotEmptyException e) {
            throw new DatabaseException("directory is not empty: " + innerDirection.toString());
        } catch (IOException e) {
            throw new DatabaseException("ioexception " + innerDirection.toString());
        }*/

        try {
            Files.delete(innerDirection);
        } catch (NoSuchFileException e) {
            throw new DatabaseException("no such file: " + innerDirection.toString());

        } catch (DirectoryNotEmptyException e) {
            throw new DatabaseException("directory is not empty: " + innerDirection.toString());

        } catch (IOException e) {
            throw new DatabaseException("ioexception " + innerDirection.toString());

        }
    }

    public static void deleteFile(Path innerFile) {

        try {
            Files.delete(innerFile);
        } catch (IOException ioexc) {
            throw new DatabaseException("can't delete file " + innerFile);
        }

    }

}