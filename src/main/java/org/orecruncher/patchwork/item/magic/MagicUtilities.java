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

package org.orecruncher.patchwork.item.magic;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class MagicUtilities {

	private MagicUtilities() {

	}

	/**
	 * Collects all ItemStacks from the player inventory (as well as Bauble) that
	 * are magical devices. The bauble items are collected first before main
	 * inventory.
	 *
	 * @param player
	 * @return List of magic device ItemStacks
	 */
	@Nonnull
	public static final List<ItemStack> getMagicDevices(@Nonnull final EntityPlayer player) {
		final List<ItemStack> result = new ArrayList<>();

		int count = 0;
		final IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
		if (handler != null) {
			count = handler.getSlots();
			for (int i = 0; i < count; i++) {
				final ItemStack stack = handler.getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicDevice)
					result.add(stack);
			}
		}

		count = player.inventory.getSizeInventory();
		for (int i = 0; i < count; i++) {
			final ItemStack stack = player.inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicDevice)
				result.add(stack);
		}

		return result;
	}

}