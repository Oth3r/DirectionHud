package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.*;

public class HUD {
    public static int minute;
    public static int hour;
    public static String weatherIcon = "?";
    public static class commandExecutor {
        public static void logic(Player player, String[] args) {
            if (!Utl.checkEnabled.hud(player)) return;
            if (args.length == 0) {
                UI(player,null);
                return;
            }
            String type = args[0].toLowerCase();
            String[] trimmedArgs = Utl.trimStart(args, 1);
            switch (type) {
                case "preset" -> presetCMD(player,trimmedArgs);
                case "edit" -> orderCMD(player, trimmedArgs);
                case "color" -> colorCMD(player, trimmedArgs);
                case "toggle" -> toggleCMD(player, trimmedArgs);
                default -> player.sendMessage(CUtl.error(CUtl.lang("error.command")));
            }
        }
        public static void presetCMD(Player player, String[] args) {
            if (args.length != 3) return;
            CUtl.color.presetUI(player,args[0],"/"+args[1].replace("_"," "),"/"+args[2].replace("_"," "));
        }
        public static void orderCMD(Player player, String[] args) {
            //UI
            if (args.length == 0)
                order.UI(player, null, null);
            //RESET
            if (args.length == 1 && args[0].equals("reset"))
                order.reset(player, true);
            if (args.length != 3) return;
            String type = args[0].toLowerCase();
            switch (type) {
                case "move" -> order.move(player, args[1], args[2], true);
                case "state" -> order.toggle(player, args[1], Boolean.parseBoolean(args[2]), true);
                case "setting" -> order.setting(player, args[1], args[2], true);
            }
        }
        public static void colorCMD(Player player, String[] args) {
            if (args.length == 0) {
                color.UI(player, null);
                return;
            }
            //COLOR
            if (args.length == 3 && args[0].equals("edit")) {
                if (args[2].equals("primary")) color.changeUI(player, args[1], args[2], null);
                if (args[2].equals("secondary")) color.changeUI(player, args[1], args[2], null);
            }
            //RESET
            if (args[0].equals("reset")) {
                if (args.length == 1) color.reset(player,null,null,true);
                if (args.length == 3) color.reset(player,args[1],args[2],true);
            }
            if (args[0].equals("preset") && args.length == 3) {
                color.presets(player,args[1],args[2]);
            }
            //CHANGE COLOR
            if (args.length < 4) return;
            String type = args[0].toLowerCase();
            switch (type) {
                case "set" -> color.setColor(player, args[1], args[2], args[3], true);
                case "bold" -> color.setBold(player, args[1], args[2], Boolean.parseBoolean(args[3]), true);
                case "italics" -> color.setItalics(player, args[1], args[2], Boolean.parseBoolean(args[3]), true);
                case "rgb" -> color.setRGB(player, args[1], args[2], Boolean.parseBoolean(args[3]), true);
            }
        }
        public static void toggleCMD(Player player, String[] args) {
            if (args.length == 0) toggle(player, null, false);
            if (args.length != 1) return;
            toggle(player, Boolean.parseBoolean(args[0]), true);
        }
    }
    public static class commandSuggester {
        public static ArrayList<String> logic(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Utl.checkEnabled.hud(player)) return suggester;
            if (pos == 1) {
                suggester.add("edit");
                suggester.add("color");
                suggester.add("toggle");
            }
            if (pos == 5 && args[1].equals("set")) {
                suggester.add("#ffffff");
            }
            if (pos == args.length) return Utl.formatSuggestions(suggester,args);
            return suggester;
        }
    }
    private static CTxT lang(String key, Object... args) {
        return CUtl.lang("hud."+key, args);
    }
    public static void build(Player player) {
        ArrayList<String> coordinates = new ArrayList<>();
        coordinates.add("pXYZ: ");
        coordinates.add("s"+player.getBlockX()+" "+player.getBlockY()+" "+player.getBlockZ());
        ArrayList<String> destination = new ArrayList<>();
        ArrayList<String> distance = new ArrayList<>();
        ArrayList<String> tracking = new ArrayList<>();
        if (Destination.get(player).hasXYZ()) {
            destination.add("pDEST: ");
            destination.add("s"+ Destination.get(player).getXYZ());
            distance.add("p[");
            distance.add("s"+ Destination.getDist(player));
            distance.add("p]");
        }
        if (PlayerData.get.dest.getTracking(player) != null) {
            tracking.add("/p[");
            tracking.add("s"+getTracking(player));
            tracking.add("/p]");
        }
        ArrayList<String> direction = new ArrayList<>();
        direction.add("p"+getPlayerDirection(player));
        ArrayList<String> time = new ArrayList<>();
        if (PlayerData.get.hud.setting.time24h(player)) {
            time.add("s"+getGameTime(false));
        } else {
            time.add("s"+getGameTime(true)+" ");
            time.add("p"+getAMPM());
        }
        ArrayList<String> weather = new ArrayList<>();
        weather.add("p"+weatherIcon);
        HashMap<String, ArrayList<String>> modules = new HashMap<>();
        modules.put("coordinates", coordinates);
        modules.put("distance", distance);
        modules.put("destination", destination);
        modules.put("direction", direction);
        modules.put("time", time);
        modules.put("weather", weather);
        modules.put("tracking", tracking);
        int start = 1;
        CTxT msg = CTxT.of("");
        for (int i=0; i < order.getEnabled(player).size(); i++) {
            if (!Destination.get(player).hasXYZ()) {
                if (modules.get(order.getEnabled(player).get(i)).equals(destination) ||
                        modules.get(order.getEnabled(player).get(i)).equals(distance)) continue;
            }
            for (String str : modules.get(order.getEnabled(player).get(i))) {
                String string = str.substring(1);
                boolean strike = false;
                if (str.charAt(0) == '/') {
                    str = str.substring(1);
                    string = string.substring(1);
                    strike = true;
                }
                if (str.charAt(0) == 'p') {
                    msg.append(color.addColor(player,string,1, LoopManager.rainbowF+start,5)
                            .strikethrough(strike));
                    if (color.getHUDColor(player,1).equals("rainbow"))
                        start = start + (string.replaceAll("\\s", "").length()*5);
                } else if (str.charAt(0) == 's') {
                    msg.append(color.addColor(player,string,2, LoopManager.rainbowF+start,5)
                            .strikethrough(strike));
                    if (color.getHUDColor(player,2).equals("rainbow"))
                        start = start + (string.replaceAll("\\s", "").length()*5);
                }
            }
            if (i-1 < order.getEnabled(player).size()) msg.append(" ");
        }
        if (msg.equals(CTxT.of(""))) return;
        msg.cEvent(3,"https://modrinth.com/mod/directionhud");
        player.buildHUD(msg);
    }
    public static String getPlayerDirection(Player player) {
        double rotation = (player.getYaw() - 180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return "N";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        } else {
            return "?";
        }
    }
    public static String getGameTime(boolean t12hr) {
        int hr = hour;
        String min = "0" + minute;
        min = min.substring(min.length() - 2);
        int minute = Integer.parseInt(min);
        if (t12hr) {
            String time = "";
            if(hr == 0) hr = 12;
            else if(hr > 12) {hr -= 12;}
            time += hr;
            time += ":";
            if(minute < 10) time += "0";
            time += minute;
            return time;
        }
        return hr + ":" + min;
    }
    public static String getAMPM() {
        int hr = hour;
        String ampm = "AM";
        if(hr > 12) {ampm = "PM";}
        else if(hr == 12) ampm = "PM";
        return ampm;
    }
    public static String getTracking(Player player) {
        if (PlayerData.get.dest.getTracking(player) == null) return "";
        Player pl = Destination.social.track.getTarget(player);
        if (pl == null) return "???";
        Loc plLoc = new Loc(pl);
        if (!player.getDimension().equals(pl.getDimension())) {
            if (Utl.dim.canConvert(player.getDimension(),pl.getDimension())) {
                plLoc.convertTo(player.getDimension());
            } else return "-?-";
        }
        int x = plLoc.getX()-player.getBlockX();
        int z = (plLoc.getZ()-player.getBlockZ())*-1;
        double rotation = (player.getYaw() - 180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        double d = Math.toDegrees(Math.atan2(x, z));
        if (d < 0) d = d + 360;
        if (Utl.inBetweenD(rotation, Utl.sub(d, 15, 360), (d+15)%360)) return "-"+Assets.symbols.up+"-";
        if (Utl.inBetweenD(rotation, d, (d+65)%360)) return Assets.symbols.left+Assets.symbols.up+"-";
        if (Utl.inBetweenD(rotation, d, (d+115)%360)) return Assets.symbols.left+"--";
        if (Utl.inBetweenD(rotation, d, (d+165)%360)) return Assets.symbols.left+Assets.symbols.down+"-";
        if (Utl.inBetweenD(rotation, Utl.sub(d, 65, 360), d)) return "-"+Assets.symbols.up+Assets.symbols.right;
        if (Utl.inBetweenD(rotation, Utl.sub(d, 115, 360), d)) return "--"+Assets.symbols.right;
        if (Utl.inBetweenD(rotation, Utl.sub(d, 165, 360), d)) return "-"+Assets.symbols.down+Assets.symbols.right;
        return "-"+Assets.symbols.down+"-";
    }
    public static class order {
        //has to be lowercase
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public static boolean validCheck(String s) {
            if (s.equals("coordinates")) return true;
            if (s.equals("distance")) return true;
            if (s.equals("destination")) return true;
            if (s.equals("direction")) return true;
            if (s.equals("tracking")) return true;
            if (s.equals("time")) return true;
            return s.equals("weather");
        }
        public static void reset(Player player, boolean Return) {
            PlayerData.set.hud.order(player, config.HUDOrder);
            PlayerData.set.hud.setting.time24h(player, config.HUD24HR);
            PlayerData.set.hud.setModule(player, PlayerData.defaults.hudModule());
            CTxT msg = CUtl.tag().append(lang("module.reset",CUtl.TBtn("reset").color('c')));
            if (Return) UI(player, msg, null);
            else player.sendMessage(msg);
        }
        public static void move(Player player, String module, String direction, boolean Return) {
            ArrayList<String> order = getEnabled(player);
            int pos = order.indexOf(module.toLowerCase());
            if (!validCheck(module)) return;
            CTxT msg = CUtl.tag().append(lang("module.move",CTxT.of(langName(module)).color(CUtl.s()),lang("module.move_"+direction)));
            if (direction.equals("down")) {
                if (pos == order.size() - 1) return;
                order.remove(pos);
                order.add(pos + 1, module);
                order.addAll(getDisabled(player));
                setOrderC(player, order);
            } else if (direction.equals("up")) {
                if (pos == 0) return;
                order.remove(pos);
                order.add(pos - 1, module);
                order.addAll(getDisabled(player));
                setOrderC(player, order);
            } else return;
            if (Return) UI(player, msg, module);
            else player.sendMessage(msg);
        }
        public static void toggle(Player player, String module, boolean toggle, boolean Return) {
            if (!validCheck(module)) return;
            CTxT msg = CUtl.tag().append(lang("module.toggle",CUtl.TBtn(toggle ? "on" : "off"),CTxT.of(langName(module)).color(CUtl.s())));
            //OFF
            if (!toggle && moduleState(player, module)) removeModule(player, module);
                //ON
            else if (toggle && !moduleState(player, module)) addModule(player, module);
            if (Return) UI(player, msg, module);
            else player.sendMessage(msg);
        }
        public static void setting(Player player, String setting, String option, boolean Return) {
            if (setting.equals("time")) {
                if (!PlayerData.get.hud.module.time(player)) return;
                if (!(option.equals("12hr") || option.equals("24hr"))) return;
                PlayerData.set.hud.setting.time24h(player, option.equals("24hr"));
                CTxT msg = CUtl.tag().append(lang("module.time.change",CUtl.lang("button.time."+option).color(CUtl.s())));
                if (Return) UI(player, msg, "time");
                else player.sendMessage(msg);
            }
        }
        public static boolean moduleState(Player player, String s) {
            if (s.equalsIgnoreCase("coordinates"))
                return PlayerData.get.hud.module.coordinates(player);
            if (s.equalsIgnoreCase("distance"))
                return PlayerData.get.hud.module.distance(player);
            if (s.equalsIgnoreCase("destination"))
                return PlayerData.get.hud.module.destination(player);
            if (s.equalsIgnoreCase("direction"))
                return PlayerData.get.hud.module.direction(player);
            if (s.equalsIgnoreCase("tracking"))
                return PlayerData.get.hud.module.tracking(player);
            if (s.equalsIgnoreCase("time"))
                return PlayerData.get.hud.module.time(player);
            if (s.equalsIgnoreCase("weather"))
                return PlayerData.get.hud.module.weather(player);
            return false;
        }
        public static String allModules() {
            return "coordinates distance tracking destination direction time weather";
        }
        public static String[] getOrderC(Player player) {
            return PlayerData.get.hud.order(player).split(" ");
        }
        public static void setOrderC(Player player, List<String> ls) {
            PlayerData.set.hud.order(player, String.join(" ", ls));
        }
        public static String fixOrder(String order) {
            ArrayList<String> list = new ArrayList<>(List.of(order.split(" ")));
            ArrayList<String> allModules = new ArrayList<>(List.of(allModules().split(" ")));
            list.removeIf(s -> !validCheck(s));
            for (String a: allModules) {
                if (Collections.frequency(list, a) > 1) list.remove(a);
            }
            allModules.removeAll(list);
            list.addAll(allModules);
            return String.join(" ", list);
        }
        public static ArrayList<String> getEnabled(Player player) {
            String[] order = getOrderC(player);
            ArrayList<String> list = new ArrayList<>();
            for (String s: order) {
                if (moduleState(player, s)) list.add(s);
            }
            return list;
        }
        public static ArrayList<String> getDisabled(Player player) {
            String[] order = getOrderC(player);
            ArrayList<String> list = new ArrayList<>();
            for (String s: order) {
                if (!moduleState(player, s)) list.add(s);
            }
            return list;
        }
        public static void removeModule(Player player, String s) {
            if (!validCheck(s)) return;
            ArrayList<String> order = getEnabled(player);
            ArrayList<String> orderD = getDisabled(player);
            if (!order.contains(s)) return;
            order.remove(s);
            orderD.add(s);
            PlayerData.set.hud.module.byName(player, s, false);
            order.addAll(orderD);
            setOrderC(player, order);
        }
        public static void addModule(Player player, String s) {
            if (!validCheck(s)) return;
            ArrayList<String> order = getEnabled(player);
            ArrayList<String> orderD = getDisabled(player);
            if (!orderD.contains(s)) return;
            orderD.remove(s);
            order.add(s);
            PlayerData.set.hud.module.byName(player, s, true);
            order.addAll(orderD);
            setOrderC(player, order);
        }
        public static CTxT arrow(boolean up, boolean gray, String name) {
            if (up) {
                if (gray) return CTxT.of(Assets.symbols.up).btn(true).color('7');
                return CTxT.of(Assets.symbols.up).btn(true).color(CUtl.p()).cEvent(1,"/hud edit move "+name+" up");
            }
            if (gray) return CTxT.of(Assets.symbols.down).btn(true).color('7');
            return CTxT.of(Assets.symbols.down).btn(true).color(CUtl.p()).cEvent(1,"/hud edit move "+name+" down");
        }
        public static CTxT xButton(String name) {
            return CTxT.of(Assets.symbols.x).btn(true).color('c').cEvent(1,"/hud edit state "+name+" false")
                    .hEvent(CUtl.TBtn("module.disable.hover").color('c'));
        }
        public static String langName(String s) {
            if (s.equalsIgnoreCase("coordinates")) return lang("module.coordinates").getString();
            if (s.equalsIgnoreCase("distance")) return lang("module.distance").getString();
            if (s.equalsIgnoreCase("destination")) return lang("module.destination").getString();
            if (s.equalsIgnoreCase("direction")) return lang("module.direction").getString();
            if (s.equalsIgnoreCase("tracking")) return lang("module.tracking").getString();
            if (s.equalsIgnoreCase("time")) return lang("module.time").getString();
            if (s.equalsIgnoreCase("weather")) return lang("module.weather").getString();
            return "";
        }
        public static CTxT moduleName(Player player, String s, CTxT addStart) {
            CTxT hoverT = CTxT.of("");
            if (s.equalsIgnoreCase("coordinates")) {
                hoverT.append(lang("module.coordinates.info")).append("\n")
                        .append(color.addColor(player,"XYZ: ",1,15,20))
                        .append(color.addColor(player,"0 0 0",2,95,20));
            }
            if (s.equalsIgnoreCase("distance")) {
                hoverT.append(lang("module.distance.info")).append("\n")
                        .append(color.addColor(player,"[",1,15,20))
                        .append(color.addColor(player,"0",2,35,20))
                        .append(color.addColor(player,"]",1,55,20));
            }
            if (s.equalsIgnoreCase("destination")) {
                hoverT.append(lang("module.destination.info")).append("\n")
                        .append(color.addColor(player,"DEST: ",1,15,20))
                        .append(color.addColor(player,"0 0 0",2,115,20));
            }
            if (s.equalsIgnoreCase("direction")) {
                hoverT.append(lang("module.direction.info")).append("\n")
                        .append(color.addColor(player,"N",1,15,20));
            }
            if (s.equalsIgnoreCase("tracking")) {
                hoverT.append(lang("module.tracking.info")).append("\n")
                        .append(color.addColor(player,"[",1,15,20).strikethrough(true))
                        .append(color.addColor(player,"-"+Assets.symbols.up+Assets.symbols.right,2,35,20))
                        .append(color.addColor(player,"]",1,55,20).strikethrough(true));
            }
            if (s.equalsIgnoreCase("time")) {
                if (PlayerData.get.hud.setting.time24h(player)) {
                    hoverT.append(lang("module.time.info")).append("\n")
                            .append(color.addColor(player,"22:22",1,15,20));
                } else {
                    hoverT.append(lang("module.time.info")).append("\n")
                            .append(color.addColor(player,"11:11 ",2,15,20))
                            .append(color.addColor(player,"AM",1,115,20));
                }
            }
            if (s.equalsIgnoreCase("weather")) {
                hoverT.append(lang("module.weather.info")).append("\n")
                        .append(color.addColor(player,Assets.symbols.sun,1,15,20));
            }
            if (addStart == null) return CTxT.of(langName(s)).hEvent(hoverT);
            return CTxT.of(langName(s)).hEvent(addStart.append("\n").append(hoverT));
        }
        public static void UI(Player player, CTxT abovemsg, String highlight) {
            if (highlight == null) highlight = "";
            //MODULES
            HashMap<String, CTxT> modules = new HashMap<>();
            modules.put("coordinates", CTxT.of(" "));
            modules.put("distance", CTxT.of(" "));
            modules.put("destination", CTxT.of(" "));
            modules.put("tracking", CTxT.of(" "));
            modules.put("direction", CTxT.of(" "));
            modules.put("time", CTxT.of(" "));
            modules.put("weather", CTxT.of(" "));
            //MAKE THE TEXT
            if (getEnabled(player).size() > 0) {
                for (int i = 0; i < getEnabled(player).size(); i++) {
                    String moduleName = getEnabled(player).get(i);
                    CTxT moduleNameText = moduleName(player, moduleName, null);
                    if (highlight.equals(moduleName)) moduleNameText.color(CUtl.s());
                    if (i == 0) {
                        modules.put(moduleName, modules.get(moduleName).append(arrow(true, true, moduleName)).append(" "));
                        modules.put(moduleName, modules.get(moduleName).append(xButton(moduleName)).append(" "));
                        //IF ONLY 1
                        if (getEnabled(player).size() == 1) modules.put(moduleName, modules.get(moduleName).append(arrow(false, true, moduleName)).append(" "));
                        else modules.put(moduleName, modules.get(moduleName).append(arrow(false, false, moduleName)).append(" "));
                    } else if (i == getEnabled(player).size() - 1) {
                        modules.put(moduleName, modules.get(moduleName).append(arrow(true, false, moduleName)).append(" ")
                                .append(xButton(moduleName)).append(" ").append(arrow(false, true, moduleName)).append(" "));
                    } else {
                        modules.put(moduleName, modules.get(moduleName).append(arrow(true, false, moduleName)).append(" ")
                                .append(xButton(moduleName)).append(" ").append(arrow(false, false, moduleName)).append(" "));
                    }
                    modules.put(moduleName,modules.get(moduleName).append(moduleNameText).append(" "));
                    //ADDING 12HR / 24HR
                    if (moduleName.equals("time")) {
                        boolean hr = PlayerData.get.hud.setting.time24h(player);
                        modules.put(moduleName, modules.get(moduleName)
                                .append(CUtl.TBtn("time."+(hr?"24hr":"12hr")).btn(true).color(CUtl.s())
                                        .cEvent(1,"/hud edit setting time "+(hr?"12hr":"24hr"))
                                        .hEvent(CUtl.TBtn("time.hover",CUtl.TBtn("time."+(hr?"12hr":"24hr")).color(CUtl.s())))));
                    }
                }
            }
            if (getDisabled(player).size() > 0) {
                for (int i = 0; i < getDisabled(player).size(); i++) {
                    String moduleName = getDisabled(player).get(i);
                    modules.put(moduleName, moduleName(player,moduleName,CUtl.lang("button.module.enable.hover").color('a')).color('7').cEvent(1,"/hud edit state "+moduleName+" true"));
                }
            }
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui.edit").color(Assets.mainColors.edit))
                    .append(CTxT.of("\n                                       \n").strikethrough(true));
            if (!getEnabled(player).isEmpty()) for (String s: getEnabled(player)) msg.append(modules.get(s)).append("\n");
            else msg.append(" ").append(lang("module.none").color('c')).append("\n ").append(lang("module.none_2").color('c')).append("\n");
            if (!getDisabled(player).isEmpty()) {
                msg.append(CTxT.of("                                       ").strikethrough(true)).append("\n")
                        .append(lang("ui.edit.disabled").color(Assets.mainColors.edit)).append("\n");
                CTxT disabled = CTxT.of("");
                int chars = 0;
                for (int i = 0; i < getDisabled(player).size(); i++) {
                    if (chars >= 20) {
                        chars = 0;
                        disabled.append("\n");
                    }
                    disabled.append(" ").append(modules.get(getDisabled(player).get(i)));
                    chars += modules.get(getDisabled(player).get(i)).getString().length()+1;
                }
                msg.append(disabled).append("\n");
            }
            msg.append("          ").append(CUtl.TBtn("reset").btn(true).color('c').cEvent(1,"/hud edit reset")
                            .hEvent(CUtl.TBtn("reset.hover_edit").color('c')))
                    .append("  ").append(CUtl.CButton.back("/hud"))
                    .append(CTxT.of("\n                                       ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static class color {
        public static void reset(Player player,String setting, String type, boolean Return) {
            if (type == null) {
                PlayerData.set.hud.primary(player, defaultFormat(1));
                PlayerData.set.hud.secondary(player, defaultFormat(2));
                type = "all";
            } else if (type.equals("primary")) {
                PlayerData.set.hud.primary(player, defaultFormat(1));
            } else if (type.equals("secondary")) {
                PlayerData.set.hud.secondary(player, defaultFormat(2));
            } else return;
            CTxT msg = CUtl.tag().append(lang("color.reset",CUtl.TBtn("reset").color('c'),lang("color."+type)));
            if (Return && type.equals("all")) UI(player,msg);
            else if (Return) changeUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static void presets(Player player, String setting, String type) {
            CUtl.color.presetUI(player,"custom","/hud color set "+setting+" "+type+" ","/hud color edit "+setting+" "+type);
        }
        public static void setColor(Player player, String setting, String type, String color, boolean Return) {
            color = color.toLowerCase();
            String ogColor = "#"+color;
            if (color.contains("#")) ogColor = color;
            if (type.equals("primary")) {
                color = CUtl.color.format(color,config.HUDPrimaryColor);
                if (!color.equals(ogColor)) {
                    player.sendMessage(CUtl.error(CUtl.lang("error.color",CTxT.of(ogColor).color(CUtl.s()))));
                    return;
                }
                PlayerData.set.hud.primary(player, color+"-"+getHUDBold(player,1)+"-"+getHUDItalics(player,1)+"-"+getHUDRGB(player,1));
            } else if (type.equals("secondary")) {
                color = CUtl.color.format(color,config.HUDSecondaryColor);
                if (!color.equals(ogColor)) {
                    player.sendMessage(CUtl.error(CUtl.lang("error.color",CTxT.of(ogColor).color(CUtl.s()))));
                    return;
                }
                PlayerData.set.hud.secondary(player, color+"-"+getHUDBold(player,2)+"-"+getHUDItalics(player,2)+"-"+getHUDRGB(player,2));
            } else return;
            CTxT msg = CUtl.tag().append(lang("color.set",lang("color."+type),CUtl.color.getBadge(color)));
            if (Return) changeUI(player,setting, type,msg);
            else player.sendMessage(msg);
        }
        public static void setBold(Player player, String setting, String type, boolean state, boolean Return) {
            if (type.equals("primary")) {
                if (getHUDBold(player, 1)==state) return;
                PlayerData.set.hud.primary(player, getHUDColor(player,1)+"-"+state+"-"+getHUDItalics(player,1)+"-"+getHUDRGB(player,1));
            } else if (type.equals("secondary")) {
                if (getHUDBold(player, 2)==state) return;
                PlayerData.set.hud.secondary(player, getHUDColor(player,2)+"-"+state+"-"+getHUDItalics(player,2)+"-"+getHUDRGB(player,2));
            } else return;
            CTxT msg = CUtl.tag().append(lang("color.set.bold",
                    CUtl.lang("button."+(state?"on":"off")).color(state?'a':'c'),lang("color."+type)));
            if (Return) changeUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static void setItalics(Player player, String setting, String type, boolean state, boolean Return) {
            if (type.equals("primary")) {
                if (getHUDItalics(player, 1)==state) return;
                PlayerData.set.hud.primary(player, getHUDColor(player,1)+"-"+getHUDBold(player,1)+"-"+state+"-"+getHUDRGB(player,1));
            } else if (type.equals("secondary")){
                if (getHUDItalics(player, 2)==state) return;
                PlayerData.set.hud.secondary(player, getHUDColor(player,2)+"-"+getHUDBold(player,2)+"-"+state+"-"+getHUDRGB(player,2));
            } else return;
            CTxT msg = CUtl.tag().append(lang("color.set.italics",
                    CUtl.lang("button."+(state?"on":"off")).color(state?'a':'c'),lang("color."+type)));
            if (Return) changeUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static void setRGB(Player player, String setting, String type, boolean state, boolean Return) {
            if (type.equals("primary")) {
                if (getHUDRGB(player, 1)==state) return;
                PlayerData.set.hud.primary(player, getHUDColor(player,1)+"-"+getHUDBold(player,1)+"-"+getHUDItalics(player,1)+"-"+state);
            } else if (type.equals("secondary")){
                if (getHUDRGB(player, 2)==state) return;
                PlayerData.set.hud.secondary(player, getHUDColor(player,2)+"-"+getHUDBold(player,2)+"-"+getHUDItalics(player,1)+"-"+state);
            } else return;
            CTxT msg = CUtl.tag().append(lang("color.set.rgb",
                    CUtl.lang("button."+(state?"on":"off")).color(state?'a':'c'),lang("color."+type)));
            if (Return) changeUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static String defaultFormat(int i) {
            if (i==1) return config.HUDPrimaryColor+"-"+ config.HUDPrimaryBold+"-"+ config.HUDPrimaryItalics+"-"+ config.HUDPrimaryRainbow;
            return config.HUDSecondaryColor+"-"+ config.HUDSecondaryBold+"-"+ config.HUDSecondaryItalics+"-"+ config.HUDSecondaryRainbow;
        }
        public static String getHUDColor(Player player, int i) {
            String[] p = PlayerData.get.hud.primary(player).split("-");
            String[] s = PlayerData.get.hud.secondary(player).split("-");
            if (i==1) return p[0];
            return s[0];
        }
        public static Boolean getHUDBold(Player player, int i) {
            String[] p = PlayerData.get.hud.primary(player).split("-");
            String[] s = PlayerData.get.hud.secondary(player).split("-");
            if (i==1) return Boolean.parseBoolean(p[1]);
            return Boolean.parseBoolean(s[1]);
        }
        public static Boolean getHUDItalics(Player player, int i) {
            String[] p = PlayerData.get.hud.primary(player).split("-");
            String[] s = PlayerData.get.hud.secondary(player).split("-");
            if (i==1) return Boolean.parseBoolean(p[2]);
            return Boolean.parseBoolean(s[2]);
        }
        public static Boolean getHUDRGB(Player player, int i) {
            String[] p = PlayerData.get.hud.primary(player).split("-");
            String[] s = PlayerData.get.hud.secondary(player).split("-");
            if (i==1) return Boolean.parseBoolean(p[3]);
            return Boolean.parseBoolean(s[3]);
        }
        public static CTxT addColor(Player player, String txt, int i, float start, float step) {
            if (getHUDRGB(player,i)) return CTxT.of(txt).rainbow(true,start,step).italic(getHUDItalics(player,i)).bold(getHUDBold(player,i));
            return CTxT.of(txt).color(getHUDColor(player,i)).italic(getHUDItalics(player,i)).bold(getHUDBold(player,i));
        }
        public static void changeUI(Player player, String setting, String type, CTxT abovemsg) {
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            CTxT back = CUtl.CButton.back("/hud color");
            int typ;
            if (type.equals("primary")) typ = 1;
            else if (type.equals("secondary")) typ = 2;
            else return;
            String currentColor = getHUDColor(player,typ);
            msg.append(" ").append(addColor(player,Utl.capitalizeFirst(lang("color."+type).getString()),typ,15,20))
                    .append(CTxT.of("\n                               \n").strikethrough(true));
            CTxT reset = CUtl.TBtn("reset").btn(true).color('c').cEvent(1, "/hud color reset "+setting+" "+type)
                    .hEvent(CUtl.lang("button.reset.hover_color_hud",addColor(player,lang("color."+type).getString().toUpperCase(),typ,15,20)));
            CTxT presetsButton = CUtl.TBtn("color.presets").color(Assets.mainColors.presets)
                    .cEvent(1,"/hud color preset "+setting+" "+type).btn(true)
                    .hEvent(CUtl.TBtn("color.presets.hover",CUtl.TBtn("color.presets.hover_2").color(Assets.mainColors.presets)));
            CTxT customButton = CUtl.TBtn("color.custom").btn(true).color(Assets.mainColors.custom)
                    .cEvent(2,"/hud color set "+setting+" "+type+" ")
                    .hEvent(CUtl.TBtn("color.custom.hover",CUtl.TBtn("color.custom.hover_2").color(Assets.mainColors.custom)));
            CTxT boldButton = CUtl.TBtn("color.bold").btn(true).color(getHUDBold(player, typ)?'a':'c')
                    .cEvent(1,"/hud color bold "+setting+" "+type+" "+(getHUDBold(player,typ)?"false":"true"))
                    .hEvent(CUtl.TBtn("color.bold.hover",CUtl.TBtn(getHUDBold(player,typ)?"off":"on").color(getHUDBold(player, typ)?'a':'c'),lang("color."+type)));
            CTxT italicsButton = CUtl.TBtn("color.italics").btn(true).color(getHUDItalics(player, typ)?'a':'c')
                    .cEvent(1,"/hud color italics "+setting+" "+type+" "+(getHUDItalics(player,typ)?"false":"true"))
                    .hEvent(CUtl.TBtn("color.italics.hover",CUtl.TBtn(getHUDItalics(player,typ)?"off":"on").color(getHUDItalics(player, typ)?'a':'c'),lang("color."+type)));
            CTxT rgbButton = CTxT.of(CUtl.color.rainbow(CUtl.TBtn("color.rgb").getString(),15,95)).btn(true).bold(getHUDRGB(player,typ))
                    .cEvent(1,"/hud color rgb "+setting+" "+type+" "+(getHUDRGB(player,typ)?"false":"true"))
                    .hEvent(CUtl.TBtn("color.rgb.hover",CUtl.TBtn(getHUDRGB(player,typ)?"off":"on").color(getHUDRGB(player, typ)?'a':'c'),lang("color."+type)));
            msg.append("   ")
                    .append(presetsButton).append(" ").append(customButton).append("\n\n")
                    .append(CUtl.color.colorEditor(currentColor,setting,"/hud color set "+setting+" "+type+" ","/hud color edit big "+type)).append("\n\n ")
                    .append(boldButton).append(" ").append(italicsButton).append(" ").append(rgbButton).append("\n\n     ")
                    .append(reset).append(" ").append(back)
                    .append(CTxT.of("\n                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static void UI(Player player, CTxT abovemsg) {
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(CUtl.color.rainbow(lang("ui.color").getString(),15f,45f))
                    .append(CTxT.of("\n                                \n").strikethrough(true)).append(" ");
            //PRIMARY
            msg.append(CTxT.of(addColor(player,CUtl.TBtn("color.primary").getString(),1,15,20)).btn(true).cEvent(1,"/hud color edit normal primary")
                    .hEvent(CUtl.TBtn("color.edit.hover",addColor(player,CUtl.TBtn("color.primary").getString(),1,15,20)))).append(" ");
            //SECONDARY
            msg.append(CTxT.of(addColor(player,CUtl.TBtn("color.secondary").getString(),2,15,20)).btn(true).cEvent(1,"/hud color edit normal secondary")
                    .hEvent(CUtl.TBtn("color.edit.hover",addColor(player,CUtl.TBtn("color.secondary").getString(),2,15,20)))).append("\n\n      ");
            //RESET
            msg.append(CUtl.TBtn("reset").btn(true).color('c').cEvent(1,"/hud color reset")
                            .hEvent(CUtl.TBtn("reset.hover_color",CUtl.TBtn("all").color('c'))))
                    .append("  ").append(CUtl.CButton.back("/hud"))
                    .append(CTxT.of("\n                                ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static void toggle(Player player, Boolean state, boolean Return) {
        PlayerData.set.hud.state(player, Objects.requireNonNullElseGet(state, () -> !PlayerData.get.hud.state(player)));
        player.updateHUD();
        CTxT msg = CUtl.tag().append(lang("toggle",CUtl.TBtn((PlayerData.get.hud.state(player)?"on":"off")).color(PlayerData.get.hud.state(player)?'a':'c')));
        if (Return) UI(player, msg);
        else player.sendMessage(msg);
    }
    public static void UI(Player player, CTxT abovemsg) {
        CTxT msg = CTxT.of("");
        if (abovemsg != null) msg.append(abovemsg).append("\n");
        msg.append(" ").append(lang("ui").color(CUtl.p())).append(CTxT.of("\n                                 \n").strikethrough(true)).append(" ");
        //COLOR
        msg.append(CUtl.CButton.hud.color()).append(" ");
        //EDIT
        msg.append(CUtl.CButton.hud.edit()).append(" ");
        //TOGGLE
        char color = 'c';
        String type = "false";
        if (!PlayerData.get.hud.state(player)) { type = "true"; color = 'a'; }
        msg.append(CUtl.CButton.hud.toggle(color, type)).append("\n\n ");
        //BACK
        msg.append(CUtl.CButton.back("/directionhud"));
        msg.append(CTxT.of("\n                                 ").strikethrough(true));
        player.sendMessage(msg);
    }
}
