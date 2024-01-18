package com.pattt.instancehousing.packets.util;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;

public abstract class AbstractPacket {
	// The packet we will be modifying
	protected PacketContainer handle;

	/**
	 * Constructs a new strongly typed wrapper for the given packet.
	 * 
	 * @param handle - handle to the raw packet data.
	 * @param type - the packet type.
	 */
	protected AbstractPacket(PacketContainer handle, PacketType type) {
		// Make sure we're given a valid packet
		if (handle == null)
			throw new IllegalArgumentException("Packet handle cannot be NULL.");
		if (!Objects.equal(handle.getType(), type))
			throw new IllegalArgumentException(handle.getHandle()
					+ " is not a packet of type " + type);

		this.handle = handle;
	}

	/**
	 * Retrieve a handle to the raw packet data.
	 * 
	 * @return Raw packet data.
	 */
	public PacketContainer getHandle() {
		return handle;
	}

	/**
	 * Send the current packet to the given receiver.
	 * 
	 * @param receiver - the receiver.
	 * @throws InvocationTargetException 
	 * @throws RuntimeException If the packet cannot be sent.
	 */
	public void sendPacket(Player receiver) throws InvocationTargetException {
		ProtocolLibrary.getProtocolManager().sendServerPacket(receiver,
				getHandle());
	}

	/**
	 * Send the current packet to all online players.
	 */
	public void broadcastPacket() {
		ProtocolLibrary.getProtocolManager().broadcastServerPacket(getHandle());
	}

	/**
	 * Simulate receiving the current packet from the given sender.
	 * 
	 * @param sender - the sender.
	 * @throws RuntimeException If the packet cannot be received.
	 * @deprecated Misspelled. recieve to receive
	 * @see #receivePacket(Player)
	 */
	@Deprecated
	public void recievePacket(Player player) {
		try {
			ProtocolLibrary.getProtocolManager().recieveClientPacket(player, getHandle());
		} catch (Exception e) {
			throw new RuntimeException("Cannot recieve packet.", e);
		}
	}

	/**
	 * Simulate receiving the current packet from the given sender.
	 * 
	 * @param sender - the sender.
	 * @throws RuntimeException if the packet cannot be received.
	 */
	public void receivePacket(Player player) {
		try {
			ProtocolLibrary.getProtocolManager().recieveClientPacket(player, getHandle());
		} catch (Exception e) {
			throw new RuntimeException("Cannot receive packet.", e);
		}
	}
}