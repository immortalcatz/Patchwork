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

package org.orecruncher.patchwork.block;

import javax.annotation.Nonnull;

import org.orecruncher.patchwork.ModBase;
import org.orecruncher.patchwork.ModInfo;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;

public abstract class BlockContainerBase extends BlockContainer implements IBlockRegistration {

	@ItemStackHolder(value = "patchwork:material", meta = 2)
	public static final ItemStack ROTATE_TOOL = ItemStack.EMPTY;

	protected final String name;

	protected BlockContainerBase(@Nonnull final String name, @Nonnull final Material materialIn) {
		super(materialIn);
		this.name = name;
		setRegistryName(this.name);
		setTranslationKey(ModInfo.MOD_ID + "." + this.name);

		// Let the registration handler know about this
		// new item.
		BlockRegistrationHandler.add(this);
	}

	@Override
	@Nonnull
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public Item createItemBlock() {
		return new ItemBlock(this).setRegistryName(getRegistryName());
	}

	@Override
	public void registerBlockModel() {
		ModBase.proxy().registerItemRenderer(Item.getItemFromBlock(this), 0,
				new ModelResourceLocation(ModInfo.MOD_ID + ":" + this.name, "inventory"));
	}

	@Override
	public void onBlockClicked(@Nonnull final World world, @Nonnull final BlockPos pos,
			@Nonnull final EntityPlayer player) {
		if (!world.isRemote) {
			final IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof IRotateable) {
				final ItemStack heldItem = player.getHeldItemMainhand();
				if (ItemStack.areItemsEqual(heldItem, ROTATE_TOOL)) {
					final IRotateable rot = (IRotateable) state.getBlock();
					rot.rotateBlock(player, world, pos, player.getAdjustedHorizontalFacing());
				}
			}
		}
	}

}
