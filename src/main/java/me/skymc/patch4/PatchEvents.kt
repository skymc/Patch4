package me.skymc.patch4

import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.kotlin.Reflex
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.concurrent.ConcurrentHashMap

@TListener
object PatchEvents : Listener {

    @PlayerContainer
    val bite = ConcurrentHashMap<String, Int>()

    @PlayerContainer
    val hook = ConcurrentHashMap<String, Entity>()

    @TPacket(type = TPacket.Type.SEND)
    fun e(player: Player, packet: Packet): Boolean {
        if (packet.any("PacketPlayOutEntityVelocity") && packet.read("a", 0) == bite[player.name]) {
            return false
        }
        if (packet.any("PacketPlayOutEntityMetadata") && packet.read("a", 0) == bite[player.name]) {
            return false
        }
        if (packet.any("PacketPlayOutEntityStatus") && packet.read("a", 0) == bite[player.name]) {
            return false
        }
        if (packet.any("PacketPlayOutEntityEffect") && packet.read("a", 0) == bite[player.name]) {
            return false
        }
        return true
    }

    @EventHandler
    fun e(e: PlayerInteractEvent) {
        if (e.action == Action.RIGHT_CLICK_BLOCK && CronusUtils.getUsingItem(e.player, Material.FISHING_ROD).type == Material.FISHING_ROD) {
            hook.remove(e.player.name)?.remove()
        }
    }

    @EventHandler
    fun e(e: PlayerFishEvent) {
        if (e.state == PlayerFishEvent.State.FISHING) {
            hook[e.player.name] = Reflex.Companion.of(e).invoke<Entity>("getHook")!!
        }
        if (e.state == PlayerFishEvent.State.BITE) {
            bite[e.player.name] = Reflex.Companion.of(e).invoke<Entity>("getHook")!!.entityId
            Tasks.delay(40) {
                bite.remove(e.player.name)
            }
        }
    }
}