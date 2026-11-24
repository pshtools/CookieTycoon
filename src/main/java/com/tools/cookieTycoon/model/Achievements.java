package com.tools.cookieTycoon.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class Achievements {

    private static final Achievement[] BY_ID;
    private static final List<Achievement> ALL;

    static {
        List<Achievement> list = new ArrayList<>();
        define(list);

        int maxId = 0;
        for (Achievement a : list) {
            maxId = Math.max(maxId, a.id());
        }
        Achievement[] byId = new Achievement[maxId + 1];
        for (Achievement a : list) {
            if (byId[a.id()] != null) {
                throw new IllegalStateException("Дублируется id ачивки: " + a.id());
            }
            byId[a.id()] = a;
        }
        list.sort((x, y) -> Integer.compare(x.id(), y.id()));

        BY_ID = byId;
        ALL = Collections.unmodifiableList(list);
    }

    private Achievements() {
    }

    
    public static Achievement get(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : null;
    }

    
    public static List<Achievement> all() {
        return ALL;
    }

    
    private static void define(List<Achievement> out) {
        out.add(ach(0, UpgradeType.CURSOR, "Усиленный указательный палец", 100, 2, true, 1));
        out.add(ach(1, UpgradeType.CURSOR, "Крем от синдрома запястного канала", 500, 2, true, 1));
        out.add(ach(2, UpgradeType.CURSOR, "Амбидекстр", 10000, 2, true, 10));
        out.add(ach(3, UpgradeType.CURSOR, "Тысяча пальцев", 100000, 0.1, false, 25));
        out.add(ach(4, UpgradeType.CURSOR, "Миллион пальцев", 10000000, 0.5, false, 50));
        out.add(ach(5, UpgradeType.CURSOR, "Миллиард пальцев", 100000000, 5, false, 100));
        out.add(ach(6, UpgradeType.CURSOR, "Триллион пальцев", 1_000_000_000, 50, false, 150));
        out.add(ach(43, UpgradeType.CURSOR, "Квадриллион пальцев", 10 * Math.pow(10, 9), 500, false, 200));
        out.add(ach(82, UpgradeType.CURSOR, "Квинтиллион пальцев", 10 * Math.pow(10, 12), 5000, false, 250));
        out.add(ach(109, UpgradeType.CURSOR, "Секстиллион пальцев", 10 * Math.pow(10, 15), 50000, false, 300));
        out.add(ach(188, UpgradeType.CURSOR, "Септиллион пальцев", 10*Math.pow(10, 18), 500000, false, 350));
        out.add(ach(189, UpgradeType.CURSOR, "Октиллион пальцев", 10*Math.pow(10, 21), 5000000, false, 400));
        out.add(ach(660, UpgradeType.CURSOR, "Нониллион пальцев", 10*Math.pow(10, 24), 50000000, false, 450));
        out.add(ach(764, UpgradeType.CURSOR, "Дециллион пальцев", 10*Math.pow(10, 27), 500000000, false, 500));
        out.add(ach(873, UpgradeType.CURSOR, "Ундециллион пальцев", 10*Math.pow(10, 30), 5 * Math.pow(10, 9), false, 550));
        out.add(ach(7, UpgradeType.GRANNY, "Помощь от бабули", 1000, 2, true, 1));
        out.add(ach(8, UpgradeType.GRANNY, "Стальные скалки", 5000, 2, true, 5));
        out.add(ach(9, UpgradeType.GRANNY, "Вставные зубы", 50000, 2, true, 25));
        out.add(ach(44, UpgradeType.GRANNY, "Сливовый сок", 5000000, 2, true, 50));
        out.add(ach(110, UpgradeType.GRANNY, "Очки с двойным стеклом", 500000000, 2, true, 100));
        out.add(ach(192, UpgradeType.GRANNY, "Агенты старения", 50 * Math.pow(10, 9), 2, true, 150));
        out.add(ach(294, UpgradeType.GRANNY, "Экстремальные ходунки", 50 * Math.pow(10, 12), 2, true, 200));
        out.add(ach(307, UpgradeType.GRANNY, "Безудержный", 50 * Math.pow(10, 15), 2, true, 250));
        out.add(ach(428, UpgradeType.GRANNY, "Обратная деменция", 50 * Math.pow(10, 18), 2, true, 350));
        out.add(ach(480, UpgradeType.GRANNY, "Стойкие краски для волос", 50 * Math.pow(10, 21), 2, true, 350));
        out.add(ach(506, UpgradeType.GRANNY, "Хорошие манеры", 500 * Math.pow(10, 24), 2, true, 400));
        out.add(ach(662, UpgradeType.GRANNY, "Генерационная дегенерация", 5 * Math.pow(10, 30), 2, true, 450));
        out.add(ach(700, UpgradeType.GRANNY, "Визиты", 50 * Math.pow(10, 33), 2, true, 500));
        out.add(ach(743, UpgradeType.GRANNY, "Кухонные кабинеты", 500 * Math.pow(10, 36), 2, true, 550));
        out.add(ach(840, UpgradeType.GRANNY, "Трости с пенопластовыми наконечниками", 5 * Math.pow(10, 42), 2, true, 600));
        out.add(ach(10, UpgradeType.FARM, "Дешёвые мотыги", 11000, 2, true, 1));
        out.add(ach(11, UpgradeType.FARM, "Удобрение", 55000, 2, true, 5));
        out.add(ach(12, UpgradeType.FARM, "Печеньковые деревья", 550000, 2, true, 25));
        out.add(ach(45, UpgradeType.FARM, "Генетически-модифицированные печеньки", 55000000, 2, true, 50));
        out.add(ach(111, UpgradeType.FARM, "Пряничные чучела", 5.5 * Math.pow(10, 9), 2, true, 100));
        out.add(ach(193, UpgradeType.FARM, "Посыпка", 550 * Math.pow(10, 9), 2, true, 150));
        out.add(ach(295, UpgradeType.FARM, "Помадковые грибы", 550 * Math.pow(10, 12), 2, true, 200));
        out.add(ach(308, UpgradeType.FARM, "Пшеничные триффиды", 550 * Math.pow(10, 15), 2, true, 250));
        out.add(ach(429, UpgradeType.FARM, "Гуманные пестициды", 550 * Math.pow(10, 18), 2, true, 350));
        out.add(ach(481, UpgradeType.FARM, "Сараи", 550 * Math.pow(10, 21), 2, true, 350));
        out.add(ach(507, UpgradeType.FARM, "Линдвормы", 5.5 * Math.pow(10, 27), 2, true, 400));
        out.add(ach(663, UpgradeType.FARM, "Глобальное хранилище семян", 55 * Math.pow(10, 30), 2, true, 450));
        out.add(ach(701, UpgradeType.FARM, "Обратное веганство", 550 * Math.pow(10, 33), 2, true, 500));
        out.add(ach(744, UpgradeType.FARM, "Мульча из печенья", 5.5 * Math.pow(10, 39), 2, true, 550));
        out.add(ach(841, UpgradeType.FARM, "Самоходные тракторы", 55 * Math.pow(10, 42), 2, true, 600));
        out.add(ach(16, UpgradeType.MINE, "Сахарный газ", 120000, 2, true, 1));
        out.add(ach(17, UpgradeType.MINE, "Мега-дрель", 600000, 2, true, 5));
        out.add(ach(18, UpgradeType.MINE, "Ультра-дрель", 6000000, 2, true, 25));
        out.add(ach(47, UpgradeType.MINE, "Ультима-дрель", 600000000, 2, true, 50));
        out.add(ach(113, UpgradeType.MINE, "Минирование водородной бомбой", 600000000, 2, true, 100));
        out.add(ach(195, UpgradeType.MINE, "Главная кузница", 6 * Math.pow(10, 12), 2, true, 150));
        out.add(ach(296, UpgradeType.MINE, "Планетоделители", 6 * Math.pow(10, 15), 2, true, 200));
        out.add(ach(309, UpgradeType.MINE, "Скважины рапсового масла", 6 * Math.pow(10, 18), 2, true, 250));
        out.add(ach(430, UpgradeType.MINE, "Люди-кроты", 6 * Math.pow(10, 21), 2, true, 350));
        out.add(ach(482, UpgradeType.MINE, "Подземные канарейки", 6 * Math.pow(10, 24), 2, true, 350));
        out.add(ach(508, UpgradeType.MINE, "Снова скучно", 60 * Math.pow(10, 27), 2, true, 400));
        out.add(ach(664, UpgradeType.MINE, "Добыча воздуха", 600 * Math.pow(10, 30), 2, true, 450));
        out.add(ach(702, UpgradeType.MINE, "Карамельные сплавы", 6 * Math.pow(10, 36), 2, true, 500));
        out.add(ach(745, UpgradeType.MINE, "Вкусная минералогия", 60 * Math.pow(10, 39), 2, true, 550));
        out.add(ach(842, UpgradeType.MINE, "Опоры шахтовых стволов", 600 * Math.pow(10, 42), 2, true, 600));
    }

    private static Achievement ach(int id, UpgradeType type, String name, double price, double bonus,
                                   boolean multiplicative, int requiredCount) {
        return new Achievement(id, type, name, price, bonus, multiplicative, requiredCount);
    }
}
