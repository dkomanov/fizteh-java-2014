package strings;

/**
 * Представляет интерфейс для создание экземпляров {@link TableProvider}. Предполагается, что реализация интерфейса
 * фабрики будет иметь публичный конструктор без параметров.
 *
 * @author Fedor Lavrentyev
 * @author Dmitriy Komanov
 */
public interface TableProviderFactory {

    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param dir Директория с файлами базы данных.
     * @return Объект для работы с базой данных.
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     */
    TableProvider create(String dir);
}
