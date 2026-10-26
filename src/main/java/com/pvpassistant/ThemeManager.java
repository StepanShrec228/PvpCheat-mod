package com.pvpassistant;

public class ThemeManager {
    public enum Theme {
        DARK_BLUE("Тёмно-синяя (По умолчанию)", 0xFF101828, 0xFF1E293B, 0xFF3B82F6, 0xFFFFFFFF),
        DARK("Строгая тёмная", 0xFF121212, 0xFF1E1E1E, 0xFFBB86FC, 0xFFFFFFFF),
        LIGHT("Светлая", 0xFFF8FAFC, 0xFFE2E8F0, 0xFF2563EB, 0xFF0F172A),
        BLUE("Ярко-синяя", 0xFF0B132B, 0xFF1C2541, 0xFF48CAE4, 0xFFFFFFFF);

        private final String name;
        private final int backgroundColor;
        private final int panelColor;
        private final int accentColor;
        private final int textColor;

        Theme(String name, int bgColor, int panelColor, int accentColor, int textColor) {
            this.name = name;
            this.backgroundColor = bgColor;
            this.panelColor = panelColor;
            this.accentColor = accentColor;
            this.textColor = textColor;
        }

        public String getName() { return name; }
        public int getBackgroundColor() { return backgroundColor; }
        public int getPanelColor() { return panelColor; }
        public int getAccentColor() { return accentColor; }
        public int getTextColor() { return textColor; }
    }

    public static Theme currentTheme = Theme.DARK_BLUE;

    public static void cycleTheme() {
        Theme[] values = Theme.values();
        int nextIndex = (currentTheme.ordinal() + 1) % values.length;
        currentTheme = values[nextIndex];
    }
}
