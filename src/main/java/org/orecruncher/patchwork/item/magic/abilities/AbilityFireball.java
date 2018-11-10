/*
 * Licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.orecruncher.patchwork.item.magic.abilities;

import javax.annotation.Nonnull;

import org.orecruncher.patchwork.entity.EntityMagicFireball;
import org.orecruncher.patchwork.item.ModItems;
import org.orecruncher.patchwork.item.magic.AbilityHandler;
import org.orecruncher.patchwork.item.magic.ItemMagicDevice;
import org.orecruncher.patchwork.item.magic.capability.IMagicDevice;
import org.orecruncher.patchwork.item.magic.capability.IMagicDeviceSettable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AbilityFireball extends AbilityHandler {

	// Figure 20 shots with a normal wand
	private static final int ACTIVATION_COST = ItemMagicDevice.Quality.NORMAL.getMaxPower() / 20;
	private static final int COOLDOWN_TICKS = 4 * 20;

	public AbilityFireball() {
		super("fireball");
	}

	@Override
	public void onItemRightClick(@Nonnull final IMagicDevice caps, @Nonnull final ItemStack stack,
			@Nonnull final World world, @Nonnull final EntityPlayer player, @Nonnull final EnumHand hand) {
		// There is a cooldown on this item
		if (player.getCooldownTracker().hasCooldown(ModItems.MAGIC_DEVICE))
			return;

		boolean success = false;

		final IMagicDeviceSettable c = (IMagicDeviceSettable) caps;
		if (c.consumeEnergy(ACTIVATION_COST)) {
			
			final EntityMagicFireball fireball = new EntityMagicFireball(player);

			// Spawn the sucker
			world.spawnEntity(fireball);

			// Play the sound
			final BlockPos soundPos = new BlockPos(player);
			player.world.playEvent((EntityPlayer) null, 1018, soundPos, 0);

			success = true;

		} else {
			// Need to play a click sound because they are empty
			playNoChargeSound(player);
		}

		setCooldown(player, success ? COOLDOWN_TICKS : 20);
	}

}
