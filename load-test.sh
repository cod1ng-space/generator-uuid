#!/bin/bash
# Скрипт для генерации нагрузки на service-b

echo "Генерация нагрузки на service-b..."
echo "Отправка 1000 запросов к http://localhost:8080/uuid"

for i in {1..1000}
do
  curl -s http://localhost:8080/uuid > /dev/null
  if [ $((i % 100)) -eq 0 ]; then
    echo "Отправлено $i запросов..."
  fi
done

echo "Готово! Отправлено 1000 запросов."
