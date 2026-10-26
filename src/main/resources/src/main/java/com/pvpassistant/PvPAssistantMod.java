package com.pvpassistant;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

public class PvPAssistantMod implements ClientModInitializer {

    public static boolean aimbotEnabled = true;
    public static boolean killauraEnabled = true;
    public static boolean maceAuraEnabled = true;
    public static boolean alwaysCriticals = true;

    private int attackCooldown = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            if (client.options.keyUse.isPressed() == false && net.minecraft.client.util.InputUtil.isKeyPressed(client.getWindow().getHandle(), KeyStorage.guiKey)) {
                if (client.currentScreen == null && !KeyStorage.waitingForKeyBind) {
                    client.setScreen(new ClickGuiScreen(Text.literal("Menu")));
                }
            }

            handlePvPLogic(client);
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            ThemeManager.Theme theme = ThemeManager.currentTheme;
            drawContext.drawTextWithShadow(client.textRenderer, "§l[PvP Assistant Deluxe]", 10, 10, theme.getAccentColor());
            drawContext.drawTextWithShadow(client.textRenderer, "Tema: " + theme.getName(), 10, 22, theme.getTextColor());
        });
    }

    private void handlePvPLogic(MinecraftClient client) {
        PlayerEntity player = client.player;
        double range = 4.5;

        Box area = player.getBoundingBox().expand(range);
        List<LivingEntity> targets = client.world.getEntitiesByClass(
                LivingEntity.class, area,
                e -> e != player && e.isAlive() && !e.isTeammate(player)
        );

        LivingEntity target = targets.stream()
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(player)))
                .orElse(null);

        if (target == null) return;

        if (aimbotEnabled) {
            Vec3d targetPos = target.getEyePos();
            Vec3d playerPos = player.getEyePos();
            double dx = targetPos.x - playerPos.x;
            double dy = targetPos.y - playerPos.y;
            double dz = targetPos.z - playerPos.z;
            double distSq = dx * dx + dz * dz;

            float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(distSq)));

            player.setYaw(player.getYaw() + (yaw - player.getYaw()) * 0.4F);
            player.setPitch(player.getPitch() + (pitch - player.getPitch()) * 0.4F);
        }

        if (attackCooldown > 0) attackCooldown--;

        if (attackCooldown <= 0) {
            ItemStack heldItem = player.getMainHandStack();
            boolean isMace = heldItem.getItem() instanceof MaceItem;

            if ((killauraEnabled && !isMace) || (maceAuraEnabled && isMace)) {
                if (alwaysCriticals && player.isOnGround()) {
                    player.jump();
                }
                client.interactionManager.attackEntity(player, target);
                player.swingHand(Hand.MAIN_HAND);
                attackCooldown = isMace ? 12 : 7;
            }
        }
    }
}
