## Мысли о реализации интерпретатора

* Ему параметрами передаются входной/выходной потоки и поток ошибок.
* Он ничего сам не парсит, а дёргает парсер, передавая ему входной поток. Причем сам парсер передается интерпретатору в качестве аргумента конструктора
* Парсер возвращает объект интерфейса ``Command``

Нужно как-то корректно обрабатывать завершение работы.

После выполнения команды ``exit`` работа интерпретатора не всегда завершается. Поэтому, создаем класс исключения ``TerminateInterpreterException``, которое должно быть брошено командой, чтобы завершить работу интерпретатора.
Это неплохое решение по нескольким причинам:

* Команды всегда вызываются интерпретатором. Значит можно в их коде прописывать логику для завершения работы.
* При таком подходе команда может ничего не знать об интерпретаторе, парсере и т.д. Ей не нужно никак обращаться к интепретатору, чтобы уведомить его, что пора заканчивать работу. Она просто кидает исключение и всё ок.

Так как теперь для использования интерпретатора нужно предоставить ему свой парсер, то, чтобы можно было поставлять интепретатор как библиотеку, можно написать какой-нибудь более менее общий парсер для всех програм. Возможно, его можно по-разному конфигурировать в каких-то пределах при создании объекта.

## Желаемый Workflow с интепретатором
```java
MyParser myParser = new MyParser(...);
Interpreter shell = new Interpreter(myParser);
shell.run();
```

## Реализация команд в нашей задаче

У команд есть метод ``void execute()``, в котором они как-то дергают соответствующую базу данных за ее методы. Следовательно, каждая команда должна иметь поле ``private DbManager relatedDbManager`` или что-то в этом духе.
Соответственно, так как интерпретатор получает команды от парсера, наш класс ``DbCommandsParser`` должен  получать в аргументах объект класса ``DbManager``, с которым команды будут работать. Парсер будет прокидывать этот объект командам в конструктор.
Интепретатор дает возможность команде работать с выходным потоком и потоком ошибок, передавая их аргументами в метод ``void execute() throws TerminateInterpreterException`` у команды. Это позволяет командам гибко работать с выводом результатов и ошибок.

* Отделить ``TableManager`` от ``DbManager``. Класс ``DbManager`` имеет поле ``private TableManager currentTable`` и должен делегировать методы для работы с ``TableManager``.

* Команды для ``TableManager`` наследуются от своего абстрактного класса ``TableManagerCommand`` , а команды ``DbManager`` от своего ``DbManagerCommand``. Они оба имплементируют интерфейс ``InterpreterCommand``.
__________________
## Заметки по задаче 5 Storeable

Синтаксис команды ```put```: ```put <json-object>```. Например, ```put ["Dmitry", "Persiyanov", 20, "+7-999-999-99-99"]```.
Мне, как разработчику, придется сделать минимум изменений в классе этой команды. Для пользователя же это тоже неплохой формат ввода, который легко пишется и читается.
_______________
## Заметки по задаче 6 Parallel

Опишем общие идеи для реализации параллельного доступа к таблице. Для этого попробуем описать workflow девелопера с нашей библиотекой. Сделав это, можно будет думать, куда, что и как присобачить к имеющемуся коду.

1. Девелопер (далее юзер/пользователь) в своей программе хочет создать несколько потоков и из каждого совершать действия с нашей базой.
2. Изначально у него есть ```TableProvider```, полученный через фабрику.
3. Каждый поток делает ```getTable``` и получает сразу таблицу, с которой может работать как с обычной. То есть, если пользователь работает в одном потоке, то структура программы никак не меняется!

### Детали реализации

1. Новая семантика ```DbTable``` - пул, в котором содержится актуальная версия таблицы.
2. ```DbTable``` научится создавать для тредов объекты с диффами, с которыми уже будут работать треды. Назовём этот класс ```ThreadDbTable```.
2. Соответственно, ```ThreadDbTable``` содержит ```ThreadLocal<Map<String, String>>```. Таким образом, куче потоков соответствует ОДИН ```DbTable```, который хранит все актуальные данные, и через который происходят все коммиты с потоков.
3. Далее, от нас требуют сделать "честную" очередь для одновременно коммитящих в таблицу потоков. Это будет реализовываться с помощью fair policy у ```ReentrantReadWriteLock``` на пуле.
