package ru.fizteh.fivt.students.annafedyushkina.ListRank;

import java.io.*;
import static java.lang.Integer.parseInt;

public class Ranking {
    private File file_in;
    private File file_out;
    private File directory;

    Ranking(File fin, File fout, File dir) { //конструктор
        this.file_in = fin;
        this.file_out = fout;
        this.directory = dir;
    }

    File init_file() { // припишем в конце каждой строки 1 - потом мы хотим сделать так,
        // чтобы это число стало расстоянием до последнего элемента
        File ifile = null;
        try {
            ifile = File.createTempFile("init_file", ".txt", directory);
        } catch (IOException e) {
            System.err.println("Some problems with file creation =^.^=");
            System.exit(1);
        }
        try (BufferedReader read = new BufferedReader(new FileReader(file_in))) {
            try (BufferedWriter write = new BufferedWriter(new FileWriter(ifile))) {
                String str;
                while ((str = read.readLine()) != null) {
                    write.write(str + " -1\n");
                }
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
            System.err.println("Runtime error");
            System.exit(1);
        }
        return number;
    }

    File randColor(File file) { //покрасим вершины и если будет ребро из 0 в 1, то мы удалим 1
        File withColor = null;
        try {
            withColor = File.createTempFile("randColor", ".txt", directory);
        } catch (IOException e) {
            System.err.println("Some problems with file creation =^.^=");
            System.exit(1);
        }
        try (BufferedReader read = new BufferedReader(new FileReader(file))) {
            try (BufferedWriter write = new BufferedWriter(new FileWriter(withColor))) {
                String str;
                while ((str = read.readLine()) != null) {
                    write.write(str + " " + (int) (Math.random() * 2) + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Some problems with file creation =^.^=");
            System.exit(2);
        }
        return withColor;
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
                    int first_len = numberOfLines(first);
                    int second_len = numberOfLines(second);
                    int num1 = 0;
                    int num2 = 0;
                    String str1 = firstRead.readLine();
                    String str2 = secondRead.readLine();
                    while (num1 != first_len || num2 != second_len) {
                        if (num1 == first_len) {
                            str1 = firstRead.readLine();
                            resWrite.write(str1 + "\n");
                            num1 ++;
                        } else if (num2 == second_len) {
                            str2 = secondRead.readLine();
                            resWrite.write(str2 + "\n");
                            num2++;
                        } else if ((parseInt(str1.split(" ")[ind]) < parseInt(str2.split(" ")[ind]))) {
                            resWrite.write(str1 + "\n");
                            str1 = firstRead.readLine();
                            num1++;
                        } else {
                            resWrite.write(str2 + "\n");
                            str2 = secondRead.readLine();
                            num2++;
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
                    int i;
                    for (i = 0; i < lines / 2; i++) {
                        String s = fileRead.readLine();
                        firstWrite.write(s + "\n");
                    }
                    for (; i < lines / 2; i++) {
                        String s = fileRead.readLine();
                        secondWrite.write(s + "\n");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't merge files");
            System.exit(1);
        }
        File first1 = mergeSort(first, ind);
        File second1 = mergeSort(second, ind);
        File result = merge(first1, second1, ind);
        return result;
    }

    File sort(File init) { //сортирует наш список как надо
        System.out.println(numberOfLines(init));
        if (numberOfLines(init) <= 1) {
            return init;
        }
        File colored = randColor(init);
        File file1 = mergeSort(colored, 0);
        File file2 = mergeSort(colored, 1);
        File next = connect(file2, file1);
        sort(next);
        next = sort(next);
        return update_ranks(init, next);
    }

    File update_ranks(File first, File second) { // в файле first обновляет ранги тех, что в second
        File res = null;
        File temp = null;
        try {
            res = File.createTempFile("upd_res", ".txt", directory);
            temp = File.createTempFile("temporary", ".txt", directory);
        } catch (IOException e) {
            System.err.println("Some problems with file creation =^.^=");
            System.exit(1);
        }
        first = mergeSort(first, 0);
        second = mergeSort(second, 0);
        try (BufferedWriter resWrite = new BufferedWriter(new FileWriter(res))) {
            try (BufferedReader firstRead = new BufferedReader(new FileReader(first))) {
                try (BufferedReader secondRead = new BufferedReader(new FileReader(second))) {
                    try (BufferedWriter tempWrite = new BufferedWriter(new FileWriter(temp))) {
                        String str1 = firstRead.readLine();
                        String str2 = secondRead.readLine();
                        while (str1 != null && str2 != null) {
                            String[] strings1 = str1.split(" ");
                            String[] strings2 = str1.split(" ");
                            if (!strings1[0].equals(strings2[0])) {
                                tempWrite.write(str1 + "\n");
                                str1 = firstRead.readLine();
                            } else {
                                resWrite.write(strings1[0] + " " + strings1[1] + " " + strings2[2] + "\n");
                                str1 = firstRead.readLine();
                                str2 = secondRead.readLine();
                            }
                        }
                        while (str1 != null) {
                            tempWrite.write(str1 + "\n");
                            str1 = firstRead.readLine();
                        }
                    }
                }
            }
            temp = mergeSort(temp, 1);
            try (BufferedReader tempRead = new BufferedReader(new FileReader(temp))) {
                try (BufferedReader secondRead = new BufferedReader(new FileReader(second))) {
                    String str0 = tempRead.readLine();
                    String str2 = secondRead.readLine();
                    while (str0 != null && str2 != null) {
                        String[] strings0 = str0.split(" ");
                        String[] strings2 = str2.split(" ");
                        if (parseInt(strings0[1]) < parseInt(strings2[0])) {
                            resWrite.write(str0 + "\n");
                            str0 = tempRead.readLine();
                        } else if (parseInt(strings0[1]) > parseInt(strings2[0])) {
                            str2 = secondRead.readLine();
                        } else {
                            resWrite.write(strings0[0] + " " + strings0[1] + " " + (parseInt(strings0[2]) + parseInt(strings2[2])) + "\n");
                            str0 = tempRead.readLine();
                            str2 = secondRead.readLine();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Some problems with updating ranks =^.^=");
            System.exit(1);
        }
        return res;
    }

    File connect(File first, File second) {
        File res = null;
        try {
            res = File.createTempFile("connect", ".txt", directory);
        } catch (IOException e) {
            System.err.println("Some problems with file creation =^.^=");
            System.exit(1);
        }
        try (BufferedReader firstRead = new BufferedReader(new FileReader(first))) {
            try (BufferedReader secondRead = new BufferedReader(new FileReader(second))) {
                try (BufferedWriter resWrite = new BufferedWriter(new FileWriter(res))) {
                    firstRead.readLine();
                    String str1 = firstRead.readLine();
                    String str2 = secondRead.readLine();
                    while (str1 != null && str2 != null) {
                        String[] strings1 = str1.split(" ");
                        String[] strings2 = str2.split(" ");
                        if (!strings1[1].equals(strings2[0])) {
                            str2 = secondRead.readLine();
                            strings2 = str2.split(" ");
                        }
                        if (strings1[3].equals("0") && strings2[3].equals("0")) {
                            resWrite.write(strings1[0] + " " + strings1[1] + " " + strings1[2] + "\n");
                            if (strings2[1].equals("0")) {
                                resWrite.write(strings2[0] + " " + strings2[1] + " " + strings2[2] + "\n");
                            }
                        } else if (strings1[3].equals("1") && strings2[3].equals("1")) {
                            resWrite.write(strings2[0] + " " + strings2[1] + " " + strings2[2] + "\n");
                        } else if (strings1[3].equals("0") && strings2[3].equals("1")) {
                            resWrite.write(strings1[0] + " " + strings2[1] + " " + (parseInt(strings1[2]) + parseInt(strings2[2])) + "\n");
                        } else if (strings2[1].equals("0")) {
                            resWrite.write(strings2[0] + " " + strings2[1] + " " + strings2[2] + "\n");
                        }
                        str1 = firstRead.readLine();
                        str2 = secondRead.readLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Problems with connecting =^.^=");
            System.exit(1);
        }
        return res;
    }

    void do_some_magic() { // основная функция. сортирует и выводит файл
        File ifile = init_file();
        File res = sort(ifile);
        res = mergeSort(res, 2);
        try (BufferedReader read = new BufferedReader(new FileReader(res))) {
            try (BufferedWriter write = new BufferedWriter(new FileWriter(file_out))) {
                String str;
                while ((str = read.readLine()) != null) {
                    String[] edges = str.split(" ");
                    write.write(edges[0] + " " + edges[1] + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Some problems with magic =^.^=");
            System.exit(2);
        }
    }
}