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

package org.orecruncher.patchwork.item;

import org.orecruncher.patchwork.client.ModCreativeTab;

import net.minecraft.item.Item;

public class ModItems {

	//@formatter:off
	public static final Item COIN = new ItemCoin();
	public static final Item MOB_NET = new ItemMobNet();
	public static final Item TOOLS = new ItemTools();
	public static final Item MAGNET = new ItemMagnet();
	public static final Item RING_OF_FLIGHT = new ItemRingOfFlight();
	
	// Material
	public static final Item REPAIR_PASTE = new ItemBase("repairpaste").setCreativeTab(ModCreativeTab.tab);
	public static final Item FLIGHT_ESSENCE = new ItemBase("flight_essence").setCreativeTab(ModCreativeTab.tab).setHasEffect(true);
	public static final Item WING_FEATHER = new ItemBase("wing_feather").setCreativeTab(ModCreativeTab.tab);
	public static final Item WING_OBSIDIAN = new ItemBase("wing_obsidian").setCreativeTab(ModCreativeTab.tab);
	public static final Item WING_SPEED = new ItemBase("wing_speed").setCreativeTab(ModCreativeTab.tab);
	public static final Item WING_STURDY = new ItemBase("wing_sturdy").setCreativeTab(ModCreativeTab.tab);
	//@formatter:on

	public static void initialize() {
		// Currently a do nothing function. By calling this it triggers the
		// classes static initializers to run.
	}

}
