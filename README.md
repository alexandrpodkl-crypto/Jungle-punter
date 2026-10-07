# Iron Jungle — мод для Minecraft 1.21.1 (Fabric)

Первая механика: **ручная пантера**.

## Как играть с пантерой
- Живёт в джунглях (обычные, редкие, бамбуковые). Есть яйцо призыва во вкладке «Яйца призыва».
- **Приручение:** сырая треска или лосось, шанс 1 к 3 (сердечки = получилось, дым = нет).
- **ПКМ по своей пантере:** сидеть / идти за тобой.
- **Кормление:** любая рыба или мясо лечит её. При полном здоровье еда включает размножение.
- Защищает хозяина и атакует тех, кого бьёшь ты. Прыгает на цель.
- Дикая: 20 HP, урон 5. Приручённая: 40 HP, урон 7. Быстрее волка.

## Как запустить
1. Установи **JDK 21** (например, Eclipse Temurin 21: https://adoptium.net).
2. Распакуй архив, открой терминал в папке `ironjungle`.
3. Запусти игру сразу с модом (Fabric ставить не нужно):
   - Windows: `gradlew.bat runClient`
   - Linux/Mac: `./gradlew runClient`

   Первый запуск долгий (10–15 минут): скачиваются Minecraft и библиотеки.
4. Чтобы получить файл мода для обычной игры: `gradlew.bat build`.
   Готовый `.jar` будет в `build/libs/`. Его кладут в папку `mods` вместе с Fabric Loader и Fabric API для 1.21.1.

Удобнее всего работать в **IntelliJ IDEA Community**: File → Open → папка `ironjungle`, дальше Gradle всё подтянет сам.

## Где что лежит
- `src/main/java/dev/ironjungle/entity/PantherEntity.java` — поведение (ИИ, приручение, характеристики).
- `src/main/java/dev/ironjungle/entity/ModEntities.java` — регистрация, спавн в джунглях, яйцо.
- `src/client/java/dev/ironjungle/client/PantherModel.java` — модель и анимации.
- `src/main/resources/assets/ironjungle/textures/entity/panther/panther.png` — текстура (64×64, можно перерисовать).

## Если сборка падает
Скопируй сюда текст ошибки из терминала. Чаще всего дело в версиях в `gradle.properties`, актуальные есть на https://fabricmc.net/develop.
