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

package org.orecruncher.patchwork;

import javax.annotation.Nonnull;

import org.orecruncher.lib.VersionChecker;
import org.orecruncher.lib.logging.ModLog;
import org.orecruncher.patchwork.sided.SideSupport;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod(modid = ModInfo.MOD_ID, useMetadata = true, dependencies = ModInfo.DEPENDENCIES, version = ModInfo.VERSION, acceptedMinecraftVersions = ModInfo.MINECRAFT_VERSIONS, updateJSON = ModInfo.UPDATE_URL, certificateFingerprint = ModInfo.FINGERPRINT)
public class ModBase {

	@Instance(ModInfo.MOD_ID)
	protected static ModBase instance;

	@SidedProxy(clientSide = "org.orecruncher.patchwork.sided.ClientSupport", serverSide = "org.orecruncher.patchwork.sided.ServerSupport")
	protected static SideSupport proxy;
	protected static ModLog logger = ModLog.NULL_LOGGER;

	@Nonnull
	public static ModBase instance() {
		return instance;
	}

	@Nonnull
	public static SideSupport proxy() {
		return proxy;
	}

	@Nonnull
	public static ModLog log() {
		return logger;
	}

	// ==================================
	//
	// Standard proxy event handling.
	//
	// ==================================

	@EventHandler
	public void preInit(@Nonnull final FMLPreInitializationEvent event) {
		logger = ModLog.setLogger(ModInfo.MOD_ID, event.getModLog());
		logger.setDebug(ModOptions.logging.enableLogging);
		MinecraftForge.EVENT_BUS.register(this);

		proxy().preInit(event);
	}

	@EventHandler
	public void init(@Nonnull final FMLInitializationEvent event) {
		proxy().init(event);
	}

	@EventHandler
	public void postInit(@Nonnull final FMLPostInitializationEvent event) {
		proxy().postInit(event);
	}

	@EventHandler
	public void loadComplete(@Nonnull final FMLLoadCompleteEvent event) {
		proxy().loadCompleted(event);
	}

	@EventHandler
	public void onFingerprintViolation(@Nonnull final FMLFingerprintViolationEvent event) {
		proxy().fingerprintViolation(event);
	}

	// ==================================
	//
	// Extra event handling
	//
	// ==================================

	@SubscribeEvent
	public void playerLogin(final PlayerLoggedInEvent event) {
		if (ModOptions.logging.enableVersionCheck)
			new VersionChecker(ModInfo.MOD_ID, "patchwork.msg.NewVersion").playerLogin(event);
	}

}
