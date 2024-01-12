package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.Main.PNXRegionProtectorMain;

public final class SQLiteSettings {

    public final String databaseFile;

    SQLiteSettings(String databaseFile) {
        this.databaseFile = databaseFile.replace("{@plugin-folder}", PNXRegionProtectorMain.MAIN_FOLDER);
    }
}
