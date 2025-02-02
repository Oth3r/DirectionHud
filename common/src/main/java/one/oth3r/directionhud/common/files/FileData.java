package one.oth3r.directionhud.common.files;

import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;

public class FileData {
    /**
     * directionHUD config file
     */
    private static final Config config = new Config();

    public static Config getConfig() {
        return new Config(config);
    }

    public static void setConfig(Config newConfig) {
        config.copyFileData(newConfig);
    }

    /**
     * module display data
     */
    private static final ModuleText moduleText = new ModuleText();

    public static ModuleText getModuleText() {
        return moduleText;
    }

    /**
     * global destinations
     */
    private static GlobalDest globalDestinations = new GlobalDest();

    public static GlobalDest getGlobal() {
        return globalDestinations;
    }

    public static void setGlobalDestinations(GlobalDest globalDestinations) {
        FileData.globalDestinations = new GlobalDest(globalDestinations);
    }

    public static void loadFiles() {
        config.load();
        PlayerData.loadDefaults();
        LangReader.loadLanguageFile();
        Dimension.loadDimensionSettings();
        moduleText.load();
        if (config.getDestination().getGlobal()) GlobalDest.load();
    }

    /**
     * clears the per-server data
     */
    public static void clearServerData() {
        globalDestinations = new GlobalDest();
    }
}
