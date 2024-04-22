package one.oth3r.directionhud.common;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.utils.Helper.Enums;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.ArrayList;
import java.util.HashMap;

public class LoopManager {
    /**
     * anyone in the savePlayers list will have the playerData saved to file and removed from the list every second
     */
    private static final ArrayList<Player> savePlayers = new ArrayList<>();
    public static void addSavePlayer(Player player) {
        // only add if not already in
        if (!savePlayers.contains(player)) savePlayers.add(player);
    }
    public static void removeSavePlayer(Player player) {
        savePlayers.remove(player);
    }

    public static int rainbowF;
    private static int secondTick;
    private static int HUDTick;
    private static int ParticleTick;
    @SuppressWarnings("unchecked")
    public static void tick() {
        // tick the counters
        secondTick++;
        rainbowF += 10;
        HUDTick++;
        ParticleTick++;
        if (HUDTick >= config.HUDLoop) {
            HUDTick = 0;
            for (Player player : Utl.getPlayers()) {
                if ((boolean) player.getPData().getHud().getSetting(HUD.Setting.state)) {
                    HashMap<HUD.Module, ArrayList<String>> HUDData = HUD.build.getHUDInstructions(player);
                    // if the client has directionhud and the hud type is the actionBar send as a packet
                    if (DirectionHUD.clientPlayers.contains(player) &&
                            Enums.get(player.getPData().getHud().getSetting(HUD.Setting.type),HUD.Setting.DisplayType.class).equals(HUD.Setting.DisplayType.actionbar))
                        player.sendHUDPackets(HUDData);
                    else player.displayHUD(HUD.build.compile(player,HUDData));
                }
                // if player has DEST, AutoClear is on, and the distance is in the AutoClear range, clear
                if (Destination.dest.get(player).hasXYZ() && (boolean) player.getPData().getDEST().getSetting(Destination.Setting.autoclear) &&
                        Destination.dest.getDist(player) <= (int) player.getPData().getDEST().getSetting(Destination.Setting.autoclear_rad)) {
                    Destination.dest.clear(player, 2);
                }
            }
        }
        if (ParticleTick >= config.ParticleLoop) {
            ParticleTick = 0;
            for (Player player :Utl.getPlayers()) particles(player);
        }
        // reset the rainbow at 360
        if (rainbowF >= 360) rainbowF = 0;
        // tick every 2
        if (secondTick%2==0) {
            for (Player player : Utl.getPlayers()) {
                // only update the speed if the module and hud is on
                if ((boolean) player.getPData().getHud().getSetting(HUD.Setting.state) && player.getPData().getHud().getModule(HUD.Module.speed)) {
                    ArrayList<Double> pos = player.getVec();
                    ArrayList<Double> oldPos = (ArrayList<Double>) player.getPData().getDataMap().get("speed_data");
                    // replace with players current speed
                    player.getPData().getDataMap().put("speed_data", pos);
                    // only do x and y if 3d is off
                    if (!(boolean) player.getPData().getHud().getSetting(HUD.Setting.module__speed_3d)) {
                        pos.set(1,0.0);
                        oldPos.set(1,0.0);
                    }
                    // update the speed
                    player.getPData().getDataMap().put("speed", Utl.vec.distance(oldPos, pos) * 10);
                }
            }
        }
        // update the time every five ticks
        if (secondTick%5==0) Utl.setTime();
        // every 20 ticks
        if (secondTick >= 20) {
            secondTick = 0;
            for (Player player :Utl.getPlayers()) secondLoop(player);
            // save all the files for the players in the savePlayers list
            for (Player player : new ArrayList<>(savePlayers)) {
                PlayerData.toFile(player);
                removeSavePlayer(player);
            }
        }
    }
    private static void particles(Player player) {
        // spawn all the particles
        if (Destination.dest.get(player).hasXYZ()) {
            // destination particles
            if ((boolean) player.getPData().getDEST().getSetting(Destination.Setting.particles__dest)) {
                ArrayList<Double> destVec1 = Destination.dest.get(player).getVec(player);
                ArrayList<Double> destVec2 = new ArrayList<>(destVec1);
                destVec1.set(1,destVec1.get(1)+3);
                destVec2.set(1,destVec2.get(1)-3);
                Utl.particle.spawnLine(player, destVec1, destVec2, Utl.particle.DEST);
            }
            // line particles
            if ((boolean) player.getPData().getDEST().getSetting(Destination.Setting.particles__line))
                player.spawnParticleLine(Destination.dest.get(player).getVec(player),Utl.particle.LINE);
        }
        // track particles
        if ((boolean) player.getPData().getDEST().getSetting(Destination.Setting.particles__tracking)) {
            // make sure there's a target
            Player target = Destination.social.track.getTarget(player);
            if (target != null) {
                boolean sendParticles = true;
                ArrayList<Double> targetVec = target.getVec();
                if (!target.getDimension().equals(player.getDimension())) {
                    sendParticles = false;
                    // if convertible and autoconvert is enabled, send the particles
                    if (Dimension.canConvert(player.getDimension(), target.getDimension()) &&
                            (boolean) player.getPData().getDEST().getSetting(Destination.Setting.autoconvert)) {
                        sendParticles = true;
                        // update the vec to the converted loc
                        Loc targetLoc = target.getLoc();
                        targetLoc.convertTo(player.getDimension());
                        targetVec = targetLoc.getVec(player);
                    }
                }
                // actually send the particles
                if (sendParticles) player.spawnParticleLine(targetVec,Utl.particle.TRACKING);
            }
        }
    }
    private static void secondLoop(Player player) {
        DHUD.inbox.tick(player);
        // count down the social cooldown
        Double timer = player.getPData().getSocialCooldown();
        if (timer != null) {
            player.getPData().setSocialCooldown(timer-1);
            if (timer<=1) player.getPData().setSocialCooldown(null);
        }
        // tracker message logic
        Destination.social.track.logic(player);
    }
}