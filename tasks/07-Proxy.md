## Proxy

Логирование интерфейсов базы данных.

Необходимо написать реализацию интерфейса [LoggingProxyFactory](../src/ru/fizteh/fivt/proxy/LoggingProxyFactory.java).
Реализация интерфейса должна иметь публичный конструктор без параметров.

Необходимо, чтобы реализации интерфейсов Table, TableProvider и TableProviderFactory реализовали интерфейс
```AutoCloseable```. При вызове метода close для интерфейса Table должен выполняться rollback, вызов любого
метода должен приводить к IllegalStateException, TableProvider при этом должен вернуть новый экземпляр класса
Table в методе getTable. При вызове метода close для интерфейса TableProvider должны вызываться close у всех
таблиц, полученных через TableProvider, после вызова все методы должны выбрасывать IllegalStateException.
Аналогичным образом должно происходить с TableProviderFactory.

Логирование всех значений аргументов-возвращаемых значений должно делаться с помощью метода ```toString()```,
за исключением объектов, реализующих Iterable, для них для каждого варианта свой вариант логирования.

Необходимо реализовать toString для реализаций интерфейсов: Table, TableProvider и Storeable.

### Table.toString
Выводит строку ```Table[absolute-path-to-dir]```, где Table - это название класса-реализации
(```getClass().getSimpleName()```), а ```absolute-path-to-dir``` - абсолютный путь до директории с файлами БД.

Например, ```MyTable[/home/student/db/table]```

### TableProvider.toString
Выводит строку ```TableProvider[absolute-path-to-dir]```, где TableProvider - это название класса-реализации
(```getClass().getSimpleName()```), а ```absolute-path-to-dir``` - абсолютный путь до директории с базами данных.

Например, ```MyTableProvider[/home/student/db]```

### Storeable.toString
Выводит строку ```Storeable[values]```, где Storeable - это название класса-реализации
(```getClass().getSimpleName()```), а ```values``` - значения, записанные через запятую. Значение-null записывается
как пустая строка.

Например, ```MyStoreable[1,2,,string]```

### Общий формат логов

Логи записываются в формате, соответствующем варианту (это противоположный формат задания Storeable). Каждый вызов
метода - это строго одна строка в логе.

#### Вариант 1. JSON

Формат лога на примере вызова метода ```TableProvider.createTable```:
```
{
    "timestamp": 12345,
    "class": "ru.fizteh.fivt.students.pupkin.db.TableProviderImpl",
    "method": "createTable",
    "arguments": [
        "name",
        ["java.lang.Integer","java.lang.String"]
    ],
    "returnValue": "TableImpl[/home/student/db/name]"
}
```

Формат лога на примере вызова метода ```TableProvider.getTable```:
```
{
    "timestamp": 12345,
    "class": "ru.fizteh.fivt.students.pupkin.db.TableProviderImpl",
    "method": "getTable",
    "arguments": [ null ],
    "thrown": "java.lang.IllegalArgumentException: name is null"
}
```

Формат лога на примере вызова метода ```Table.size```:
```
{
    "timestamp": 12345,
    "class": "ru.fizteh.fivt.students.pupkin.db.TableImpl",
    "method": "size",
    "arguments": [],
    "returnValue": 1
}
```

Т.е. примитивные типы должны быть представлены в результирующем JSON как соответствующие типы.

Значение timestamp берётся из метода ```System.currentTimeMillis()```.

#### Вариант 2. XML

Формат лога на примере вызова метода ```TableProvider.createTable```:
```
<invoke timestamp="12345" class="ru.fizteh.fivt.students.pupkin.db.TableProviderImpl" name="createTable">
    <arguments>
        <argument>name</argument>
        <argument>
            <list>
                <value>java.lang.Integer</value>
                <value>java.lang.String</value>
            </list>
        </argument>
    </arguments>
    <return>TableImpl[/home/student/db/name]</return>
</invoke>
```

Формат лога на примере вызова метода ```TableProvider.getTable```:
```
<invoke timestamp="12345" class="ru.fizteh.fivt.students.pupkin.db.TableProviderImpl" name="getTable">
    <arguments>
        <argument><null/></argument>
    </arguments>
    <thrown>java.lang.IllegalArgumentException: name is null</thrown>
</invoke>
```

Формат лога на примере вызова метода ```Table.size```:
```
<invoke timestamp="12345" class="ru.fizteh.fivt.students.pupkin.db.TableImpl" name="size">
    <arguments/>
    <return>1</return>
</invoke>
```

Значение timestamp берётся из метода ```System.currentTimeMillis()```.

### Дополнительно
* LoggingProxyFactory может работать не только с классами-интерфейсами БД.
* Нужно написать юнит-тест. Для него надо написать интерфейс и реализацию. Протестировать
все варианты логирования (исключения, переносы строк, примитивные типы, массивы и списки).
Для тестов удобно использовать StringWriter.
* Протестировать работу close. Вызывать close в старых тестах.
* Если тип возвращаемого значения метода - void, то просто не надо выводить сооветствующий
элемент в XML (return) или поле в JSON (returnValue).
* Не надо проксировать вызовы методов, которые определены в Object. Как минимум это необходимо,
чтобы не поломать симметричность поведения equals/hashCode.
* Если проксируемый метод выбрасывает исключение, то Method.invoke оборачивает его в исключение
InvocationTargetException. Пробрасывать дальше надо targetException, а не обёртку.
* Proxy не имеет права выбрасывать "свои" исключения. При ошибке записи в Writer исключение
должно быть проглочено.
* Список (Iterable) может содержать в себе другой список. Надо логировать его рекурсивно.
Нельзя забывать о возможности циклических ссылок. На месте циклической ссылки необходимо выводить
'cyclic'. Для проверки на циклы можно использовать IdentityHashMap.
