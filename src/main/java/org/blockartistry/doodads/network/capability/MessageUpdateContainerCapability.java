// From Choonsters TestMod3!

package org.blockartistry.doodads.network.capability;

import javax.annotation.Nullable;

import org.blockartistry.doodads.Doodads;
import org.blockartistry.doodads.util.capability.CapabilityUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Base class for messages that update capability data for a single slot of a
 * {@link Container}.
 *
 * @param <HANDLER>
 *            The capability handler type
 * @param <DATA>
 *            The data type written to and read from the buffer
 * @author Choonster
 */
public abstract class MessageUpdateContainerCapability<HANDLER, DATA> implements IMessage {
	/**
	 * The {@link Capability} instance to update.
	 */
	protected final Capability<HANDLER> capability;

	/**
	 * The {@link EnumFacing} to get the capability handler from.
	 */
	@Nullable
	EnumFacing facing;

	/**
	 * The window ID of the {@link Container}.
	 */
	int windowID;

	/**
	 * The slot's index in the {@link Container}.
	 */
	int slotNumber;

	/**
	 * The capability data instance.
	 */
	DATA capabilityData;

	protected MessageUpdateContainerCapability(final Capability<HANDLER> capability) {
		this.capability = capability;
	}

	protected MessageUpdateContainerCapability(final Capability<HANDLER> capability, @Nullable final EnumFacing facing,
			final int windowID, final int slotNumber, final HANDLER handler) {
		this.capability = capability;
		this.facing = facing;
		this.windowID = windowID;
		this.slotNumber = slotNumber;
		this.capabilityData = convertCapabilityToData(handler);
	}

	/**
	 * Convert from the supplied buffer into your specific message type
	 *
	 * @param buf
	 *                The buffer
	 */
	@Override
	public final void fromBytes(final ByteBuf buf) {
		this.windowID = buf.readInt();
		this.slotNumber = buf.readInt();

		final int facingIndex = buf.readByte();
		if (facingIndex >= 0) {
			this.facing = EnumFacing.getFront(facingIndex);
		} else {
			this.facing = null;
		}

		final boolean hasData = buf.readBoolean();
		if (hasData) {
			this.capabilityData = readCapabilityData(buf);
		}
	}

	/**
	 * Deconstruct your message into the supplied byte buffer
	 *
	 * @param buf
	 *                The buffer
	 */
	@Override
	public final void toBytes(final ByteBuf buf) {
		buf.writeInt(this.windowID);
		buf.writeInt(this.slotNumber);

		if (this.facing != null) {
			buf.writeByte(this.facing.getIndex());
		} else {
			buf.writeByte(-1);
		}

		buf.writeBoolean(hasData());
		writeCapabilityData(buf, this.capabilityData);
	}

	/**
	 * Is there any capability data to sync?
	 *
	 * @return Is there any capability data to sync?
	 */
	public final boolean hasData() {
		return this.capabilityData != null;
	}

	/**
	 * Convert the capability handler instance to a data instance.
	 *
	 * @param handler
	 *                    The handler
	 * @return The data instance
	 */
	@Nullable
	protected abstract DATA convertCapabilityToData(HANDLER handler);

	/**
	 * Read the capability data from the buffer.
	 *
	 * @param buf
	 *                The buffer
	 * @return The data instance
	 */
	protected abstract DATA readCapabilityData(final ByteBuf buf);

	/**
	 * Write the capability data to the buffer.
	 *
	 * @param buf
	 *                 The buffer
	 * @param data
	 *                 The data instance
	 */
	protected abstract void writeCapabilityData(final ByteBuf buf, DATA data);

	public abstract static class Handler<HANDLER, DATA, MESSAGE extends MessageUpdateContainerCapability<HANDLER, DATA>>
			implements IMessageHandler<MESSAGE, IMessage> {

		/**
		 * Called when a message is received of the appropriate type. You can optionally
		 * return a reply message, or null if no reply is needed.
		 *
		 * @param message
		 *                    The message
		 * @param ctx
		 *                    The message context
		 * @return an optional return message
		 */
		@Nullable
		@Override
		public final IMessage onMessage(final MESSAGE message, final MessageContext ctx) {
			if (!message.hasData())
				return null; // Don't do anything if no data was sent

			Doodads.proxy().getThreadListener(ctx).addScheduledTask(() -> {
				final EntityPlayer player = Doodads.proxy().getPlayer(ctx);

				final Container container;
				if (message.windowID == 0) {
					container = player.inventoryContainer;
				} else if (message.windowID == player.openContainer.windowId) {
					container = player.openContainer;
				} else {
					return;
				}

				final ItemStack originalStack = container.getSlot(message.slotNumber).getStack();
				final HANDLER originalHandler = CapabilityUtils.getCapability(originalStack, message.capability,
						message.facing);
				if (originalHandler != null) {
					final ItemStack newStack = originalStack.copy();

					final HANDLER newHandler = CapabilityUtils.getCapability(newStack, message.capability,
							message.facing);
					assert newHandler != null;

					applyCapabilityData(newHandler, message.capabilityData);

					if (!originalHandler.equals(newHandler)) {
						container.putStackInSlot(message.slotNumber, newStack);
					}
				}
			});

			return null;
		}

		/**
		 * Apply the capability data from the data instance to the capability handler
		 * instance.
		 *
		 * @param handler
		 *                    The capability handler instance
		 * @param data
		 *                    The data
		 */
		protected abstract void applyCapabilityData(final HANDLER handler, final DATA data);
	}
}