package com.tools.cookieTycoon.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


public enum UpgradeType {

    CURSOR("cursor", "IRON_INGOT", "Курсор", "Мышь и курсоры",
            15, 0.1,
            1, null, false,
            "Автоматический клик каждые 10 секунд."),

    GRANNY("granny", "STONE", "Бабуля", "Бабули",
            100, 1,
            10, null, false,
            "Хорошая бабуля испекла ещё печенья."),

    FARM("farm", "WHEAT", "Ферма", "Фермы",
            1100, 8,
            19, CURSOR, false,
            "Выращивает печеньковые растения из печеньковых семян."),

    MINE("mine", "COBBLESTONE", "Шахта", "Шахты",
            12000, 47,
            28, GRANNY, false,
            "Добывает тесто для печенья и шоколадную крошку."),

    FACTORY("factory", "DISPENSER", "Фабрика", "Фабрики",
            130000, 260,
            37, FARM, false,
            "Производит большое количество печенья."),

    BANK("bank", "GOLD_BLOCK", "Банк", "Банки",
            1400000, 1400,
            46, MINE, false,
            "Генерирует печенье из процентов."),

    TEMPLE("temple", "MOSSY_COBBLESTONE_SLAB", "Храм", "Храмы",
            20_000_000, 7800,
            3, FACTORY, false,
            "Заполнен драгоценным древним шоколадом."),

    TOWER("tower", "BLAZE_POWDER", "Башня мага", "Башни мага",
            330_000_000, 44000,
            12, BANK, false,
            "Призывает печенье с помощью магических заклинаний."),

    SHIPMENT("shipment", "IRON_BLOCK", "Космическая доставка", "Космические доставки",
            51 * Math.pow(10, 8), 260000,
            21, TEMPLE, false,
            "Доставляет свежее печенье с планеты печенья."),

    LAB("lab", "GLASS_BOTTLE", "Алхимическая лаборатория", "Алхимические лаборатории",
            75 * Math.pow(10, 9), 1_600_000,
            30, TOWER, false,
            "Превращает золото в печенье!"),

    PORTAL("portal", "OBSIDIAN", "Портал", "Порталы",
            Math.pow(10, 12), 10_000_000,
            39, SHIPMENT, false,
            "Открывает дверь в Печеньковселенную."),

    TIMEMACHINE("timemachine", "CLOCK", "Машина времени", "Машины времени",
            14 * Math.pow(10, 12), 65_000_000,
            48, LAB, false,
            "Доставляет печенье из прошлого - ещё до того, как его съели."),

    CONDENSER("condenser", "OBSERVER", "Конденсатор антиматерии", "Конденсаторы антиматерии",
            170 * Math.pow(10, 12), 430_000_000,
            5, PORTAL, false,
            "Конденсирует антиматерию Вселенной в печенье."),

    PRISM("prism", "NETHER_STAR", "Призма", "Призмы",
            2.1 * Math.pow(10, 15), 2.9 * Math.pow(10, 9),
            14, TIMEMACHINE, false,
            "Преобразует свет в печенье."),

    CHANCEMAKER("chancemaker", "GREEN_CONCRETE", "Шансмейкер", "Шансмейкеры",
            26 * Math.pow(10, 15), 21 * Math.pow(10, 9),
            23, CONDENSER, false,
            "Создает печенье из воздуха благодаря чистой удачи."),

    ENGINE("engine", "PISTON", "Фрактальный двигатель", "Фрактальные двигатели",
            310 * Math.pow(10, 15), 150 * Math.pow(10, 9),
            32, PRISM, false,
            "Увеличивает количество печенья."),

    CONSOLE("console", "REDSTONE_LAMP", "Консоль Java", "Консоли Java",
            71 * Math.pow(10, 18), 1.1 * Math.pow(10, 12),
            41, CHANCEMAKER, false,
            "Создает печенье из того самого кода,", "на котором написана эта игра."),

    IDLEVERSE("idleverse", "END_CRYSTAL", "Пустая вселенная", "Пустые вселенные",
            12 * Math.pow(10, 21), 8.3 * Math.pow(10, 12),
            50, ENGINE, false,
            "Кроме нашей существует ещё бессчетное множество ", "пустых вселенных. Вы наконец-то нашли способ украсть их ", "продукцию и превратить всё, что они сделали, в печенье!"),

    CORTEXBAKER("cortexbaker", "FURNACE", "Мозговой пекарь", "Мозговые пекари",
            1.9 * Math.pow(10, 24), 64 * Math.pow(10, 12),
            25, CONSOLE, false,
            "Этот искусственный мозг размером с планету ", "способен воплощать мечты о печенье. Пространство и ", "время не имеют значения. Реальность произвольна."),

    YOU("you", "PLAYER_HEAD", "", "Вы",
            540 * Math.pow(10, 24), 510 * Math.pow(10, 12),
            34, IDLEVERSE, true,
            "Всё это печенье существует только благодаря вам. ", "Вы подумали, что, будь вас больше, вы", "смогли бы и заработать больше.");

    
    public static final double PRICE_GROWTH = 1.15;

    public static final int COUNT = values().length;

    private static final UpgradeType[] VALUES = values();
    private static final Map<String, UpgradeType> BY_KEY = new HashMap<>();
    private static final List<String> KEYS;

    static {
        for (UpgradeType type : VALUES) {
            BY_KEY.put(type.key, type);
        }
        KEYS = Collections.unmodifiableList(
                Arrays.stream(VALUES).map(t -> t.key).collect(Collectors.toList()));
    }

    private final String key;
    private final String icon;
    private final String displayName;
    private final String pluralName;
    private final double basePrice;
    private final double baseCps;
    private final int menuSlot;
    private final UpgradeType requires;
    private final boolean personal;
    private final List<String> lore;

    UpgradeType(String key, String icon, String displayName, String pluralName,
                double basePrice, double baseCps, int menuSlot, UpgradeType requires,
                boolean personal, String... lore) {
        this.key = key;
        this.icon = icon;
        this.displayName = displayName;
        this.pluralName = pluralName;
        this.basePrice = basePrice;
        this.baseCps = baseCps;
        this.menuSlot = menuSlot;
        this.requires = requires;
        this.personal = personal;
        this.lore = List.of(lore);
    }

    public String key() { return key; }

    
    public String icon() { return icon; }

    public String displayName() { return displayName; }

    
    public String pluralName() { return pluralName; }

    public double baseCps() { return baseCps; }

    
    public int menuSlot() { return menuSlot; }

    
    public UpgradeType requires() { return requires; }

    
    public boolean isPersonal() { return personal; }

    public List<String> lore() { return lore; }

    
    public double price(int owned) {
        return basePrice * Math.pow(PRICE_GROWTH, owned);
    }

    
    public static double bulkFactor(int amount) {
        if (amount == 1) {
            return 1.0;
        }
        return (Math.pow(PRICE_GROWTH, amount) - 1) / (PRICE_GROWTH - 1);
    }

    public static UpgradeType byKey(String key) {
        return key == null ? null : BY_KEY.get(key.toLowerCase(Locale.ROOT));
    }

    public static UpgradeType byOrdinal(int ordinal) {
        return VALUES[ordinal];
    }

    public static List<String> keys() {
        return KEYS;
    }
}
