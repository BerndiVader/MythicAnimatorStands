package com.gmail.berndivader.mmArmorStandAnimator;

import static com.comphenix.protocol.PacketType.Play.Server.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;

public class EntityHider implements Listener {
    protected HashMap<Integer,UUID> EntityMap = new HashMap<>();
    
    @SuppressWarnings("deprecation")
	private static final PacketType[] ENTITY_PACKETS = {
    		HELD_ITEM_SLOT, POSITION,
    		SPAWN_ENTITY, ENTITY_EQUIPMENT, BED, ANIMATION, NAMED_ENTITY_SPAWN,
            COLLECT, SPAWN_ENTITY, SPAWN_ENTITY_LIVING, SPAWN_ENTITY_PAINTING, SPAWN_ENTITY_EXPERIENCE_ORB,
            ENTITY_VELOCITY, REL_ENTITY_MOVE, ENTITY_LOOK, ENTITY_MOVE_LOOK, ENTITY_MOVE_LOOK,
            ENTITY_TELEPORT, ENTITY_HEAD_ROTATION, ENTITY_STATUS, ATTACH_ENTITY, ENTITY_METADATA,
            ENTITY_EFFECT, REMOVE_ENTITY_EFFECT, BLOCK_BREAK_ANIMATION, UPDATE_ENTITY_NBT, COMBAT_EVENT
    };
    private ProtocolManager manager;
    private Listener bukkitListener;
    private PacketAdapter protocolListener;
    
    public EntityHider(Plugin plugin) {
        this.manager = ProtocolLibrary.getProtocolManager(); 
        manager.addPacketListener(protocolListener = constructProtocol(plugin));
    }
    
    public void hideEntity(Entity entity) {
    	EntityMap.put(entity.getEntityId(),entity.getUniqueId());
        PacketContainer e1 = new PacketContainer(ENTITY_DESTROY);
        e1.getIntegerArrays().write(0, new int[] { entity.getEntityId() });
    	for (AbstractPlayer p : MythicMobs.inst().getEntityManager().getPlayersInRangeSq(BukkitAdapter.adapt(entity.getLocation()), 16000)) {
    		try {
                manager.sendServerPacket(BukkitAdapter.adapt(p), e1);
        	} catch (InvocationTargetException e) {
        		//
        	}
        }
        return;
    }
    
    public void removeEntity(Entity entity) {
    	EntityMap.remove(entity.getEntityId(),entity.getUniqueId());
    }
    
    private PacketAdapter constructProtocol(Plugin plugin) {
        return new PacketAdapter(plugin, ENTITY_PACKETS) {
            @Override
            public void onPacketSending(PacketEvent event) {
            	int index = event.getPacketType() == COMBAT_EVENT ? 1 : 0;
            	int id = event.getPacket().getIntegers().readSafely(index);
               	if (EntityMap.containsKey(id)) {
           			event.setCancelled(true);
               	}
            }
        };
    }
    
    public void close() {
        if (manager != null) {
            HandlerList.unregisterAll(bukkitListener);
            manager.removePacketListener(protocolListener);
            manager = null;
        }
    }
}