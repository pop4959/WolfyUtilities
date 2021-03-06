package me.wolfyscript.utilities.api.custom_items.meta;


import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RepairCostMeta extends Meta {

    public RepairCostMeta() {
        super("repairCost");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        ItemMeta metaOther = itemOther.getItemMeta();
        ItemMeta meta = item.getItemMeta();
        if (metaOther instanceof Repairable && meta instanceof Repairable) {
            switch (option) {
                case EXACT:
                    return ((Repairable) metaOther).getRepairCost() == ((Repairable) meta).getRepairCost();
                case IGNORE:
                    ((Repairable) metaOther).setRepairCost(0);
                    ((Repairable) meta).setRepairCost(0);
                    itemOther.setItemMeta(metaOther);
                    item.setItemMeta(meta);
                    return true;
                case LOWER:
                    return ((Repairable) metaOther).getRepairCost() < ((Repairable) meta).getRepairCost();
                case HIGHER:
                    return ((Repairable) metaOther).getRepairCost() > ((Repairable) meta).getRepairCost();
            }
        }
        return true;
    }
}
