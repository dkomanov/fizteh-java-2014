package ru.fizteh.fivt.students.vladislav_korzun.FileMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class FileMap {
    public static void main(final String[] args) {
        try {
            String path = System.getProperty("db.file");
            if (!Paths.get(path).toFile().exists()) {
                Paths.get(path).toFile().createNewFile();
            }
            RandomAccessFile dbfile = new RandomAccessFile(path, "rw");
            DbMain db = new DbMain();
            if (dbfile.length() > 0) {
                db.read(dbfile);
            }
            CommandExecutor commands = new CommandExecutor();
            commands.executeCommands(args, db, dbfile);
            dbfile.close();
            } catch (Exception e) {
                System.err.println("Error connecting database");
            }
        }
    }

class Commands {
    
    public Map<String, String> filemap;
    
    Commands(DbMain db) {
        filemap = db.filemap;
    }
    
    void put(String key, String value) {
        try {
            String val = filemap.put(key, value);
            if (val == null) {
                System.out.println("new");
            } else {
                System.out.println("owerwrite");
                System.out.println(val);
            }            
        } catch (NullPointerException e) {
            System.err.println("Key or value is null");
        }
    }
    
    void get(String key) {
        try {
            String val = filemap.get(key);
            if (val.equals(null)) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(val);
            }            
        } catch (NullPointerException e) {
            System.err.println("Key or value is null");
        } 
    }
    
    void remove(String key) {
        try {
            String val = filemap.remove(key);
            if (val.equals(null)) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }            
        } catch (NullPointerException e) {
            System.err.println("Key or value is null");
        } 
    }
    
    void list() {
        Set<String> keys = filemap.keySet();
        String joined = String.join(", ", keys);
        System.out.println(joined);
    }
    
    void exit(DbMain db, RandomAccessFile dbfile) {
        db.filemap = filemap;
        db.write(dbfile);
    }   
  
}

class Parser {
    String[] semicolonparser(final String arg) {
        String[] answer = null;
        String buffer = new String();
        buffer = arg.trim();    
        answer = buffer.split(";");
        return answer;
    }
    
    String[] spaceparser(final String arg) {
        String[] answer = null;
        String buffer = new String();
        buffer = arg.trim();
        answer = buffer.split(" ");
        return answer;
    }
}

class DbMain {   
    
    public Map<String, String> filemap;
    
    DbMain() {
        filemap = new TreeMap<String, String>();
    }
    
    void read(RandomAccessFile db) {
        List<Integer> offset = new LinkedList<Integer>() ;
        int counter = 0;               
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        List<String> keys = new LinkedList<String>();
        Byte bt = null;
        try {
            do {
                do {
                    bt = db.readByte();
                    counter++;
                    if (bt != 0) {
                        buffer.write(bt);
                    }
                } while (bt != 0);
                keys.add(buffer.toString("UTF-8"));
                buffer.reset();
                offset.add(db.readInt());
                counter += 4;
            } while (counter != offset.get(0));
            offset.add((int) db.length());
            for (int i = 1; i < offset.size(); i++) {
                while (offset.get(i) > counter) {
                    bt = db.readByte();
                    counter++;
                    buffer.write(bt);
                }
                filemap.put(keys.get(i - 1), buffer.toString("UTF-8"));
                buffer.reset();
            }
            buffer.close();
        } catch (UnsupportedEncodingException e) {
            System.err.println("Named charset is not supported");
        } catch (IOException e) {
            System.err.println("Invalid input");
        } 
    }
    
    void write(RandomAccessFile db) {     
        try {
            db.setLength(0);
            LinkedList<Integer> offset = new LinkedList<Integer>();
            Set<String> keys = filemap.keySet();            
            for (String key : keys) {
                db.write(key.getBytes("UTF-8"));
                db.write('\0');
                offset.add((int) db.getFilePointer());
                db.writeInt(0);
            }
            Collection<String> vals = filemap.values();           
            int i = 0;
            int pointer = 0;
            for (String val: vals) {
                pointer = (int) db.getFilePointer(); 
                db.seek(offset.get(i));
                i++;
                db.writeInt(pointer);
                db.seek(pointer);
                db.write(val.getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Named charset is not supported ");
        } catch (IOException e) {
            System.err.println("Pos is less than 0 or if an I/O error occurs");
        }
    }
}

class CommandExecutor {
    void executeCommands(String[] args, DbMain db, RandomAccessFile dbfile) {
        Scanner in = new Scanner(System.in);
        Commands command = new Commands(db);
        String request = new String();
        Parser parser = new Parser();
        String[] arg = null;
        String[] arg2 = null;
        String key = new String();
        String value = new String();
        int i = 0;
        do {
            if (args.length == 0) {
                System.out.print("$ ");
                request = in.nextLine();
            } else {
                for (i = 0; i < args.length; i++) {
                    request += (args[i] + " ");
                }                
                request = request + ";exit";
            }            
            arg = parser.semicolonparser(request);
            for (i = 0; i < arg.length; i++) {
                arg2 = parser.spaceparser(arg[i]);
                try {
                    switch(arg2[0]) {
                        case "put":
                            key = arg2[1];
                            value = arg2[2];
                            command.put(key, value);
                            break;
                        case "get":
                            key = arg2[1];
                            command.get(key);
                            break;
                        case "remove":
                            key = arg2[1];
                            command.remove(key);
                            break;
                        case "list": 
                            command.list();
                            break;
                        case "exit":
                            in.close();
                            command.exit(db, dbfile);
                            break;
                        default:
                            System.err.println("Invalid command");
                            if (args.length > 0) {
                                in.close();
                                System.exit(1);
                            }
                            break;
                        }
                } catch (Exception e) {
                    System.err.println("Invalid input");
                }
            }
        } while(!arg2[0].equals("exit"));
    }
}
