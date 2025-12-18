#!/bin/bash
# Скрипт для переключения между неэффективной и оптимизированной версиями

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SERVICE_B_DIR="$SCRIPT_DIR/service-b"

INEFFICIENT_IMPL="$SERVICE_B_DIR/src/main/java/com/example/serviceb/service/impl/InefficientUuidServiceImpl.java"
OPTIMIZED_IMPL="$SERVICE_B_DIR/src/main/java/com/example/serviceb/service/impl/OptimizedUuidServiceImpl.java"

echo "=== UUID Service Implementation Switcher ==="
echo ""
echo "Выберите версию для использования:"
echo "1) Неэффективная версия (InefficientUuidServiceImpl)"
echo "2) Оптимизированная версия (OptimizedUuidServiceImpl)"
echo ""
read -p "Ваш выбор (1 или 2): " choice

case $choice in
  1)
    echo "Переключение на неэффективную версию..."
    # Убираем @Primary у оптимизированной
    sed -i 's/@Primary/\/\/@Primary/' "$OPTIMIZED_IMPL"
    # Добавляем @Primary к неэффективной
    if ! grep -q "@Primary" "$INEFFICIENT_IMPL"; then
      sed -i '/@Service/a @Primary' "$INEFFICIENT_IMPL"
      # Добавим import если нужно
      if ! grep -q "import org.springframework.context.annotation.Primary;" "$INEFFICIENT_IMPL"; then
        sed -i '/import org.springframework.stereotype.Service;/a import org.springframework.context.annotation.Primary;' "$INEFFICIENT_IMPL"
      fi
    fi
    echo "✅ Активирована неэффективная версия"
    ;;
  2)
    echo "Переключение на оптимизированную версию..."
    # Убираем @Primary у неэффективной
    sed -i '/@Primary/d' "$INEFFICIENT_IMPL"
    # Активируем @Primary у оптимизированной
    sed -i 's/\/\/@Primary/@Primary/' "$OPTIMIZED_IMPL"
    echo "✅ Активирована оптимизированная версия"
    ;;
  *)
    echo "❌ Неверный выбор. Выход."
    exit 1
    ;;
esac

echo ""
echo "Пересборка проекта..."
cd "$SERVICE_B_DIR"
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
  echo ""
  echo "✅ Проект успешно пересобран!"
  echo ""
  echo "Теперь можно запустить сервис:"
  echo "  java -jar target/service-b-0.0.1-SNAPSHOT.jar"
  echo ""
  echo "Или с профилированием:"
  echo "  java -XX:StartFlightRecording=filename=profile.jfr,dumponexit=true \\"
  echo "       -jar target/service-b-0.0.1-SNAPSHOT.jar"
else
  echo "❌ Ошибка при сборке проекта"
  exit 1
fi
