package me.wolfyscript.utilities.api.custom_items.meta;


import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;

public class PlayerHeadMeta extends Meta {

    public PlayerHeadMeta() {
        super("playerHead");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        if(option.equals(MetaSettings.Option.EXACT)){
            String valueOther = itemOther.getPlayerHeadValue();
            String value = item.getPlayerHeadValue();
            if(!valueOther.equals(value)){
                return false;
            }
        }
        itemOther.removePlayerHeadValue();
        item.removePlayerHeadValue();
        return true;
    }
}
