package de.ancash.minecraft.serde.impl;

import static de.ancash.minecraft.serde.IItemTags.KNOWLEDGE_BOOK_RECIPES_TAG;
import static de.ancash.minecraft.serde.IItemTags.KNOWLEDGE_BOOK_TAG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import de.ancash.minecraft.serde.ItemDeserializer;
import de.ancash.minecraft.serde.ItemSerializer;

public class KnowledgeBookMetaSerDe implements IItemSerializer, IItemDeserializer {

	public static final KnowledgeBookMetaSerDe INSTANCE = new KnowledgeBookMetaSerDe();

	KnowledgeBookMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		KnowledgeBookMeta meta = (KnowledgeBookMeta) item.getItemMeta();
		if (meta.hasRecipes()) {
			map.put(KNOWLEDGE_BOOK_RECIPES_TAG,
					meta.getRecipes().stream().map(ItemSerializer.INSTANCE::serialize).collect(Collectors.toList()));
		}
		meta.setRecipes(null);
		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof KnowledgeBookMeta;
	}

	@Override
	public String getKey() {
		return KNOWLEDGE_BOOK_TAG;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		KnowledgeBookMeta meta = (KnowledgeBookMeta) item.getItemMeta();
		if (map.containsKey(KNOWLEDGE_BOOK_RECIPES_TAG))
			meta.setRecipes(((List<String>) map.get(KNOWLEDGE_BOOK_RECIPES_TAG)).stream()
					.map(ItemDeserializer.INSTANCE::deserializeNamespacedKey).collect(Collectors.toList()));
		item.setItemMeta(meta);
	}
}
