package ru.fizteh.fivt.students.gampr.externaltreedepth;

import ru.fizteh.fivt.students.gampr.externallistrank.ListSort;

import java.io.*;

import static java.lang.Long.parseLong;

public class TreeDeep {
    private File fileIn;
    private File fileOut;
    private File dirTmp;
    private ListSort list;

    // Простенький конструктор
    public TreeDeep(File fileIn, File fileOut, File dirTmp) {
        this.fileIn = fileIn;
        this.fileOut = fileOut;
        this.dirTmp = dirTmp;
    }

    /*
        Одна вершина при разворачивании дерева может раздробиться
        Чтобы нумеровать эти части используем двойной индекс - номер сына, номер вершины
     */
    long makeIndex(long first, long second, long size) {
        return first * (size + 1) + second;
    }

    /*
        Создает новый файл
        Разворачиваем наше дерево в список
        Ребра, ведущие вниз будут иметь ранг "1", вверх - "-1"
        Найдем ранг всех вершин списка, минимальный(т. к. он отрицательный) будет глубиной дерева
     */
    public File initialization() throws IOException {
        File fileInit;

        // Инициализируем вспомогательный класс
        list = new ListSort(fileIn, fileOut, dirTmp);

        // Посчитаемкол-во строк во входном файле, чтобыосуществить двойную индексацию
        long size = list.countLines(fileIn);

        // Создаем новый файл
        try {
            fileInit = File.createTempFile("init", ".txt", dirTmp);
        } catch (IOException e) {
            throw new IOException("Can't create init file: " + e.getMessage());
        }

        File tmp;
        try {
            tmp = File.createTempFile("initTmp", ".txt", dirTmp);
        } catch (IOException e) {
            throw new IOException("Can't create init file: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileInit))) {
            // Сначала добавляем в наш список те ребра, которые действительно есть в нашем дереве
            long root = 0; // задетектируем корень
            long prev = -1;
            long prevTo = -1;

            File first = list.mergeSort(fileIn, 0);
            File second = list.mergeSort(fileIn, 1);

            try (BufferedReader reader = new BufferedReader(new FileReader(first))) {
                String string;
                while ((string = reader.readLine()) != null) {
                    // Изначально ранг всех ребер положим единице, ведь это ребра вниз

                    long now = parseLong(string.split(" ")[0]);
                    if (prev != now) {
                        if (!string.split(" ")[1].equals("0")) {
                            writer.write(string + " 1\n");
                        }
                        prev = now;
                        root ^= prev; // детектируем корень
                    } else if (!string.split(" ")[1].equals("0")) {
                        writer.write(makeIndex(prevTo, now, size) + " " + string.split(" ")[1] + " 1\n");
                    }
                    prevTo = parseLong(string.split(" ")[1]);
                    root ^= prevTo; // детектируем корень
                }
            } catch (IOException e) {
                throw new IOException("Can't create init file: " + e.getMessage());
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(first))) {
                try (BufferedWriter writerTmp = new BufferedWriter(new FileWriter(tmp))) {
                    String string;
                    prev = -1;
                    long max = -1;
                    while ((string = reader.readLine()) != null) {
                        long now = parseLong(string.split(" ")[0]);
                        if (prev != now) {
                            if (prev != -1) {
                                if (prev == root) {
                                    // Нужно кинуть ребро из корня в фиктивную вершину
                                    writer.write(makeIndex(max, root, size) + " 0 -1\n");
                                }
                                writerTmp.write(prev + " " + max + "\n");
                                max = -1;
                            }
                            prev = now;
                            if (max < parseLong(string.split(" ")[1])) {
                                max = parseLong(string.split(" ")[1]);
                            }
                        }
                    }
                    if (prev != -1) {
                        if (prev == root) {
                            // Нужно кинуть ребро из корня в фиктивную вершину
                            writer.write(makeIndex(max, root, size) + " 0 -1\n");
                        }
                        writerTmp.write(prev + " " + max + "\n");
                    }
                }
            }
            first = tmp;

            // Теперь можно добавить "обратные" ребра, наверх
            try (BufferedReader readerFirst = new BufferedReader(new FileReader(first))) {
                try (BufferedReader readerSecond = new BufferedReader(new FileReader(second))) {
                    String stringFirst = readerFirst.readLine();
                    String stringSecond = readerSecond.readLine();
                    long prevSecond = -1;
                    long prevToSecond = -1;
                    long prevFirst = -1;
                    long prevToFirst = -1;
                    while (stringFirst != null && stringSecond != null) {
                        // Изначально ранг всех ребер положим минус единице, ведь это ребра вверх
                        prevSecond = parseLong(stringSecond.split(" ")[0]);
                        prevToSecond = parseLong(stringSecond.split(" ")[1]);
                        prevFirst = parseLong(stringFirst.split(" ")[0]);
                        prevToFirst = parseLong(stringFirst.split(" ")[1]);
                        if (prevToSecond < prevFirst) {
                            stringSecond = readerSecond.readLine();
                        } else if (prevToSecond > prevFirst) {
                            stringFirst = readerFirst.readLine();
                        } else {
                            if (prevToFirst == 0) {
                                writer.write(prevToSecond + " " + makeIndex(prevToSecond, prevSecond, size) + " -1\n");
                            } else {
                                writer.write(makeIndex(prevToFirst, prevFirst, size)
                                        + " " + makeIndex(prevToSecond, prevSecond, size) + " -1\n");
                            }
                            stringFirst = readerFirst.readLine();
                            stringSecond = readerSecond.readLine();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Can't create init file: " + e.getMessage());
        }

        return fileInit;
    }

    /*
        Запустим вычисление глубины дерева
        Запишем нужную ниформацию в выходной файл
    */
    public void go() throws IOException {
        File fileInit = initialization();
        // Получаем файл, каждая строчка которого имеет вид
        // <номер_вершины> <номер_следующей_вершины> -<максимальное_расстояние_до_листа>
        File res = sort(fileInit);
        res = list.mergeSort(res, 2);

        // Выпишем ответ в том виде, в котором его хотят от нас
        try (BufferedReader reader = new BufferedReader(new FileReader(res))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut))) {
                String string = reader.readLine();
                String[] numbers = string.split(" ");
                writer.write(-parseLong(numbers[2]) + "\n");
            }
        } catch (IOException e) {
            throw new IOException("Can't write answer: " + e.getMessage());
        }
    }

    public File sort(File init) throws IOException {
        if (list.countLines(init) <= 1) {
            return init;
        }
        // Раскрашиваем элементы
        File colored = list.coloring(init);
        // Теперь нужно удалить некоторые элементы
        File sortFirst = list.mergeSort(colored, 0);
        File sortSecond = list.mergeSort(colored, 1);
        File next = list.join(sortSecond, sortFirst);
        // Удаляем файлы которые нам больше не понадобятся
        colored.delete();
        sortFirst.delete();
        sortSecond.delete();
        // Решаем рекурсивно, сводим задачу к меньшей
        next = sort(next);
        return list.reduce(init, next);
    }

}
