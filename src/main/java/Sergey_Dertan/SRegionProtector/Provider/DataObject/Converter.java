package Sergey_Dertan.SRegionProtector.Provider.DataObject;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.math.Vector3;
import com.google.gson.Gson;

import java.util.*;

public abstract class Converter {

    private Converter() {
    }

    public static RegionDataObject toDataObject(Region region) {
        RegionDataObject dataObject = new RegionDataObject();

        dataObject.name = region.name;

        dataObject.minX = region.minX;
        dataObject.minY = region.minY;
        dataObject.minZ = region.minZ;
        dataObject.maxX = region.maxX;
        dataObject.maxY = region.maxY;
        dataObject.maxZ = region.maxZ;

        dataObject.creator = region.getCreator();
        dataObject.level = region.level;

        dataObject.owners = new Gson().toJson(region.getOwners());
        dataObject.members = new Gson().toJson(region.getMembers());
        dataObject.priority = region.getPriority();
        return dataObject;
    }

    public static RegionDataObject toRegionDataObject(Map<String, Object> data) { //for the yaml data provider
        RegionDataObject dataObject = new RegionDataObject();

        dataObject.minX = ((Number) data.get(Tags.MIN_X_TAG)).doubleValue();
        dataObject.minY = ((Number) data.get(Tags.MIN_Y_TAG)).doubleValue();
        dataObject.minZ = ((Number) data.get(Tags.MIN_Z_TAG)).doubleValue();
        dataObject.maxX = ((Number) data.get(Tags.MAX_X_TAG)).doubleValue();
        dataObject.maxY = ((Number) data.get(Tags.MAX_Y_TAG)).doubleValue();
        dataObject.maxZ = ((Number) data.get(Tags.MAX_Z_TAG)).doubleValue();
        dataObject.name = (String) data.get(Tags.NAME_TAG);
        dataObject.level = (String) data.get(Tags.LEVEL_TAG);
        dataObject.creator = (String) data.get(Tags.CREATOR_TAG);
        dataObject.owners = new Gson().toJson(Arrays.asList(Utils.deserializeStringArray(((String) data.get(Tags.OWNERS_TAG)))));
        dataObject.members = new Gson().toJson(Arrays.asList(Utils.deserializeStringArray(((String) data.get(Tags.MEMBERS_TAG)))));
        dataObject.priority = ((Number) data.getOrDefault(Tags.PRIORITY_TAG, 0)).intValue();
        return dataObject;
    }

    public static FlagListDataObject toDataObject(RegionFlag[] flags, String region) {
        boolean[] state = new boolean[flags.length];
        for (int i = 0; i < flags.length; ++i) {
            state[i] = flags[i].state;
        }
        RegionTeleportFlag tpFlag = (RegionTeleportFlag) flags[RegionFlags.FLAG_TELEPORT];
        Map<String, Object> teleport = new HashMap<>(4);
        teleport.put(Tags.X_TAG, tpFlag.position != null ? tpFlag.position.x : 0);
        teleport.put(Tags.Y_TAG, tpFlag.position != null ? tpFlag.position.y : 0);
        teleport.put(Tags.Z_TAG, tpFlag.position != null ? tpFlag.position.z : 0);
        teleport.put(Tags.LEVEL_TAG, tpFlag.level);

        FlagListDataObject dataObject = new FlagListDataObject();
        dataObject.state = new Gson().toJson(state);
        dataObject.teleportData = new Gson().toJson(teleport);
        dataObject.sellData = ((RegionSellFlag) flags[RegionFlags.FLAG_SELL]).price;
        dataObject.region = region;
        return dataObject;
    }

    public static Region fromDataObject(RegionDataObject dataObject, RegionFlag[] flags) {
        return new Region(
                dataObject.name, dataObject.creator,
                dataObject.level,
                dataObject.priority,
                dataObject.minX, dataObject.minY, dataObject.minZ,
                dataObject.maxX, dataObject.maxY, dataObject.maxZ,
                new Gson().fromJson(dataObject.owners, String[].class),
                new Gson().fromJson(dataObject.members, String[].class),
                flags
        );
    }

    public static Region fromDataObject(RegionDataObject dataObject, FlagListDataObject flagsDataObject) {
        return fromDataObject(dataObject, fromDataObject(flagsDataObject).toArray(new RegionFlag[0]));
    }

    public static List<RegionFlag> fromDataObject(FlagListDataObject dataObject) {
        List<RegionFlag> flags = new ArrayList<>(RegionFlags.FLAG_AMOUNT);
        boolean[] state = new Gson().fromJson(dataObject.state, boolean[].class);
        @SuppressWarnings("unchecked")
        Map<String, Object> teleportData = (Map<String, Object>) new Gson().fromJson(dataObject.teleportData, Map.class);
        for (int i = 0; i < state.length; ++i) {
            if (i == RegionFlags.FLAG_TELEPORT) {
                double x = ((Number) teleportData.get(Tags.X_TAG)).doubleValue();
                double y = ((Number) teleportData.get(Tags.Y_TAG)).doubleValue();
                double z = ((Number) teleportData.get(Tags.Z_TAG)).doubleValue();
                String level = (String) teleportData.get(Tags.LEVEL_TAG);
                flags.add(new RegionTeleportFlag(state[i], new Vector3(x, y, z), level));
                continue;
            }
            if (i == RegionFlags.FLAG_SELL) {
                flags.add(new RegionSellFlag(state[i], dataObject.sellData));
                continue;
            }
            flags.add(new RegionFlag(state[i]));
        }
        return flags;
    }

    public static FlagListDataObject toDataObject(Map<String, Map<String, Object>> data) { //for the yaml data provider
        FlagListDataObject dataObject = new FlagListDataObject();
        boolean[] state = new boolean[RegionFlags.FLAG_AMOUNT];
        for (Map.Entry<String, Map<String, Object>> flag : data.entrySet()) {
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_INVALID) continue;
            state[RegionFlags.getFlagId(flag.getKey())] = (Boolean) flag.getValue().get(Tags.STATE_TAG);
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_SELL) {
                dataObject.sellData = ((Number) flag.getValue().getOrDefault(Tags.PRICE_TAG, -1L)).longValue();
            }
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_TELEPORT) {
                @SuppressWarnings("unchecked")
                Map<String, Object> teleportData = (Map<String, Object>) flag.getValue().getOrDefault(Tags.POSITION_TAG, new HashMap<>());
                Map<String, Object> teleport = new HashMap<>();
                teleport.put(Tags.X_TAG, teleportData.getOrDefault(Tags.X_TAG, 0));
                teleport.put(Tags.Y_TAG, teleportData.getOrDefault(Tags.Y_TAG, 0));
                teleport.put(Tags.Z_TAG, teleportData.getOrDefault(Tags.Z_TAG, 0));
                teleport.put(Tags.LEVEL_TAG, teleportData.getOrDefault(Tags.LEVEL_TAG, ""));
                dataObject.teleportData = new Gson().toJson(teleport);
            }
        }
        dataObject.state = new Gson().toJson(state);
        return dataObject;
    }
}
