package ru.fizteh.fivt.students.gampr.externallistrank;

import java.io.*;

import static java.lang.Long.parseLong;

public class ListSort {
    private File fileIn;
    private File fileOut;
    private File dirTmp;

    // Простенький конструктор
    public ListSort(File fileIn, File fileOut, File dirTmp) {
        this.fileIn = fileIn;
        this.fileOut = fileOut;
        this.dirTmp = dirTmp;
    }

    /*
        Считает кол-во строк в файле
    */
    public long countLines(File file) throws IOException {
        long count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            throw new IOException("Can't count lines: " + e.getMessage());
        }
        return count;
    }

    /*
        Для mergeSort соединить оба файла
     */
    private File merge(File first, File second, int index) throws IOException {
        File result;

        // Создаем файл для результата
        try {
            result = File.createTempFile("mergeResult", ".txt", dirTmp);
        } catch (IOException e) {
            throw new IOException("Can't create mergeResult file: " + e.getMessage());
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
                        if ((parseLong(strFirst.split(" ")[index]) <= parseLong(strSecond.split(" ")[index]))) {
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
            throw new IOException("Can't merge files: " + e.getMessage());
        }

        return result;
    }

    /*
        Сортировка файла, index задает номер числа, по которому проходит сортировка
     */
    public File mergeSort(File file, int index) throws IOException {
        long count = countLines(file);
        if (count <= 1) {
            return file;
        }
        File first;
        File second;

        // Создаем вспомогательные файлы
        try {
            first = File.createTempFile("mergeFirst", ".txt", dirTmp);
            second = File.createTempFile("mergeSecond", ".txt", dirTmp);
        } catch (IOException e) {
            throw new IOException("Can't create merge file: " + e.getMessage());
        }

        //Разбиваем файл на два
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            try (BufferedWriter writerFirst = new BufferedWriter(new FileWriter(first))) {
                try (BufferedWriter writerSecond = new BufferedWriter(new FileWriter(second))) {
                    for (long iLine = 0; iLine < count; iLine++) {
                        String s = reader.readLine();
                        if (iLine < (count / 2)) {
                            writerFirst.write(s + "\n");
                        } else {
                            writerSecond.write(s + "\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Can't fill mergeFirst and mergeSecond files: " + e.getMessage());
        }

        // Сортируем каждый по отдельности и соединяем
        File newFirst = mergeSort(first, index);
        File newSecond = mergeSort(second, index);
        File result = merge(newFirst, newSecond, index);

        // Удаляем за собой
        first.delete();
        second.delete();
        newFirst.delete();
        newSecond.delete();

        return result;
    }

    /*
        Создает новый файл
        Теперь помимо хранения информации о списке будем хранить так называемый ранг элемента
        <ранг элемента> = -<расстояние от данного элемента списка до конца списка>
        Изначально ранг всех элементов положим единице
        В конце алгоритма мы получим правильные значения рангов всех элементов
     */
    public File initialization() throws IOException {
        File fileInit;

        // Создаем новый файл
        try {
            fileInit = File.createTempFile("init", ".txt", dirTmp);
        } catch (IOException e) {
            throw new IOException("Can't create init file: " + e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileIn))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileInit))) {
                String string;
                while ((string = reader.readLine()) != null) {
                    // Изначально ранг всех элементов положим единице
                    writer.write(string + " -1\n");
                }
            }
        } catch (IOException e) {
            throw new IOException("Can't create init file: " + e.getMessage());
        }

        return fileInit;
    }

    /*
        Запустим сортировку списка
        Запишем нужную ниформацию в выходной файл
    */
    public void go() throws IOException {
        File fileInit = initialization();
        // Получаем файл, каждая строчка которого имеет вид
        // <номер_вершины> <номер_следующей_вершины> -<расстояние_до_конца>
        File res = sort(fileInit);
        res = mergeSort(res, 2);

        // Выпишем ответ в том виде, в котором его хотят от нас
        try (BufferedReader reader = new BufferedReader(new FileReader(res))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut))) {
                String string;
                while ((string = reader.readLine()) != null) {
                    String[] numbers = string.split(" ");
                    writer.write(numbers[0] + " " + numbers[1] + "\n");
                }
            }
        } catch (IOException e) {
            throw new IOException("Can't write answer: " + e.getMessage());
        }
    }

    /*
        Красит вершины списка в цвета, для дальнейшего удаления тех, которые раскрашены в 1, а их предшественник в 0
        Каждый строчка результирующего файла имеет вид
        <номер_вершины> <номер_следующей_вершины> -<расстояние_до_конца> <цвет_вершины>
     */
    public File coloring(File file) throws IOException {
        File colored;

        // Создаем файл для хранения результата
        try {
            colored = File.createTempFile("colored", ".txt", dirTmp);
        } catch (IOException e) {
            throw new IOException("Can't create colored file: " + e.getMessage());
        }

        // Случайно красим вершины
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(colored))) {
                String string;
                while ((string = reader.readLine()) != null) {
                    writer.write(string + " " + (long) (Math.random() * 2) + "\n");
                }
            }
        } catch (IOException e) {
            throw new IOException("Can't create colored file: " + e.getMessage());
        }

        return colored;
    }

    /*
        Удаляем вершины, которые раскрашены в 1, а их предшественники в 0
        Объединяем соседние для этих вершин элементы, поддерживая целостность списка и адекватность значений рангов
        Значение цвета удаляем
     */
    public File join(File first, File second) throws IOException {
        File result;

        // Создаем вспомогательный файл
        try {
            result = File.createTempFile("join", ".txt", dirTmp);
        } catch (IOException e) {
            throw new IOException("Can't create join file: " + e.getMessage());
        }

        try (BufferedReader readerFirst = new BufferedReader(new FileReader(first))) {
            try (BufferedReader readerSecond = new BufferedReader(new FileReader(second))) {
                try (BufferedWriter writerResult = new BufferedWriter(new FileWriter(result))) {
                    // Пропустим первую строчку, где 0 является следующей вершиной
                    readerFirst.readLine();
                    String strFirst = readerFirst.readLine();
                    String strSecond = readerSecond.readLine();
                    while (strFirst != null && strSecond != null) {
                        String[] stringsFirst = strFirst.split(" ");
                        String[] stringsSecond = strSecond.split(" ");
                        // Нужно, чтобы строчки из файлов согласовывались друг с другом
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
                            // Ранг меняется
                            writerResult.write((parseLong(stringsFirst[2]) + parseLong(stringsSecond[2])) + "\n");
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
            throw new IOException("Can't join files: " + e.getMessage());
        }

        return result;
    }

    /*
        Обновляем значения рангов элементов, записанных в файле first
        Для этого используем значения рангов из файла second(там они уже посчитаны)
     */
    public File reduce(File first, File second) throws IOException {
        File result;
        File tmp;

        // Создаем вспомогательные файлы
        try {
            result = File.createTempFile("reduce", ".txt", dirTmp);
            tmp = File.createTempFile("reduce_tmp", ".txt", dirTmp);
        } catch (IOException e) {
            throw new IOException("Can't create reduce file: " + e.getMessage());
        }

        first = mergeSort(first, 0);
        second = mergeSort(second, 0);

        // Для начала обновим значения рангов для элементов, чей ранг уже был посчитан
        try (BufferedWriter writerResult = new BufferedWriter(new FileWriter(result))) {
            try (BufferedReader readerFirst = new BufferedReader(new FileReader(first))) {
                try (BufferedReader readerSecond = new BufferedReader(new FileReader(second))) {
                    try (BufferedWriter writerTmp = new BufferedWriter(new FileWriter(tmp))) {
                        String strFirst = readerFirst.readLine();
                        String strSecond = readerSecond.readLine();
                        while (strFirst != null && strSecond != null) {
                            String[] stringsFirst = strFirst.split(" ");
                            String[] stringsSecond = strSecond.split(" ");
                            if (!stringsFirst[0].equals(stringsSecond[0])) {
                                // Для остальных элементов ранг посчитаем попозже
                                writerTmp.write(strFirst + "\n");
                                strFirst = readerFirst.readLine();
                            } else {
                                writerResult.write(stringsFirst[0] + " ");
                                writerResult.write(stringsFirst[1] + " ");
                                // Просто записываем уже посчитанный ранг
                                writerResult.write(stringsSecond[2] + "\n");

                                strFirst = readerFirst.readLine();
                                strSecond = readerSecond.readLine();
                            }
                        }
                        // Осташиеся строчки первого файла тоже надо будет обновить
                        while (strFirst != null) {
                            writerTmp.write(strFirst + "\n");
                            strFirst = readerFirst.readLine();
                        }
                    }
                }
            }

            tmp = mergeSort(tmp, 1);

            // Обновляем ранг для оставшихся элементов
            try (BufferedReader readerTmp = new BufferedReader(new FileReader(tmp))) {
                try (BufferedReader readerSecond = new BufferedReader(new FileReader(second))) {
                    String strTmp = readerTmp.readLine();
                    String strSecond = readerSecond.readLine();
                    while (strTmp != null && strSecond != null) {
                        String[] stringsTmp = strTmp.split(" ");
                        String[] stringsSecond = strSecond.split(" ");
                        if (parseLong(stringsTmp[1]) < parseLong(stringsSecond[0])) {
                            // Для этого элемента ранг уже посчитан верно
                            writerResult.write(strTmp + "\n");
                            strTmp = readerTmp.readLine();
                        } else
                        if (parseLong(stringsTmp[1]) > parseLong(stringsSecond[0])) {
                            strSecond = readerSecond.readLine();
                        } else {
                            writerResult.write(stringsTmp[0] + " ");
                            writerResult.write(stringsTmp[1] + " ");
                            // Обновляем ранг
                            writerResult.write((parseLong(stringsTmp[2]) + parseLong(stringsSecond[2])) + "\n");

                            strTmp = readerTmp.readLine();
                            strSecond = readerSecond.readLine();
                        }
                    }
                    // Не забываем про остальные элементы, если они есть
                    while (strTmp != null) {
                        writerResult.write(strTmp + "\n");
                        strTmp = readerTmp.readLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Can't reduce files: " + e.getMessage());
        }

        // Удаляем временный файл за собой
        tmp.delete();
        return result;
    }

    public File sort(File init) throws IOException {
        if (countLines(init) <= 1) {
            return init;
        }
        // Раскрашиваем элементы
        File colored = coloring(init);
        // Теперь нужно удалить некоторые элементы
        File sortFirst = mergeSort(colored, 0);
        File sortSecond = mergeSort(colored, 1);
        File next = join(sortSecond, sortFirst);
        // Удаляем файлы которые нам больше не понадобятся
        colored.delete();
        sortFirst.delete();
        sortSecond.delete();
        // Решаем рекурсивно, сводим задачу к меньшей
        next = sort(next);
        return reduce(init, next);
    }

}
