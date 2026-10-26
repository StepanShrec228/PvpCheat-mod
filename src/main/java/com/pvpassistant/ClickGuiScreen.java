package com.pvpassistant;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickGuiScreen extends Screen {

    private int guiLeft, guiTop;
    private final int guiWidth = 320;
    private final int guiHeight = 220;

    protected ClickGuiScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        guiLeft = (this.width - guiWidth) / 2;
        guiTop = (this.height - guiHeight) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        ThemeManager.Theme theme = ThemeManager.currentTheme;

        drawRoundedRect(context, guiLeft, guiTop, guiWidth, guiHeight, 8, theme.getBackgroundColor());
        
        drawRoundedRect(context, guiLeft, guiTop, guiWidth, 30, 8, theme.getPanelColor());
        context.drawTextWithShadow(this.textRenderer, "⚔ PvP Assistant Deluxe v2.0", guiLeft + 12, guiTop + 10, theme.getAccentColor());

        drawModuleButton(context, "Aimbot Assist", PvPAssistantMod.aimbotEnabled, guiLeft + 20, guiTop + 45, mouseX, mouseY);
        drawModuleButton(context, "Killaura", PvPAssistantMod.killauraEnabled, guiLeft + 20, guiTop + 80, mouseX, mouseY);
        drawModuleButton(context, "Mace Aura (Для булавы)", PvPAssistantMod.maceAuraEnabled, guiLeft + 20, guiTop + 115, mouseX, mouseY);
        drawModuleButton(context, "Auto Criticals (Авто-криты)", PvPAssistantMod.alwaysCriticals, guiLeft + 20, guiTop + 150, mouseX, mouseY);

        drawCustomButton(context, "Тема: " + theme.getName(), guiLeft + 20, guiTop + 185, 135, 24, theme.getPanelColor(), mouseX, mouseY);

        String bindText = KeyStorage.waitingForKeyBind ? "Нажмите..." : "Кнопка GUI: " + GLFW.glfwGetKeyName(KeyStorage.guiKey, 0);
        drawCustomButton(context, bindText, guiLeft + 165, guiTop + 185, 135, 24, theme.getPanelColor(), mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isInside(mouseX, mouseY, guiLeft + 20, guiTop + 45, 280, 26)) {
                PvPAssistantMod.aimbotEnabled = !PvPAssistantMod.aimbotEnabled;
            }
            if (isInside(mouseX, mouseY, guiLeft + 20, guiTop + 80, 280, 26)) {
                PvPAssistantMod.killauraEnabled = !PvPAssistantMod.killauraEnabled;
            }
            if (isInside(mouseX, mouseY, guiLeft + 20, guiTop + 115, 280, 26)) {
                PvPAssistantMod.maceAuraEnabled = !PvPAssistantMod.maceAuraEnabled;
            }
            if (isInside(mouseX, mouseY, guiLeft + 20, guiTop + 150, 280, 26)) {
                PvPAssistantMod.alwaysCriticals = !PvPAssistantMod.alwaysCriticals;
            }
            if (isInside(mouseX, mouseY, guiLeft + 20, guiTop + 185, 135, 24)) {
                ThemeManager.cycleTheme();
            }
            if (isInside(mouseX, mouseY, guiLeft + 165, guiTop + 185, 135, 24)) {
                KeyStorage.waitingForKeyBind = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyStorage.waitingForKeyBind) {
            KeyStorage.guiKey = keyCode;
            KeyStorage.waitingForKeyBind = false;
            return true;
        }
        if (keyCode == KeyStorage.guiKey) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + width, y + height - radius, color);
        context.fill(x + 1, y + 1, x + radius, y + radius, color);
        context.fill(x + width - radius, y + 1, x + width - 1, y + radius, color);
        context.fill(x + 1, y + height - radius, x + radius, y + height - 1, color);
        context.fill(x + width - radius, y + height - radius, x + width - 1, y + height - 1, color);
    }

    private void drawModuleButton(DrawContext context, String name, boolean state, int x, int y, double mouseX, double mouseY) {
        ThemeManager.Theme theme = ThemeManager.currentTheme;
        boolean hover = isInside(mouseX, mouseY, x, y, 280, 26);
        int bgColor = hover ? theme.getPanelColor() : (theme.getPanelColor() & 0xDDFFFFFF);
        
        drawRoundedRect(context, x, y, 280, 26, 5, bgColor);
        context.drawTextWithShadow(this.textRenderer, name, x + 10, y + 8, theme.getTextColor());
        
        String status = state ? "§aВКЛ" : "§cВЫКЛ";
        context.drawTextWithShadow(this.textRenderer, status, x + 280 - this.textRenderer.getWidth(status) - 10, y + 8, 0xFFFFFFFF);
    }

    private void drawCustomButton(DrawContext context, String text, int x, int y, int width, int height, int baseColor, double mouseX, double mouseY) {
        ThemeManager.Theme theme = ThemeManager.currentTheme;
        boolean hover = isInside(mouseX, mouseY, x, y, width, height);
        drawRoundedRect(context, x, y, width, height, 5, hover ? theme.getAccentColor() : baseColor);
        context.drawCenteredTextWithShadow(this.textRenderer, text, x + width / 2, y + (height - 8) / 2, theme.getTextColor());
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
