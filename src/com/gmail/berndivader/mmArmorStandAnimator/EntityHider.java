package com.gmail.berndivader.mmArmorStandAnimator;

import static com.comphenix.protocol.PacketType.Play.Server.*;

import java.util.HashSet;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class EntityHider implements Listener {
    protected HashSet<Integer> EntityMap = new HashSet<>();
    @SuppressWarnings("deprecation")
    
    private static final PacketType[] ENTITY_PACKETS = {
            ENTITY_EQUIPMENT, BED, ANIMATION, NAMED_ENTITY_SPAWN,
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
        plugin.getServer().getPluginManager().registerEvents(bukkitListener = constructBukkit(), plugin);
        manager.addPacketListener(protocolListener = constructProtocol(plugin));
    }
    
    public void hideEntity(Entity entity) {
    	EntityMap.add(entity.getEntityId());
        return;
    }
    
    public void removeEntity(Entity entity) {
    	EntityMap.remove(entity.getUniqueId());
    }
    
    private Listener constructBukkit() {
        return new Listener() {
            @EventHandler
            public void onEntityDeath(EntityDeathEvent e) {
                removeEntity(e.getEntity());
            }
            
            @EventHandler
            public void onChunkUnload(ChunkUnloadEvent e) {
                for (Entity entity : e.getChunk().getEntities()) {
                    removeEntity(entity);
                }
            }
            @EventHandler
            public void onChunkLoad(ChunkLoadEvent e) {
                for (Entity entity : e.getChunk().getEntities()) {
                    hideEntity(entity);
                }
            }
            
        };
    }
    
    private PacketAdapter constructProtocol(Plugin plugin) {
        return new PacketAdapter(plugin, ENTITY_PACKETS) {
            @Override
            public void onPacketSending(PacketEvent event) {
            	int index = event.getPacketType() == COMBAT_EVENT ? 1 : 0;
            	int id = event.getPacket().getIntegers().readSafely(index);
               	if (EntityMap.contains(id)) {
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