package net.tropicraft.core.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tropicraft.core.common.dimension.TropicraftDimension;
import net.tropicraft.core.common.entity.projectile.ExplodingCoconutEntity;

public class ExplodingCoconutItem extends Item {

    public ExplodingCoconutItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        // TODO config option
        final boolean canPlayerThrow = player.isCreative() || player.canUseGameMasterBlocks();
        //allow to use anywhere but in the main area of the server
        final boolean ltOverride = world.dimension() != TropicraftDimension.WORLD;
        ItemStack item = player.getItemInHand(hand);
        if (!canPlayerThrow && !ltOverride) {
            if (!world.isClientSide) {
                player.displayClientMessage(Component.translatable("tropicraft.coconutBombWarning"), false);
            }
            return new InteractionResultHolder<>(InteractionResult.PASS, item);
        }
        
        if (!player.isCreative()) {
            item.shrink(1);
        }
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!world.isClientSide) {
            float explosionRadius = ExplodingCoconutEntity.DEFAULT_EXPLOSION_RADIUS;
            CompoundTag tag = item.getTag();
            if (tag != null && tag.contains("explosion_radius", Tag.TAG_FLOAT)) {
                explosionRadius = tag.getFloat("explosion_radius");
            }
            ExplodingCoconutEntity coconut = new ExplodingCoconutEntity(world, player, explosionRadius);
            coconut.setItem(item);
            coconut.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            world.addFreshEntity(coconut);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, item);
    }
}
