package ru.fizteh.fivt.students.annafedyushkina.TreeDepth;

import java.io.*;
import static java.lang.Integer.parseInt;

public class FindingDepth {

    private File fileIn;
    private File fileOut;
    private File directory;
    int root;

    FindingDepth(File fin, File fout, File dir) { //конструктор
        this.fileIn = fin;
        this.fileOut = fout;
        this.directory = dir;
        root = 0;
    }

    File initFile() { //найдем корень, запишем ко всем, кроме него 1
        File ifile = null;
        try {
            ifile = File.createTempFile("init_file", ".txt", directory);
        } catch (IOException e) {
            System.err.println("Some problems with file creation =^.^=");
            System.exit(1);
        }
        try (BufferedReader read = new BufferedReader(new FileReader(fileIn))) {
            try (BufferedWriter write = new BufferedWriter(new FileWriter(ifile))) {
                String str;
                int maxNumber = 0;
                while ((str = read.readLine()) != null) {
                    String[] strings = str.split(" ");
                    if (parseInt(strings[0]) > maxNumber) {
                        maxNumber = parseInt(strings[0]);
                    }
                    if (!strings[1].equals("0")) {
                        root = (root ^ parseInt(strings[1]));
                        write.write(str + " 1\n");
                    }
                }
                for (int i = 1; i <= maxNumber; i++) {
                    root = (root ^ i);
                }
                write.write(root + " " + root + " 0\n");
            }
        } catch (IOException e) {
            System.err.println("Some problems with file creation =^.^=");
            System.exit(2);
        }
        return ifile;
    }

    int numberOfLines(File f) { //считает количество линий в файле
        int number = 0;
        try (BufferedReader to_count = new BufferedReader(new FileReader(f))) {
            while (to_count.readLine() != null) {
                number++;
            }
        } catch (IOException e) {
            System.err.println("Runtime error =^.^=");
            System.exit(1);
        }
        return number;
    }

    File merge(File first, File second, int ind) { // сливает 2 файла, ind - по какому числу в строке мы сливаем
        File res = null;
        try {
            res = File.createTempFile("merge", ".txt", directory);
        } catch (IOException e) {
            System.err.println("Error with creation =^.^=");
            System.exit(1);
        }
        try (BufferedReader firstRead = new BufferedReader(new FileReader(first))) {
            try (BufferedReader secondRead = new BufferedReader(new FileReader(second))) {
                try (BufferedWriter resWrite = new BufferedWriter(new FileWriter(res))) {
                    String str1 = firstRead.readLine();
                    String str2 = secondRead.readLine();
                    while (str1 != null || str2 != null) {
                        if (str1 == null) {
                            resWrite.write(str2 + "\n");
                            str2 = secondRead.readLine();
                        } else if (str2 == null) {
                            resWrite.write(str1 + "\n");
                            str1 = firstRead.readLine();
                        } else if ((parseInt(str1.split(" ")[ind]) < parseInt(str2.split(" ")[ind]))) {
                            resWrite.write(str1 + "\n");
                            str1 = firstRead.readLine();
                        } else {
                            resWrite.write(str2 + "\n");
                            str2 = secondRead.readLine();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error with merging =^.^=");
            System.exit(1);
        }
        return res;
    }

    File mergeSort(File file, int ind) { //сортировка слиянием, ind - по какому числу в строке мы сортируем
        int lines = numberOfLines(file);
        if (lines <= 1) {
            return file;
        }
        File first = null;
        File second = null;
        try {
            first = File.createTempFile("mergeSort1", ".txt", directory);
            second = File.createTempFile("mergeSort2", ".txt", directory);
        } catch (IOException e) {
            System.err.println("Error with creating =^.^=");
            System.exit(1);
        }
        try (BufferedReader fileRead = new BufferedReader(new FileReader(file))) {
            try (BufferedWriter firstWrite = new BufferedWriter(new FileWriter(first))) {
                try (BufferedWriter secondWrite = new BufferedWriter(new FileWriter(second))) {
                    int i = 0;
                    for (; i < (lines / 2); i++) {
                        String s = fileRead.readLine();
                        firstWrite.write(s + "\n");
                    }
                    for (; i < lines; i++) {
                        String s = fileRead.readLine();
                        secondWrite.write(s + "\n");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't merge files =^.^=");
            System.exit(1);
        }
        File first1 = mergeSort(first, ind);
        File second1 = mergeSort(second, ind);
        return (merge(first1, second1, ind));
    }

    int countDepth(File file) { //будем менять отцов у вершин. так как каждый раз мы перескакиваем через отца,
        //то получим логарифм
        File first;
        File second;
        boolean goOn = true;
        int maxDepth = 0;
        while (goOn) { //продолжаем, пока все вершинки не смотрят в корень
            goOn = false;
            first = mergeSort(file, 1);
            second = mergeSort(file, 0);
            try (BufferedReader firstRead = new BufferedReader(new FileReader(first))) {
                try (BufferedReader secondRead = new BufferedReader(new FileReader(second))) {
                    try (BufferedWriter fileWrite = new BufferedWriter(new FileWriter(file))) {
                        String str1 = firstRead.readLine();
                        String str2 = secondRead.readLine();
                        while (str1 != null && str2 != null) {
                            String[] strings1 = str1.split(" ");
                            String[] strings2 = str2.split(" ");
                            if (strings1[1].equals(strings2[0])) { //обновляем
                                fileWrite.write(strings1[0] + " " + strings2[1] + " "
                                + (parseInt(strings1[2]) + parseInt(strings2[2])) + "\n");
                                if (parseInt(strings1[0]) != root) {
                                    goOn = true;
                                }
                                if (parseInt(strings1[2]) + parseInt(strings2[2]) > maxDepth) {
                                    maxDepth = parseInt(strings1[2]) + parseInt(strings2[2]);
                                }
                                str2 = secondRead.readLine();
                            }  else {
                                str1 = firstRead.readLine();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Problems with counting max depth =^.^=");
                System.exit(1);
            }
        }
        return maxDepth;
    }

    void doSomeMagic() {
        File ifile = initFile();
        int res = countDepth(ifile) + 1;
        try (BufferedWriter write = new BufferedWriter(new FileWriter(fileOut))) {
            write.write(res + "\n");
        } catch (IOException e) {
            System.err.println("Some problems with magic =^.^=");
            System.exit(2);
        }
    }
}
