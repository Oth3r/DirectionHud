package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FloodGateHandler {
    private static final String bBACK = btn("back");
    private static String lang(String key, Object... args) {
        return CUtl.lang(key,args).toString();
    }
    private static ArrayList<String> dims(Player player) {
        ArrayList<String> dim = (ArrayList<String>) Utl.dim.getList();
        dim.remove(player.getDimension());
        dim.add(0,player.getDimension());
        return dim;
    }
    private static String toggleText(boolean b) {
        return b?btn("on"):btn("off");
    }
    private static String btn(String key, Object... args) {
        return CUtl.TBtn(key, args).toString();
    }
    public static boolean isFloodgate(Player player) {
        return DirectionHUD.floodgatePlayers.get(player)!=null;
    }
    public static FloodgatePlayer getFGPlayer(Player player) {
        return DirectionHUD.floodgatePlayers.get(player);
    }
    public static class UI {
        public static void base(Player player) {
            SimpleForm.Builder builder = SimpleForm.builder().title("DirectionHUD");
            builder.button(btn("dest"));
            if (Utl.checkEnabled.hud(player)) builder.button(btn("hud"));
            builder.validResultHandler((form, response) -> {
                String button = response.clickedButton().text();
                if (button.equals(btn("dest"))) dest.base(player);
                if (button.equals(btn("hud"))) hud.base(player);
            });
            getFGPlayer(player).sendForm(builder);
        }
        public static class hud {
            public static void base(Player player) {
                SimpleForm.Builder builder = SimpleForm.builder().title(lang("hud"));
                builder.button(btn("hud.color"))
                        .button(btn("settings"))
                        .button(btn("hud.modules"))
                        .button(btn("back"));
                builder.validResultHandler((form, response) -> {
                    String button = response.clickedButton().text();
                    if (button.equals(btn("hud.color"))) {
                        //a
                    }
                    if (button.equals(btn("settings"))) {
                        //a
                    }
                    if (button.equals(btn("hud.modules"))) {
                        //a
                    }
                    if (button.equals(btn("back"))) UI.base(player);
                });
                getFGPlayer(player).sendForm(builder);
            }
        }
        public static class dest {
            public static void base(Player player) {
                //buttons
                String bSAVED = btn("dest.saved");
                String bSET = btn("dest.set");
                String bCLEAR = btn("dest.clear");
                String bLASTDEATH = btn("dest.lastdeath");
                String bSETTINGS = btn("settings");
                String bSEND = btn("dest.send");
                String bTRACK = btn("dest.track");
                SimpleForm.Builder builder = SimpleForm.builder().title(lang("dest.ui.dest"));
                if (Utl.checkEnabled.saving(player)) builder.button(bSAVED);
                builder.button(bSET);
                if (Destination.get(player).hasXYZ()) builder.button(bCLEAR);
                if (Utl.checkEnabled.lastdeath(player)) builder.button(bLASTDEATH);
                builder.button(bSETTINGS);
                if (Utl.checkEnabled.send(player)) builder.button(bSEND);
                if (Utl.checkEnabled.track(player)) builder.button(bTRACK);
                builder.button(bBACK);
                builder.validResultHandler((form, response) -> {
                    String button = response.clickedButton().text();
                    if (button.equals(bSAVED)) saved.base(player,1);
                    if (button.equals(bSET)) set(player);
                    if (button.equals(bCLEAR)) Destination.clear(player);
                    if (button.equals(bLASTDEATH)) lastdeath.base(player,1);
                    if (button.equals(bSETTINGS)) setting.base(player);
                    if (button.equals(bSEND)) send(player);
                    if (button.equals(bTRACK)) {
                        //a
                    }
                    if (button.equals(bBACK)) UI.base(player);
                });
                getFGPlayer(player).sendForm(builder);
            }
            public static void set(Player player) {
                CustomForm.Builder builder = CustomForm.builder().title(lang("dest.ui.set"));
                builder
                        .toggle(lang("ui.input.set_saved"))
                        .dropdown(lang("dest.ui.saved"), Destination.saved.getNames(player))
                        .input(lang("ui.input.location"), lang("ui.input.location.placeholder"))
                        .dropdown(lang("ui.input.dimension"),dims(player))
                        .toggle(lang("ui.input.convert"));
                builder.validResultHandler((response) -> {
                    boolean set_saved = response.asToggle();
                    int saved = response.asDropdown();
                    Loc loc = new Loc((String) response.next());
                    int dim = response.asDropdown();
                    boolean convert = response.asToggle();
                    if (set_saved) {
                        if (Destination.saved.getNames(player).size() > saved)
                            Destination.setName(player,Destination.saved.getNames(player).get(saved),convert);
                        else Destination.setName(player,"",convert);
                    } else {
                        loc.setDIM(dims(player).get(dim));
                        Destination.set(player,loc,convert);
                    }
                });
                getFGPlayer(player).sendForm(builder);
            }
            public static class saved {
                public static final int PER_PAGE = 5;
                public static void base(Player player, int pg) {
                    CUtl.PageHelper<List<String>> pageHelper = new CUtl.PageHelper<>(new ArrayList<>(Destination.saved.getList(player)),PER_PAGE);
                    SimpleForm.Builder builder = SimpleForm.builder().title(lang("dest.ui.saved")+" "+pg);
                    //buttons
                    String bADD = btn("dest.add");
                    String bLEFT = "<<<<";
                    String bRIGHT = ">>>>";
                    builder.button(bADD);
                    for (List<String> entry: pageHelper.getPage(pg)) {
                        Destination.saved.Dest dest = new Destination.saved.Dest(player, entry);
                        builder.button(dest.getName());
                    }
                    // add the nav buttons if necessary
                    if (pg > 1) builder.button(bLEFT);
                    if (pg < pageHelper.getTotalPages()) builder.button(bRIGHT);
                    builder.button(bBACK);
                    builder.validResultHandler((form, response) -> {
                        String button = response.clickedButton().text();
                        if (button.equals(bADD)) add(player);
                        if (Destination.saved.getNames(player).contains(button)) view(player,button);
                        if (button.equals(bLEFT)) base(player,pg-1);
                        if (button.equals(bRIGHT)) base(player,pg+1);
                        if (button.equals(btn("back"))) dest.base(player);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void view(Player player, String name) {
                    CUtl.PageHelper<List<String>> pageHelper = new CUtl.PageHelper<>(new ArrayList<>(Destination.saved.getList(player)),PER_PAGE);
                    Destination.saved.Dest dest = new Destination.saved.Dest(player, name);
                    //all the buttons
                    String bORDER = "#"+dest.getOrder();
                    String bNAME = dest.getName();
                    String bLOC = dest.getLoc().getBadge().toString();
                    String bCOLOR = dest.getColor();
                    String bSEND = btn("dest.send");
                    String bSET = btn("dest.set");
                    String bCONVERT = btn("dest.convert");
                    //build the buttons
                    SimpleForm.Builder builder = SimpleForm.builder().title(lang("dest"));
                    builder.button(bORDER).button(bNAME).button(bLOC).button(bCOLOR);
                    //SEND BUTTON
                    if (Utl.checkEnabled.send(player)) builder.button(bSEND);
                    //SET BUTTON
                    builder.button(bSET);
                    //CONVERT
                    if (Utl.dim.canConvert(player.getDimension(),dest.getLoc().getDIM())) builder.button(bCONVERT);
                    builder.button(bBACK);
                    builder.validResultHandler((form, response) -> {
                        String button = response.clickedButton().text();
                        if (button.equals(bORDER)) editOrder(player,name);
                        if (button.equals(bNAME)) editName(player,name);
                        if (button.equals(bLOC)) editLoc(player,name);
                        if (button.equals(bCOLOR)) editColor(player,name);
                        if (button.equals(bSEND)) sendSaved(player,name);
                        if (button.equals(bSET)) Destination.setName(player,name,false);
                        if (button.equals(bCONVERT)) Destination.setName(player,name,true);
                        if (button.equals(bBACK)) base(player,pageHelper.getPageOf(dest.getDest()));
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void editOrder(Player player, String name) {
                    Destination.saved.Dest dest = new Destination.saved.Dest(player, name);
                    // get all the names, and make an arraylist from 1-MAX
                    List<String> steps = new ArrayList<>();
                    for (int i = 0;i < Destination.saved.getNames(player).size();i++) steps.add(String.valueOf(i+1));
                    CustomForm.Builder builder = CustomForm.builder().title(lang("dest.ui.saved.edit"));
                    builder.stepSlider(lang("ui.input.order"),steps);
                    builder.validResultHandler((response) -> {
                        // order is off by 1, adjust
                        int order = response.asStepSlider()+1;
                        dest.setOrder(order);
                        view(player,name);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void editName(Player player, String name) {
                    Destination.saved.Dest dest = new Destination.saved.Dest(player, name);
                    CustomForm.Builder builder = CustomForm.builder().title(lang("dest.ui.saved.edit"));
                    builder.input(lang("ui.input.name"), lang("ui.input.name.placeholder"));
                    builder.validResultHandler((response) -> {
                        String newName = response.next();
                        // if null or empty, just return
                        if (!(newName == null || newName.equals(""))) dest.setName(newName);
                        view(player,dest.getName());
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void editLoc(Player player, String name) {
                    Destination.saved.Dest dest = new Destination.saved.Dest(player, name);
                    CustomForm.Builder builder = CustomForm.builder().title(lang("dest.ui.saved.edit"));
                    builder.input(lang("ui.input.location"), lang("ui.input.location.placeholder"))
                            .dropdown(lang("ui.input.dimension"),dims(player));
                    builder.validResultHandler((response) -> {
                        // if null or empty, make the variables what they already were to begin with
                        String xyz = response.next();
                        if (xyz == null || xyz.equals("")) xyz = player.getLoc().getXYZ();
                        Loc loc = new Loc(xyz,dims(player).get(response.asDropdown()));
                        dest.setLoc(loc);
                        view(player,name);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void editColor(Player player, String name) {
                    Destination.saved.Dest dest = new Destination.saved.Dest(player, name);
                    CustomForm.Builder builder = CustomForm.builder().title(lang("dest.ui.saved.edit"));
                    builder.input(lang("ui.input.color"), lang("ui.input.color.placeholder"));
                    builder.validResultHandler((response) -> {
                        // if null or empty, just return
                        String color = response.next();
                        if (!(color == null || color.equals(""))) {
                            color = CUtl.color.format(color);
                            dest.setColor(color);
                        }
                        view(player,name);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void sendSaved(Player player, String name) {
                    Destination.saved.Dest dest = new Destination.saved.Dest(player, name);
                    CustomForm.Builder builder = CustomForm.builder().title(lang("dest.ui.saved.edit"));
                    List<String> players = Utl.getPlayersEx(player);
                    builder.dropdown(lang("ui.input.player"),players);
                    builder.validResultHandler((response) -> {
                        Destination.social.send(player, players.get(response.asDropdown()),dest.getLoc(),dest.getName(),dest.getColor());
                    });
                    getFGPlayer(player).sendForm(builder);
                }
            }
            public static class setting {
                private static final String bEDIT = btn("dest.edit");
                public static void base(Player player) {
                    //buttons
                    String bDESTINATION = lang("dest.ui.dest");
                    String bPARTICLES = lang("dest.ui.settings.particles");
                    String bFEATURES = lang("dest.ui.settings.features");
                    SimpleForm.Builder builder = SimpleForm.builder().title(lang("dest.ui.settings"));
                    builder.button(bDESTINATION).button(bPARTICLES).button(bFEATURES).button(bBACK);
                    builder.validResultHandler((form, response) -> {
                        String button = response.clickedButton().text();
                        if (button.equals(bDESTINATION)) destination(player);
                        if (button.equals(bPARTICLES)) particles(player);
                        if (button.equals(bFEATURES)) features(player);
                        if (button.equals(bBACK)) dest.base(player);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void destination(Player player) {
                    //buttons
                    String bAUTOCLEAR = lang("dest.settings."+Destination.Settings.autoclear);
                    String bAUTOCONVERT = lang("dest.settings."+Destination.Settings.autoconvert);
                    String bYLEVEL = lang("dest.settings."+Destination.Settings.ylevel);
                    SimpleForm.Builder builder = SimpleForm.builder().title(lang("dest.ui.settings"));
                    builder.button(bAUTOCLEAR).button(bAUTOCONVERT).button(bYLEVEL).button(bBACK);
                    builder.validResultHandler((form, response) -> {
                        String button = response.clickedButton().text();
                        if (button.equals(bAUTOCLEAR))
                            viewSetting(player, Destination.Settings.autoclear,
                                    CUtl.lang("dest.settings." + Destination.Settings.autoclear)
                                            .append(": ").append(toggleText((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.autoclear))),
                                    CUtl.lang("dest.settings." + Destination.Settings.autoclear + ".info"),
                                    CUtl.lang("dest.settings." + Destination.Settings.autoclear + ".info_2"),
                                    CTxT.of(""),
                                    CUtl.lang("dest.settings." + Destination.Settings.autoclear_rad)
                                            .append(": ").append(String.valueOf(((Double)PlayerData.get.dest.setting.get(player, Destination.Settings.autoclear_rad)).intValue())),
                                    CUtl.lang("dest.settings." + Destination.Settings.autoclear_rad + ".info"));
                        if (button.equals(bAUTOCONVERT))
                            viewSetting(player, Destination.Settings.autoconvert,
                                    CUtl.lang("dest.settings." + Destination.Settings.autoconvert)
                                            .append(": ").append(toggleText((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.autoconvert))),
                                    CUtl.lang("dest.settings." + Destination.Settings.autoconvert + ".info"),
                                    CUtl.lang("dest.settings." + Destination.Settings.autoconvert + ".info_2"));
                        if (button.equals(bYLEVEL))
                            viewSetting(player, Destination.Settings.ylevel,
                                    CUtl.lang("dest.settings." + Destination.Settings.ylevel)
                                            .append(": ").append(toggleText((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.ylevel))),
                                    CUtl.lang("dest.settings." + Destination.Settings.ylevel + ".info",
                                            CUtl.lang("dest.settings." + Destination.Settings.ylevel + ".info_2"),
                                            CUtl.lang("dest.settings." + Destination.Settings.ylevel + ".info_2")));
                        if (button.equals(bBACK)) base(player);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void particles(Player player) {
                    //buttons
                    String bDEST = lang("dest.settings."+Destination.Settings.particles__dest);
                    String bLINE = lang("dest.settings."+Destination.Settings.particles__line);
                    String bTRACKING = lang("dest.settings."+Destination.Settings.particles__tracking);
                    SimpleForm.Builder builder = SimpleForm.builder().title(lang("dest.ui.settings"));
                    builder.button(bDEST).button(bLINE).button(bTRACKING).button(bBACK);
                    builder.validResultHandler((form, response) -> {
                        String button = response.clickedButton().text();
                        if (button.equals(bDEST))
                            viewSetting(player, Destination.Settings.particles__dest,
                                    CUtl.lang("dest.settings." + Destination.Settings.particles__dest)
                                            .append(": ").append(toggleText((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__dest))),
                                    CUtl.lang("dest.settings." + Destination.Settings.particles__dest + ".info"),
                                    CTxT.of(""),
                                    CUtl.lang("ui.input.color").append(": ")
                                            .append(CTxT.of((String)PlayerData.get.dest.setting.get(player,Destination.Settings.particles__dest_color))));
                        if (button.equals(bLINE))
                            viewSetting(player, Destination.Settings.particles__line,
                                    CUtl.lang("dest.settings." + Destination.Settings.particles__line)
                                            .append(": ").append(toggleText((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__line))),
                                    CUtl.lang("dest.settings." + Destination.Settings.particles__line + ".info"),
                                    CTxT.of(""),
                                    CUtl.lang("ui.input.color").append(": ")
                                            .append(CTxT.of((String)PlayerData.get.dest.setting.get(player,Destination.Settings.particles__line_color))));
                        if (button.equals(bTRACKING))
                            viewSetting(player, Destination.Settings.particles__tracking,
                                    CUtl.lang("dest.settings." + Destination.Settings.particles__tracking)
                                            .append(": ").append(toggleText((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__tracking))),
                                    CUtl.lang("dest.settings." + Destination.Settings.particles__tracking + ".info"),
                                    CTxT.of(""),
                                    CUtl.lang("ui.input.color").append(": ")
                                            .append(CTxT.of((String)PlayerData.get.dest.setting.get(player,Destination.Settings.particles__tracking_color))));
                        if (button.equals(bBACK)) base(player);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void features(Player player) {
                    //buttons
                    String bSEND = lang("dest.settings."+Destination.Settings.features__send);
                    String bTRACK = lang("dest.settings."+Destination.Settings.features__track);
                    String bLASTDEATH = lang("dest.settings."+Destination.Settings.features__lastdeath);
                    SimpleForm.Builder builder = SimpleForm.builder().title(lang("dest.ui.settings"));
                    builder.button(bSEND).button(bTRACK).button(bLASTDEATH).button(bBACK);
                    builder.validResultHandler((form, response) -> {
                        String button = response.clickedButton().text();
                        if (button.equals(bSEND))
                            viewSetting(player, Destination.Settings.features__send,
                                    CUtl.lang("dest.settings." + Destination.Settings.features__send)
                                            .append(": ").append(toggleText((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__send))),
                                    CUtl.lang("dest.settings." + Destination.Settings.features__send+".info",
                                            CUtl.lang("dest.settings."+Destination.Settings.features__send+".info_1"),
                                            CUtl.lang("dest.settings."+Destination.Settings.features__send+".info_2"),
                                            CUtl.lang("dest.settings."+Destination.Settings.features__send+".info_3")));
                        if (button.equals(bTRACK))
                            viewSetting(player, Destination.Settings.features__track,
                                    CUtl.lang("dest.settings." + Destination.Settings.features__track)
                                            .append(": ").append(toggleText((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__track))),
                                    CUtl.lang("dest.settings." + Destination.Settings.features__track + ".info"),
                                    CTxT.of(""),
                                    CUtl.lang("dest.settings." + Destination.Settings.features__track_request_mode)
                                            .append(": ").append(lang("dest.settings."+Destination.Settings.features__track_request_mode+"."+PlayerData.get.dest.setting.get(player, Destination.Settings.features__track_request_mode))),
                                    CUtl.lang("dest.settings." + Destination.Settings.features__track_request_mode+"."+config.DESTTrackingRequestModes.instant).append(": ")
                                            .append(CUtl.lang("dest.settings."+Destination.Settings.features__track_request_mode+"."+config.DESTTrackingRequestModes.instant+".info")),
                                    CUtl.lang("dest.settings." + Destination.Settings.features__track_request_mode+"."+config.DESTTrackingRequestModes.request).append(": ")
                                            .append(CUtl.lang("dest.settings."+Destination.Settings.features__track_request_mode+"."+config.DESTTrackingRequestModes.request+".info")));
                        if (button.equals(bLASTDEATH))
                            viewSetting(player, Destination.Settings.particles__tracking,
                                    CUtl.lang("dest.settings." + Destination.Settings.particles__tracking)
                                            .append(": ").append(toggleText((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__tracking))),
                                    CUtl.lang("dest.settings." + Destination.Settings.particles__tracking + ".info"),
                                    CTxT.of(""),
                                    CUtl.lang("ui.input.color").append(": ")
                                            .append(CTxT.of((String)PlayerData.get.dest.setting.get(player,Destination.Settings.particles__tracking_color))));
                        if (button.equals(bBACK)) base(player);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void viewSetting(Player player, Destination.Settings setting,  CTxT... message) {
                    ModalForm.Builder builder = ModalForm.builder().title(lang("dest.settings."+setting));
                    // build the text that gives info about the setting
                    StringBuilder content = new StringBuilder();
                    for (CTxT txt:message) content.append(txt.toString()).append("\n");
                    builder.content(content.toString()).button1(bEDIT).button2(bBACK);
                    builder.validResultHandler((response) -> {
                        String button = response.clickedButtonText();
                        if (button.equals(bEDIT)) editSetting(player,setting);
                        if (button.equals(bBACK)) {
                            if (Destination.Settings.dest().contains(setting)) destination(player);
                            if (Destination.Settings.particles().contains(setting)) particles(player);
                            if (Destination.Settings.features().contains(setting)) features(player);
                        }
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void editSetting(Player player, Destination.Settings setting) {
                    CustomForm.Builder builder = CustomForm.builder().title(lang("dest.ui.saved.edit"));
                    builder.toggle(lang("ui.input.reset"));
                    builder.toggle(lang("dest.settings."+setting),(boolean)PlayerData.get.dest.setting.get(player,setting));
                    if (setting.equals(Destination.Settings.autoclear)) {
                        // get all the valid AutoClearRads, and make an arraylist from 1-MAX
                        List<String> steps = new ArrayList<>();
                        IntStream.range(1, 16).forEach(n -> steps.add(String.valueOf(n)));
                        builder.stepSlider(lang("dest.settings."+Destination.Settings.autoclear_rad),steps,
                                ((Double)PlayerData.get.dest.setting.get(player, Destination.Settings.autoclear_rad)).intValue()-1);
                    }
                    // COLORS
                    if (Destination.Settings.colors().contains(Destination.Settings.get(setting+"_color"))) {
                        builder.input(lang("ui.input.color"),lang("ui.input.color.placeholder"),
                                (String)PlayerData.get.dest.setting.get(player,Destination.Settings.get(setting+"_color")));
                    }
                    // track req
                    if (setting.equals(Destination.Settings.features__track)) {
                        // long code but list the options and set the default to the current selected option
                        builder.dropdown(lang("dest.settings."+Destination.Settings.features__track_request_mode),
                                config.DESTTrackingRequestModes.get((String)PlayerData.get.dest.setting.get(player, Destination.Settings.features__track_request_mode)).equals(config.DESTTrackingRequestModes.instant)?0:1,
                                lang("dest.settings." + Destination.Settings.features__track_request_mode+"."+config.DESTTrackingRequestModes.instant),
                                lang("dest.settings." + Destination.Settings.features__track_request_mode+"."+config.DESTTrackingRequestModes.request));
                    }
                    builder.validResultHandler((response) -> {
                        boolean reset = response.asToggle();
                        boolean toggle = response.asToggle();
                        PlayerData.set.dest.setting.set(player,setting,toggle);
                        // AutoClear
                        if (setting.equals(Destination.Settings.autoclear))
                            PlayerData.set.dest.setting.set(player,Destination.Settings.autoclear_rad,response.asStepSlider()+1);
                        // COLORS
                        if (Destination.Settings.colors().contains(Destination.Settings.get(setting+"_color")))
                            PlayerData.set.dest.setting.set(player,Destination.Settings.get(setting+"_color"),
                                    CUtl.color.format(response.asInput(),(String)PlayerData.get.dest.setting.get(player,Destination.Settings.get(setting+"_color"))));
                        // TRACK REQUEST MODE
                        if (setting.equals(Destination.Settings.features__track))
                            PlayerData.set.dest.setting.set(player, Destination.Settings.features__track_request_mode,response.asDropdown()==0?config.DESTTrackingRequestModes.instant:config.DESTTrackingRequestModes.request);
                        if (reset) Destination.setting.reset(player,setting,false);
                        if (Destination.Settings.dest().contains(setting)) destination(player);
                        if (Destination.Settings.particles().contains(setting)) particles(player);
                        if (Destination.Settings.features().contains(setting)) features(player);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
            }
            public static class lastdeath {
                public static final int PER_PAGE = 5;
                public static void base(Player player, int pg) {
                    CUtl.PageHelper<String> pageHelper = new CUtl.PageHelper<>(PlayerData.get.dest.getLastdeaths(player),PER_PAGE);
                    SimpleForm.Builder builder = SimpleForm.builder().title(lang("dest.ui.lastdeath") + " " + pg);
                    //buttons
                    String bLEFT = "<<<<";
                    String bRIGHT = ">>>>";
                    for (String entry : pageHelper.getPage(pg))
                        builder.button(new Loc(entry).getBadge().toString());
                    // add the nav buttons if necessary
                    if (pg > 1) builder.button(bLEFT);
                    if (pg < pageHelper.getTotalPages()) builder.button(bRIGHT);
                    builder.button(bBACK);
                    builder.validResultHandler((form, response) -> {
                        String button = response.clickedButton().text();
                        for (String entry : pageHelper.getPage(pg)) {
                            String btn = new Loc(entry).getBadge().toString();
                            if (button.equals(btn)) view(player,entry);
                        }
                        if (button.equals(bLEFT)) base(player, pg - 1);
                        if (button.equals(bRIGHT)) base(player, pg + 1);
                        if (button.equals(btn("back"))) dest.base(player);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void view(Player player, String death) {
                    CUtl.PageHelper<String> pageHelper = new CUtl.PageHelper<>(PlayerData.get.dest.getLastdeaths(player),PER_PAGE);
                    Loc loc = new Loc(death);
                    //all the buttons
                    String bADD = btn("dest.add");
                    String bSET = btn("dest.set");
                    String bCONVERT = btn("dest.convert");
                    //build the buttons
                    SimpleForm.Builder builder = SimpleForm.builder().title(loc.getBadge().toString());
                    builder.button(bADD).button(bSET);
                    if (Utl.dim.canConvert(player.getDimension(),loc.getDIM())) builder.button(bCONVERT);
                    builder.button(bBACK);
                    builder.validResultHandler((form, response) -> {
                        String button = response.clickedButton().text();
                        if (button.equals(bADD)) add(player, death);
                        if (button.equals(bSET)) Destination.set(player, loc, false);
                        if (button.equals(bCONVERT)) Destination.set(player, loc, true);
                        if (button.equals(bBACK)) base(player, pageHelper.getPageOf(death));
                    });
                    getFGPlayer(player).sendForm(builder);
                }
                public static void add(Player player, String death) {
                    Loc loc = new Loc(death);
                    CustomForm.Builder builder = CustomForm.builder().title(loc.getBadge().toString());
                    builder.input(lang("ui.input.name"), lang("ui.input.name.placeholder"))
                            .input(lang("ui.input.color"), lang("ui.input.color.placeholder"));
                    builder.validResultHandler((response) -> {
                        String name = response.next();
                        // if null or empty, just return
                        if (name == null || name.equals("")) name = "name";
                        String color = CUtl.color.format(response.next());
                        Destination.saved.add(true,player,name,loc,color);
                    });
                    getFGPlayer(player).sendForm(builder);
                }
            }
            public static void add(Player player) {
                CustomForm.Builder builder = CustomForm.builder().title(lang("dest.ui.add"));
                builder
                        .input(lang("ui.input.name"), lang("ui.input.name.placeholder"))
                        .input(lang("ui.input.location"), lang("ui.input.location.placeholder"))
                        .dropdown(lang("ui.input.dimension"),dims(player))
                        .input(lang("ui.input.color"), lang("ui.input.color.placeholder"));
                builder.validResultHandler((response) -> {
                    // if responses are empty, set to default
                    String name = response.next();
                    if (name == null || name.equals("")) name = "name";
                    String xyz = response.next();
                    if (xyz == null || xyz.equals("")) xyz = player.getLoc().getXYZ();
                    Loc loc = new Loc(xyz,dims(player).get(response.asDropdown()));
                    String color = CUtl.color.format(response.next());
                    Destination.saved.add(true,player,name,loc,color);
                });
                getFGPlayer(player).sendForm(builder);
            }
            public static void send(Player player) {
                CustomForm.Builder builder = CustomForm.builder().title(lang("dest.ui.send"));
                List<String> players = Utl.getPlayersEx(player);
                builder.dropdown(lang("ui.input.player"),players);
                builder.input(lang("ui.input.name"), lang("ui.input.name.placeholder"))
                        .input(lang("ui.input.location"), lang("ui.input.location.placeholder"))
                        .dropdown(lang("ui.input.dimension"),dims(player))
                        .input(lang("ui.input.color"), lang("ui.input.color.placeholder"));
                builder.validResultHandler((response) -> {
                    String targetP = players.get(response.asDropdown());
                    // if responses are empty, set to default
                    String name = response.next();
                    if (name == null || name.equals("")) name = null;
                    String xyz = response.next();
                    if (xyz == null || xyz.equals("")) xyz = player.getLoc().getXYZ();
                    Loc loc = new Loc(xyz,dims(player).get(response.asDropdown()));
                    String color = CUtl.color.format(response.next());
                    Destination.social.send(player,targetP,loc,name,color);
                });
                getFGPlayer(player).sendForm(builder);
            }
        }
    }
}
