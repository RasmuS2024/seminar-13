# Выполнение задания "Разработка нагрузочных тестов"

При разработке тестов использовалась программа Apache JMeter 5.6.3.

## Реализация

Тесты реализованы в программе JMeter.
В тестах применялось:
- количество пользователей:     1000;
- время разгона пользователей:  10 сек;
- длительность тестов:          70 сек.

Файл теста [Test_Satellite_service.jmx](jmeter-tests/tests/Test_Satellite_service.jmx)

Сформирован [HTML-отчет](jmeter-tests/report/index.html)

![Скриншот дашборда](jmeter-tests/images/Dashboard.png)
![Скриншот дашборда - статистика тестов](jmeter-tests/images/Dashboard-2.png)

Разные графы
![Aggregate Graph.png](jmeter-tests/images/Aggregate%20Graph.png)
![Graph Results.png](jmeter-tests/images/Graph%20Results.png)
![Response Time Graph.png](jmeter-tests/images/Response%20Time%20Graph.png)


### Пользовательский сценарий нагрузочного теста
1. запрашивается get_overview, GET-запрос (сводная информация по системе, содержащая все группировки и спутники);
2. создается спутник, POST-запрос;
3. изменяется имя спутника, PUT-запрос;
4. удаляется спутник по id, DELETE-запрос.

### Запуск тестов
Я запускал под Windows 10.

Прогон тестов:
```
jmeter -n -t jmeter-tests\tests\Test_Satellite_service.jmx -l jmeter-tests\results.jtl -j jmeter-tests\jmeter.log
```

Подготовка отчета:
```
jmeter -g jmeter-tests\results.jtl -o jmeter-tests\report
```

Отчет будет находится в файле [index.html](jmeter-tests/report/index.html)
Статистика в файле [statistics.json](jmeter-tests/report/statistics.json)

[README.MD тестируемого проекта](README.MD)