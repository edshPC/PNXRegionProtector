package Sergey_Dertan.SRegionProtector.Messenger;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Utils.Utils.*;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Messenger {

    public static final String DEFAULT_LANGUAGE = "eng";
    private static Messenger instance;
    public final String language;
    private final Map<String, String> messages;

    private boolean async;
    private boolean withNemisys;

    @SuppressWarnings("unchecked")
    public Messenger() throws Exception {
        String lang = null;
        if (new File(SRegionProtectorMain.MAIN_FOLDER + "config.yml").exists()) {
            Map<String, Object> cnf = new Config(SRegionProtectorMain.MAIN_FOLDER + "config.yml", Config.YAML).getAll();
            if (cnf.containsKey("language") && !((String) cnf.get("language")).equalsIgnoreCase("default")) {
                lang = (String) cnf.get("language");
            }
        }
        if (lang == null) {
            lang = Server.getInstance().getLanguage().getLang();
        }
        if (!resourceExists(lang + ".yml", "resources/lang", SRegionProtectorMain.class)) lang = DEFAULT_LANGUAGE;
        this.language = lang;
        copyResource(lang + ".yml", "resources/lang", SRegionProtectorMain.LANG_FOLDER, SRegionProtectorMain.class);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        this.messages = new Object2ObjectArrayMap<>((Map<String, String>) yaml.loadAs(Utils.readFile(new File(SRegionProtectorMain.LANG_FOLDER + lang + ".yml")), HashMap.class));
        instance = this;
    }

    public static Messenger getInstance() {
        return instance;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public void setWithNemisys(boolean withNemisys) {
        this.withNemisys = withNemisys;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getMessage(String message, String[] search, String[] replace) {
        String msg = this.messages.getOrDefault(message, message);
        if (search.length == replace.length) {
            for (int i = 0; i < search.length; ++i) {
                if (search[i] == null || replace[i] == null) continue;
                String var1 = search[i];
                if (var1.charAt(0) != '{') var1 = '{' + var1;
                if (var1.charAt(var1.length() - 1) != '}') var1 += '}';
                msg = msg.replace(var1, replace[i]);
            }
        }
        return msg;
    }

    public String getMessage(String message, String search, String replace) {
        return this.getMessage(message, new String[]{search}, new String[]{replace});
    }

    public String getMessage(String message) {
        return this.getMessage(message, new String[0], new String[0]);
    }

    public void sendMessage(CommandSender target, String message, String[] search, String[] replace, MessageType type) {
        if (!this.async || !(target instanceof Player) || this.withNemisys) {
            target.sendMessage(this.getMessage(message, search, replace));
        } else {
            TextPacket pk = new TextPacket();
            pk.type = type.id;
            pk.message = this.getMessage(message, search, replace);
            directDataPacket((Player) target, pk);
        }
    }

    public void sendMessage(CommandSender target, String message, String[] search, String[] replace) {
        this.sendMessage(target, message, search, replace, MessageType.MESSAGE);
    }

    public void sendMessage(CommandSender target, String message, String search, String replace) {
        this.sendMessage(target, message, search, replace, MessageType.MESSAGE);
    }

    public void sendMessage(CommandSender target, String message, String search, String replace, MessageType messageType) {
        this.sendMessage(target, message, new String[]{search}, new String[]{replace}, messageType);
    }

    public void sendMessage(CommandSender target, String message) {
        this.sendMessage(target, message, MessageType.MESSAGE);
    }

    public void sendMessage(CommandSender target, String message, MessageType messageType) {
        this.sendMessage(target, message, new String[0], new String[0], messageType);
    }

    public enum MessageType {
        MESSAGE(TextPacket.TYPE_RAW),
        TIP(TextPacket.TYPE_TIP),
        POPUP(TextPacket.TYPE_POPUP);

        public final byte id;

        MessageType(byte id) {
            this.id = id;
        }

        public static MessageType fromString(String name) {
            switch (name.toLowerCase()) {
                case "message":
                case "msg":
                default:
                    return MESSAGE;
                case "tip":
                    return TIP;
                case "pop":
                case "popup":
                    return POPUP;
            }
        }
    }
}
