package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.UI.Chest.ChestUIManager;
import Sergey_Dertan.SRegionProtector.UI.Form.FormUIManager;
import Sergey_Dertan.SRegionProtector.UI.UIType;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class OpenUICommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;
    private final ChunkManager chunkManager;
    private final UIType uiType;

    public OpenUICommand(RegionManager regionManager, ChunkManager chunkManager, UIType uiType) {
        super("rggui", "gui");

        this.regionManager = regionManager;
        this.chunkManager = chunkManager;
        this.uiType = uiType;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("guitarget", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.STRING, true)
                }
        );
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.gui.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.gui.in-game");
            return false;
        }
        if (args.length == 0) {
            Region region = this.chunkManager.getRegion(((Vector3) sender), ((Player) sender).level.getName());
            if (region == null) {
                this.messenger.sendMessage(sender, "command.gui.wrong-position");
                return false;
            }
            this.openUI((Player) sender, region);
        } else {
            Region region = this.regionManager.getRegion(args[0]);
            if (region == null) {
                this.messenger.sendMessage(sender, "command.gui.wrong-target", "@region", args[0]);
                return false;
            }
            this.openUI((Player) sender, region);
        }
        return true;
    }

    private void openUI(Player player, Region region) {
        if (!region.isLivesIn(player.getName()) && !player.hasPermission("sregionprotector.info.other") && !player.hasPermission("sregionprotector.admin")) {
            this.messenger.sendMessage(player, "command.gui.permission");
            return;
        }
        if (this.uiType == UIType.CHEST) {
            ChestUIManager.open(player, region);
        } else {
            FormUIManager.open(player, region);
        }
    }
}
