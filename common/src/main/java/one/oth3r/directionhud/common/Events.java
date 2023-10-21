package one.oth3r.directionhud.common;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import org.geysermc.floodgate.api.FloodgateApi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class Events {
    public static void serverStart() {
        DirectionHUD.configDir = DirectionHUD.CONFIG_DIR;
        DirectionHUD.playerData = DirectionHUD.PLAYERDATA_DIR;
        Path dirPath = Paths.get(DirectionHUD.playerData);
        try {
            Files.createDirectories(dirPath);
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("Failed to create playerdata directory:\n" + e.getMessage());
        }
        config.load();
        LangReader.loadLanguageFile();
        //load the config twice, first to load the lang and second to make all the comments the right language
        config.load();
    }
    public static void serverEnd() {
        for (Player player: Utl.getPlayers()) PlayerData.removePlayer(player);
        DirectionHUD.LOGGER.info("Safely shutdown!");
    }
    public static void playerJoin(Player player) {
        PlayerData.addPlayer(player);
        FloodgateApi floodgate = FloodgateApi.getInstance();
        UUID uuid = UUID.fromString(player.getUUID());
        if (floodgate.isFloodgatePlayer(uuid))
            DirectionHUD.floodgatePlayers.put(player,floodgate.getPlayer(uuid));
    }
    public static void playerLeave(Player player) {
        PlayerData.removePlayer(player);
        DirectionHUD.clientPlayers.remove(player);
        DirectionHUD.floodgatePlayers.remove(player);
        DirectionHUD.bossBarManager.removePlayer(player);
    }
    public static void playerChangeWorld(Player player, String fromDIM, String toDIM) {
        if (Destination.get(player).hasXYZ()) {
            Loc loc = Destination.get(player);
            if (Utl.dim.canConvert(toDIM, Destination.get(player).getDIM()) &&
                    (boolean)PlayerData.get.dest.setting.get(player,Destination.Settings.autoconvert)) {
                //DEST AutoConvert logic
                Loc cLoc = Destination.get(player);
                cLoc.convertTo(toDIM);
                Destination.silentSet(player,cLoc);
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.dest"))
                        .append("\n ").append(CUtl.lang("dest.autoconvert.dest.info",loc.getBadge(),cLoc.getBadge()).italic(true).color('7')));
            } else if ((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.autoclear)) {
                //DEST AutoClear logic
                CTxT msg = CTxT.of("").append(CUtl.lang("dest.changed.cleared.dim").color('7').italic(true))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+fromDIM));
                if (Utl.dim.canConvert(toDIM, Destination.get(player).getDIM()))
                    msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+fromDIM+" convert"));
                Destination.clear(player, msg);
            }
        }
    }
    public static void playerDeath(Player player, Loc death) {
        if (!config.deathsaving || !(boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__lastdeath)) return;
        Destination.lastdeath.add(player, death);
        CTxT msg = CUtl.tag().append(CUtl.lang("dest.lastdeath.save"))
                .append(" ").append(death.getBadge())
                .append(" ").append(CUtl.CButton.dest.set("/dest set "+death.getXYZ()+" "+death.getDIM()));
        if (Utl.dim.canConvert(player.getSpawnDimension(),death.getDIM()))
            msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+death.getXYZ()+" "+death.getDIM()+" convert"));
        player.sendMessage(msg);
    }
}