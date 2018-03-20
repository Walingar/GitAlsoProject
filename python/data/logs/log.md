# Немного статистики
Всего файлов: 1784

Коммитов с start_time до end_time

## До удаления больших коммитов
Медиана по файлам в коммите: 
 * когда убрали рандомный файл: 4
 * когда убрали по 1 файлу и пустые: 7

Среднее арифметическое:
 * когда убрали рандомный файл: 9.3
 * когда убрали по 1 файлу и пустые: 99.5

Сколько убрали то, где остался 1 файл одному файлу: 57

## после удаления больших файлов
Медиана по файлам в коммите: 3

Среднее арифметическое: 3.8
 
Сколько убрали маленьких и больших коммитов: 80

## max in Random
min_prob = 25%
N = 26
count = 10

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 10 | 65 | - |
| fileA | 130 | 128 | 361 | 
 


## max in non Random
min_prob = 25%
N = 26
count = 10

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 10 | 65 | - |
| fileA | 130 | 116 | 357 | 


## Sum in non Random
min_prob = 25%
N = 26
count = 10

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 10 | 65 | - |
| fileA | 130 | 128 | 345 | 

## Sum in random
min_prob = 25%
N = 26
count = 10

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 10 | 65 | - |
| fileA | 130 | 131 | 358 |


# top from every file
## sum in random
min_prob = 25%
N = 26
count = 10

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 11 | 65 | - |
| fileA | 129 | 66 | 423 | 

## sum in non random

min_prob = 25%
N = 26
count = 10

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 11 | 69 | - |
| fileA | 129 | 60 | 425 | 



min_prob = 25%
N = 26
count = 5

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 11 | 69 | - |
| fileA | 129 | 54 | 431 | 

## Немного тестов на изменение count

min_prob = 25%
N = 26
count = 5

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 10 | 66 | - |
| fileA | 130 | 90 | 398 | 



min_prob = 25%
N = 26
count = 3

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 10 | 66 | - |
| fileA | 130 | 75 | 413 | 



min_prob = 25%
N = 26
count = 2

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 10 | 66 | - |
| fileA | 130 | 51 | 437 | 



min_prob = 25%
N = 26
count = 1

| truth\prediction | nothing | fileA | fileB |
| --- | --- | --- | --- |
| nothing | 10 | 66 | - |
| fileA | 130 | 32 | 456 | 


# associative rules
##from 3:
20 support:

* right = 56
* silent = 411
* false = 227

10 support:
* right = 62
* silent = 348
* false = 284
##from 5:
20 support:

* right = 68
* silent = 411
* false = 215

10 support:
* right = 78
* silent = 348
* false = 268
