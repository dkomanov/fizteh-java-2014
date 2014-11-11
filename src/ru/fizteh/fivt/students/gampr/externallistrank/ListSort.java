package ru.fizteh.fivt.students.gampr.externallistrank;

import java.io.*;

import static java.lang.Integer.parseInt;

public class ListSort {
    private File fileIn;
    private File fileOut;
    private File dirTmp;

    ListSort(File fileIn, File fileOut, File dirTmp) {
        this.fileOut = fileOut;
        this.dirTmp = dirTmp;
        this.fileIn = fileIn;
    }

    /*
        Считает кол-во строк в файле
    */
    int countLines(File file) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            System.err.println("Runtime error");
            System.exit(1);
        }
        return count;
    }

    /*
        Для mergeSort соединить оба файла
     */
    File merge(File first, File second, int index) {
        File result = null;
        try {
            result = File.createTempFile("mergeResult", ".txt", dirTmp);
        } catch (IOException e) {
            System.err.println("Can't create result file");
            System.exit(1);
        }

        try (BufferedReader readerFirst = new BufferedReader(new FileReader(first))) {
            try (BufferedReader readerSecond = new BufferedReader(new FileReader(second))) {
                try (BufferedWriter writerResult = new BufferedWriter(new FileWriter(result))) {
                    String strFirst = readerFirst.readLine();
                    String strSecond = readerSecond.readLine();
                    while (strFirst != null || strSecond != null) {
                        if (strSecond == null) {
                            writerResult.write(strFirst + "\n");
                            strFirst = readerFirst.readLine();
                        } else if (strFirst == null) {
                            writerResult.write(strSecond + "\n");
                            strSecond = readerSecond.readLine();
                        } else
                        if ((parseInt(strFirst.split(" ")[index]) < parseInt(strSecond.split(" ")[index]))) {
                            writerResult.write(strFirst + "\n");
                            strFirst = readerFirst.readLine();
                        } else {
                            writerResult.write(strSecond + "\n");
                            strSecond = readerSecond.readLine();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't merge files");
            System.exit(1);
        }
        return result;
    }

    /*
        Сортировка файла, index задает номер числа, по которому проходить сортировка
     */
    File mergeSort(File file, int index) {
        int count = countLines(file);
        if (count == 1) {
            return file;
        }
        File first = null;
        File second = null;

        try {
            first = File.createTempFile("mergeFirst", ".txt", dirTmp);
            second = File.createTempFile("mergeSecond", ".txt", dirTmp);
        } catch (IOException e) {
            System.err.println("Can't create merge file");
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            try (BufferedWriter writerFirst = new BufferedWriter(new FileWriter(first))) {
                try (BufferedWriter writerSecond = new BufferedWriter(new FileWriter(second))) {
                    for (int i = 0; i < count; i++) {
                        String s = reader.readLine();
                        if (i < (count / 2)) {
                            writerFirst.write(s + "\n");
                        } else {
                            writerSecond.write(s + "\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't merge files");
            System.exit(1);
        }

        File newFirst = mergeSort(first, index);
        File newSecond = mergeSort(second, index);
        File result = merge(newFirst, newSecond, index);
        first.delete();
        second.delete();
        newFirst.delete();
        newSecond.delete();
        return result;
    }

    File initialization() {
        File fileInit = null;
        try {
            fileInit = File.createTempFile("init", ".txt", dirTmp);
        } catch (IOException e) {
            System.err.println("Can't create init file");
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileIn))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileInit))) {
                String string;
                while ((string = reader.readLine()) != null) {
                    writer.write(string + " 1\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Can't create init file");
            System.exit(2);
        }

        return fileInit;
    }

    void go() {
        File fileInit = initialization();
        File res = sort(fileInit);
        /*
            Получаем файл, каждый строчка имеет вид
            <номер_вершины> <номер_следующей_вершины> <расстояние_до_конца>
        */
        res = mergeSort(res, 2);

        try (BufferedReader reader = new BufferedReader(new FileReader(res))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut))) {
                String string;
                while ((string = reader.readLine()) != null) {
                    String[] numbers = string.split(" ");
                    writer.write(numbers[0] + " " + numbers[1] + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Can't create init file");
            System.exit(2);
        }
    }

    /*
        Красит вершины списка в цвета, для дальнейшего удаления тех, которые раскрашены в 1, а их предшественник в 0
     */
    File coloring(File file) {
        File colored = null;
        try {
            colored = File.createTempFile("colored", ".txt", dirTmp);
        } catch (IOException e) {
            System.err.println("Can't create colored file");
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(colored))) {
                String string;
                while ((string = reader.readLine()) != null) {
                    writer.write(string + " " + (int) (Math.random() * 2) + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Can't create init file");
            System.exit(2);
        }

        return colored;
    }

    File join(File first, File second) {
        File result = null;
        try {
            result = File.createTempFile("join", ".txt", dirTmp);
        } catch (IOException e) {
            System.err.println("Can't create join file");
            System.exit(1);
        }

        try (BufferedReader readerFirst = new BufferedReader(new FileReader(first))) {
            try (BufferedReader readerSecond = new BufferedReader(new FileReader(second))) {
                try (BufferedWriter writerResult = new BufferedWriter(new FileWriter(result))) {
                    readerFirst.readLine(); // Пропустим первую строчку, где 0 является следующей вершиной
                    for (String strFirst = readerFirst.readLine(), strSecond = readerSecond.readLine();
                         strFirst != null && strSecond != null; ) {
                        String[] stringsFirst = strFirst.split(" ");
                        String[] stringsSecond = strSecond.split(" ");
                        if (!stringsFirst[1].equals(stringsSecond[0])) {
                            strSecond = readerSecond.readLine();
                            stringsSecond = strSecond.split(" ");
                        }
                        if (stringsFirst[3].equals("0") && stringsSecond[3].equals("0")) {
                            // Сохраняем первый элемент, цвет удаляем
                            writerResult.write(stringsFirst[0] + " ");
                            writerResult.write(stringsFirst[1] + " ");
                            writerResult.write(stringsFirst[2] + "\n");
                            // Если надо, то и второй элемент тоже сохраняем
                            if (stringsSecond[1].equals("0")) {
                                // Сохраняем второй элемент, цвет удаляем
                                writerResult.write(stringsSecond[0] + " ");
                                writerResult.write(stringsSecond[1] + " ");
                                writerResult.write(stringsSecond[2] + "\n");
                            }
                        } else if (stringsFirst[3].equals("1") && stringsSecond[3].equals("1")) {
                            // Сохраняем второй элемент, цвет удаляем
                            writerResult.write(stringsSecond[0] + " ");
                            writerResult.write(stringsSecond[1] + " ");
                            writerResult.write(stringsSecond[2] + "\n");
                        } else if (stringsFirst[3].equals("0") && stringsSecond[3].equals("1")) {
                            // Надо соединить элементы
                            writerResult.write(stringsFirst[0] + " ");
                            writerResult.write(stringsSecond[1] + " ");
                            writerResult.write((parseInt(stringsFirst[2]) + parseInt(stringsSecond[2])) + "\n");
                        } else if (stringsSecond[1].equals("0")) {
                            // Сохраняем второй элемент, цвет удаляем
                            writerResult.write(stringsSecond[0] + " ");
                            writerResult.write(stringsSecond[1] + " ");
                            writerResult.write(stringsSecond[2] + "\n");
                        }
                        strFirst = readerFirst.readLine();
                        strSecond = readerSecond.readLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't merge files!");
            System.exit(1);
        }

        return result;
    }

    File reduce(File first, File second) {
        File result = null;
        File tmp = null;
        try {
            result = File.createTempFile("reduce", ".txt", dirTmp);
            tmp = File.createTempFile("tmp", ".txt", dirTmp);
        } catch (IOException e) {
            System.err.println("Can't create reduce file");
            System.exit(1);
        }

        first = mergeSort(first, 0);
        second = mergeSort(second, 0);

        try (BufferedWriter writerResult = new BufferedWriter(new FileWriter(result))) {
            try (BufferedReader readerFirst = new BufferedReader(new FileReader(first))) {
                try (BufferedReader readerSecond = new BufferedReader(new FileReader(second))) {
                    try (BufferedWriter writerTmp = new BufferedWriter(new FileWriter(tmp))) {
                        for (String strFirst = readerFirst.readLine(), strSecond = readerSecond.readLine();
                             strFirst != null && strSecond != null; ) {
                            String[] stringsFirst = strFirst.split(" ");
                            String[] stringsSecond = strSecond.split(" ");
                            if (!stringsFirst[0].equals(stringsSecond[0])) {
                                writerTmp.write(strFirst + "\n");
                                strFirst = readerFirst.readLine();
                                stringsFirst = strFirst.split(" ");
                            }
                            writerResult.write(stringsFirst[0] + " ");
                            writerResult.write(stringsFirst[1] + " ");
                            writerResult.write(stringsSecond[2] + "\n");

                            strFirst = readerFirst.readLine();
                            strSecond = readerSecond.readLine();
                        }
                    }
                }
            }

            first = mergeSort(first, 1);

            try (BufferedReader readerTmp = new BufferedReader(new FileReader(tmp))) {
                try (BufferedReader readerSecond = new BufferedReader(new FileReader(second))) {
                    String strTmp = readerTmp.readLine();
                    String strSecond;
                    for (strTmp = readerTmp.readLine(), strSecond = readerSecond.readLine();
                         strTmp != null && strSecond != null; ) {
                        String[] stringsFirst = strTmp.split(" ");
                        String[] stringsSecond = strSecond.split(" ");
                        if (!stringsFirst[1].equals(stringsSecond[0])) {
                            strSecond = readerSecond.readLine();
                            stringsFirst = strSecond.split(" ");
                        }
                        writerResult.write(stringsFirst[0] + " ");
                        writerResult.write(stringsFirst[1] + " ");
                        writerResult.write((parseInt(stringsFirst[2]) + parseInt(stringsSecond[2])) + "\n");

                        strTmp = readerTmp.readLine();
                        strSecond = readerSecond.readLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't merge files!");
            System.exit(1);
        }

        return result;
    }

    File sort(File init) {
        System.out.println(countLines(init));
        if (countLines(init) <= 1) {
            return init;
        }
        File colored = coloring(init);
        File sortFirst = mergeSort(colored, 0);
        File sortSecond = mergeSort(colored, 1);
        File next = join(sortSecond, sortFirst);
        sort(next);
        colored.delete();
        return reduce(init, next);
    }

}
