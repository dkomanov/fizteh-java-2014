package ru.fizteh.fivt.students.gampr.externaltreedepth;

import ru.fizteh.fivt.students.gampr.externallistrank.ListSort;

import java.io.*;

import static java.lang.Integer.parseInt;

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
        this.list = new ListSort(fileIn, fileOut, dirTmp);
    }

    /*
        Создает новый файл
        Разворачиваем наше дерево в список
        Ребра, ведущие вниз будут иметь ранг "1", вверх - "-1"
        Найдем ранг всех вершин списка, минимальный(т. к. он отрицательный) будет глубиной дерева
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
                    // Изначально ранг всех ребер положим единице
                    writer.write(string + " -1\n");
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
                writer.write(-parseInt(numbers[2]) + "\n");
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
