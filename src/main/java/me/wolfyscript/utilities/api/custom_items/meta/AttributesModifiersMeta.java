package me.wolfyscript.utilities.api.custom_items.meta;


import com.google.common.collect.Multimap;
import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;

public class AttributesModifiersMeta extends Meta {

    public AttributesModifiersMeta() {
        super("attributes_modifiers");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        ItemMeta metaOther = itemOther.getItemMeta();
        ItemMeta meta = item.getItemMeta();
        if (option.equals(MetaSettings.Option.IGNORE)) {
            if (metaOther.hasAttributeModifiers()) {
                Multimap<Attribute, AttributeModifier> modifiers = metaOther.getAttributeModifiers();
                modifiers.keySet().forEach(metaOther::removeAttributeModifier);
            }
            if (meta.hasAttributeModifiers()) {
                Multimap<Attribute, AttributeModifier> modifiers = metaOther.getAttributeModifiers();
                modifiers.keySet().forEach(metaOther::removeAttributeModifier);
            }
        }
        itemOther.setItemMeta(metaOther);
        item.setItemMeta(meta);
        return true;
    }
}
