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

package org.orecruncher.patchwork.common.block.furnace3d;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TESRFurnace3D extends TileEntitySpecialRenderer<TileEntityFurnace3D> {

	private static final float SCALE = 0.7F;

	/*
	 * Helper class used for cracking the slots in the furnace. The axis offsets are
	 * based on a block facing south, which means the player would be looking
	 * northward into the machine. The origin of these offsets are the center of the
	 * block.
	 *
	 * The values can be determined as:
	 *
	 * LEFT < xOffset < RIGHT DOWN < yOffset < UP BACK < zOffset < FRONT
	 */
	private enum SlotHelper {
		//
		INPUT(Furnace3DStackHandler.INPUT_SLOT, -0.2F, -0.05F, 0.185F),
		//
		OUTPUT(Furnace3DStackHandler.OUTPUT_SLOT, 0.2F, -0.05F, 0.185F),
		//
		FUEL(Furnace3DStackHandler.FUEL_SLOT, 0.0F, -0.55F, 0.185F);

		private final int slot;
		private final float xOffset;
		private final float yOffset;
		private final float zOffset;

		private SlotHelper(final int slot, final float dX, final float dY, final float dZ) {
			this.slot = slot;
			this.xOffset = dX;
			this.yOffset = dY;
			this.zOffset = dZ;
		}

		public ItemStack getStack(@Nonnull final TileEntityFurnace3D te) {
			return te.getStackInSlot(this.slot);
		}

		public float offsetX() {
			return this.xOffset;
		}

		public float offsetY() {
			return this.yOffset;
		}

		public float offsetZ() {
			return this.zOffset;
		}
	}

	/*
	 * We use an EntityItem to render the stacks within the furnace. It does things
	 * like handling stack sizes just like you see when you drop different sized
	 * stacks on the ground.
	 *
	 * You only need to create one, ONCE. No need to keep creating them per
	 * ItemStack per render pass. Make sure that hover is set to 0 otherwise you
	 * will get quirky placements because of the EntityItem wanting to bob.
	 */
	private final EntityItem mock;

	public TESRFurnace3D() {
		this.mock = new EntityItem(null);
		this.mock.hoverStart = 0;
	}

	@Override
	public void render(@Nonnull final TileEntityFurnace3D te, final double x, final double y, final double z,
			final float partialTicks, final int destroyStage, final float alpha) {

		// Short circuit if there is nothing to do
		if (te.isEmpty())
			return;

		// Need this to get the proper ambient lighting on the items within. Without it
		// they will render darker than they should.
		final int i = te.getWorld().getCombinedLight(te.getPos().up(), 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i % 65536, i / 65536);

		// Have to set rotation based on facing so the items go in their proper
		// locations
		final EnumFacing facing = te.getFacing();
		GlStateManager.pushMatrix();
		// Translate to the center of the block. Makes keeping track of rotations much
		// easier.
		GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
		// Rotate so that further translations are relative to the front facing.
		GlStateManager.rotate(-facing.getHorizontalAngle(), 0, 1F, 0);

		for (final SlotHelper slot : SlotHelper.values()) {
			final ItemStack stack = slot.getStack(te);
			if (!stack.isEmpty()) {
				// Set the stack that we want to render
				this.mock.setItem(stack);
				GlStateManager.pushMatrix();
				// Adjust to the offset within the block for rendering. Don't forget this
				// translation is relative to the center of the block, and that a rotation
				// has already been applied to take into account block facing.
				GlStateManager.translate(slot.offsetX(), slot.offsetY(), slot.offsetZ());
				GlStateManager.scale(SCALE, SCALE, SCALE);
				Minecraft.getMinecraft().getRenderManager().renderEntity(this.mock, 0, 0, 0, 0F, 0, false);
				GlStateManager.popMatrix();
			}
		}
		GlStateManager.popMatrix();
	}

}
