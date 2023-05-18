package de.ancash.nbtnexus;

import static de.ancash.nbtnexus.MetaTag.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.minecraft.IItemStack;
import de.ancash.minecraft.cryptomorin.xseries.XMaterial;
import de.ancash.minecraft.nbt.NBTCompound;
import de.ancash.minecraft.nbt.NBTItem;
import de.ancash.minecraft.nbt.NBTList;
import de.ancash.nbtnexus.command.EditCommand;
import de.ancash.nbtnexus.command.NBTNexusCommand;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.SerDeStructure;
import de.ancash.nbtnexus.serde.SerializedItem;
import de.ancash.nbtnexus.serde.handler.AxolotlBucketMetaSerDe;
import de.ancash.nbtnexus.serde.handler.BannerMetaSerDe;
import de.ancash.nbtnexus.serde.handler.BookMetaSerDe;
import de.ancash.nbtnexus.serde.handler.BundleMetaSerDe;
import de.ancash.nbtnexus.serde.handler.CompassMetaSerDe;
import de.ancash.nbtnexus.serde.handler.DamageableMetaSerDe;
import de.ancash.nbtnexus.serde.handler.FireworkEffectMetaSerDe;
import de.ancash.nbtnexus.serde.handler.FireworkMetaSerDe;
import de.ancash.nbtnexus.serde.handler.KnowledgeBookMetaSerDe;
import de.ancash.nbtnexus.serde.handler.LeatherArmorMetaSerDe;
import de.ancash.nbtnexus.serde.handler.MapMetaSerDe;
import de.ancash.nbtnexus.serde.handler.MusicInstrumentMetaSerDe;
import de.ancash.nbtnexus.serde.handler.PotionMetaSerDe;
import de.ancash.nbtnexus.serde.handler.RepairableMetaSerDe;
import de.ancash.nbtnexus.serde.handler.SkullMetaMetaSerDe;
import de.ancash.nbtnexus.serde.handler.SpawnEggMetaSerDe;
import de.ancash.nbtnexus.serde.handler.SuspiciousStewMetaSerDe;
import de.ancash.nbtnexus.serde.handler.TropicalFishBucketMetaSerDe;
import de.ancash.nbtnexus.serde.handler.UnspecificMetaSerDe;

@SuppressWarnings("deprecation")
public class NBTNexus extends JavaPlugin {

	@SuppressWarnings("nls")
	public static final String SPLITTER = "$";
	@SuppressWarnings("nls")
	public static final String SPLITTER_REGEX = "\\$";

//	private ProtocolManager protocolManager;
	private static NBTNexus singleton;
	private NBTNexusCommand cmd;
	private final SerDeStructure structure = new SerDeStructure();

	public SerDeStructure getStructure() {
		return structure.clone();
	}

	public void registerSerDeStructure(IItemSerDe iisd) {
		if (iisd.getStructure() == null)
			return;
		structure.put(iisd.getKey(), iisd.getStructure());
	}

	@Override
	public void onEnable() {
		singleton = this;
		structure.put(AMOUNT_TAG, NBTTag.BYTE);
		structure.put(XMATERIAL_TAG, NBTTag.STRING);
		registerSerDeStructure(AxolotlBucketMetaSerDe.INSTANCE);
		registerSerDeStructure(BannerMetaSerDe.INSTANCE);
		registerSerDeStructure(BookMetaSerDe.INSTANCE);
		registerSerDeStructure(BundleMetaSerDe.INSTANCE);
		registerSerDeStructure(CompassMetaSerDe.INSTANCE);
		registerSerDeStructure(DamageableMetaSerDe.INSTANCE);
		registerSerDeStructure(FireworkEffectMetaSerDe.INSTANCE);
		registerSerDeStructure(FireworkMetaSerDe.INSTANCE);
		registerSerDeStructure(KnowledgeBookMetaSerDe.INSTANCE);
		registerSerDeStructure(LeatherArmorMetaSerDe.INSTANCE);
		registerSerDeStructure(MapMetaSerDe.INSTANCE);
		registerSerDeStructure(MusicInstrumentMetaSerDe.INSTANCE);
		registerSerDeStructure(PotionMetaSerDe.INSTANCE);
		registerSerDeStructure(RepairableMetaSerDe.INSTANCE);
		registerSerDeStructure(SkullMetaMetaSerDe.INSTANCE);
		registerSerDeStructure(SpawnEggMetaSerDe.INSTANCE);
		registerSerDeStructure(SuspiciousStewMetaSerDe.INSTANCE);
		registerSerDeStructure(TropicalFishBucketMetaSerDe.INSTANCE);
		registerSerDeStructure(UnspecificMetaSerDe.INSTANCE);
		System.out.println(structure.getKeys(true));
		cmd = new NBTNexusCommand(this);
		cmd.addSubCommand(new EditCommand(this));
		getCommand("nbtn").setExecutor(cmd);
//		protocolManager = ProtocolLibrary.getProtocolManager();
//		protocolManager.addPacketListener(new SetSlotAdapter(this));
		getCommand("serde").setExecutor(this);
		getCommand("deserialize").setExecutor(this);
		ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
		item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 2);
		item.setAmount(12);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setLocalizedName("test");
		item.setItemMeta(meta);
		NBTItem nbt = new NBTItem(item);
		nbt.setBoolean("bool", true);
		nbt.setByte("byte", (byte) 12);
		nbt.setByteArray("bytearr", new byte[] { 1, 2, 3 });
		nbt.setDouble("dd", 12d);
		nbt.setFloat("f", 1.2f);
//		nbt.setUUID("uuid", UUID.randomUUID());
		nbt.setIntArray("intaarr", new int[] { 1, 3, 2 });
		nbt.setString("string", "str");
		nbt.setItemStack("is", new ItemStack(Material.BEDROCK, 64));
		NBTCompound comp = nbt.addCompound("test");
		comp.getFloatList("flist").addAll(Arrays.asList(Float.MAX_VALUE, Float.MIN_VALUE));
		comp.getDoubleList("dlist").addAll(Arrays.asList(Double.MAX_VALUE, Double.MIN_VALUE));
		comp.getIntegerList("ilist").addAll(Arrays.asList(Integer.MAX_VALUE, Integer.MIN_VALUE));
		comp.getLongList("llist").addAll(Arrays.asList(Long.MAX_VALUE, Long.MIN_VALUE));
		comp.getIntArrayList("intarrrlisst").addAll(Arrays.asList(new int[] { 1 }, new int[] { 2, 3 }));
		comp.getUUIDList("iiddlissst").addAll(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
		comp.getCompoundList("complist").addCompound().addCompound("lol").setString("hello", "there");
		comp.getCompoundList("complist").addCompound().addCompound("laa").getCompoundList("2").addCompound()
				.setString("w", "asdasds");
		NBTList<String> slist = comp.getStringList("blist");
		slist.add("1");
		slist.add("2");
		slist.add("3");
		slist = comp.getStringList("alist");
		slist.add("1");
		slist.add("2");
		slist.add("3");
		for (int a = 0; a < 10; a++) {
			comp.addCompound(String.valueOf(a)).setInteger(String.valueOf(a), a);
		}
		nbt.setItemStackArray("itemarr",
				new ItemStack[] { XMaterial.DIAMOND.parseItem(), XMaterial.OAK_LOG.parseItem() });
		item = nbt.getItem();

		for (Player p : Bukkit.getOnlinePlayers())
			p.getInventory().addItem(item);

		SerializedItem serialized = SerializedItem.of(item);
		try {
			System.out.println("orig: " + item);
			System.out.println("1. yaml: " + ItemSerializer.INSTANCE.serializeItemStackToYaml(item));
			System.out.println("2. yaml: " + ItemSerializer.INSTANCE.serializeItemStackToYaml(ItemDeserializer.INSTANCE
					.deserializeYamlToItemStack(ItemSerializer.INSTANCE.serializeItemStackToYaml(item))));
			System.out.println("1. json: " + ItemSerializer.INSTANCE.serializeItemStackToJson(item));
			System.out.println("2. json: " + ItemSerializer.INSTANCE.serializeItemStackToJson(ItemDeserializer.INSTANCE
					.deserializeJsonToItemStack(ItemSerializer.INSTANCE.serializeItemStackToJson(item))));
			System.out.println("alroundd: " + ItemDeserializer.INSTANCE.deserializeJsonToItemStack(
					ItemSerializer.INSTANCE.serializeItemStackToJson(ItemDeserializer.INSTANCE
							.deserializeYamlToItemStack(ItemSerializer.INSTANCE.serializeItemStackToYaml(item)))));
			System.out.println("yaml eq: " + ItemSerializer.INSTANCE.serializeItemStackToYaml(item)
					.equals(ItemSerializer.INSTANCE.serializeItemStackToYaml(ItemDeserializer.INSTANCE
							.deserializeYamlToItemStack(ItemSerializer.INSTANCE.serializeItemStackToYaml(item)))));
			System.out.println("json eq: " + ItemSerializer.INSTANCE.serializeItemStackToJson(item)
					.equals(ItemSerializer.INSTANCE.serializeItemStackToJson(ItemDeserializer.INSTANCE
							.deserializeJsonToItemStack(ItemSerializer.INSTANCE.serializeItemStackToJson(item)))));
			System.out.println("IItemStack eq: " + new IItemStack(item).isSimilar(ItemDeserializer.INSTANCE
					.deserializeYamlToItemStack(ItemSerializer.INSTANCE.serializeItemStackToYaml(item))));
			checkEnchantedBook();
			checkBook();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkBook() throws IOException {
		ItemStack item = XMaterial.WRITTEN_BOOK.parseItem();
		BookMeta meta = (BookMeta) item.getItemMeta();
		meta.setAuthor("§aAncash");
		meta.addPage("line 1", "line 2");
		meta.addPage("line 21", "line 22");
		meta.setTitle("titlaaa");
		item.setItemMeta(meta);
		System.out.println(item);
		System.out.println(ItemSerializer.INSTANCE.serializeItemStackToYaml(item));
		System.out.println(ItemDeserializer.INSTANCE
				.deserializeYamlToItemStack(ItemSerializer.INSTANCE.serializeItemStackToYaml(item)));
		System.out.println(ItemSerializer.INSTANCE.serializeItemStackToYaml(ItemDeserializer.INSTANCE
				.deserializeYamlToItemStack(ItemSerializer.INSTANCE.serializeItemStackToYaml(item))));
	}

	private void checkEnchantedBook() throws IOException {
		ItemStack item = XMaterial.ENCHANTED_BOOK.parseItem();
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 3);
		System.out.println(item);
		System.out.println(ItemSerializer.INSTANCE.serializeItemStackToYaml(item));
		System.out.println(ItemDeserializer.INSTANCE
				.deserializeYamlToItemStack(ItemSerializer.INSTANCE.serializeItemStackToYaml(item)));
		System.out.println(ItemSerializer.INSTANCE.serializeItemStackToYaml(ItemDeserializer.INSTANCE
				.deserializeYamlToItemStack(ItemSerializer.INSTANCE.serializeItemStackToYaml(item))));
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equals("serde")) {
			if (!(sender instanceof Player))
				return false;
			Player player = (Player) sender;
			ItemStack item = player.getItemInHand();
			try {
				if (item == null)
					return false;
				String yaml = ItemSerializer.INSTANCE.serializeItemStackToYaml(item.clone());
				System.out.println("orig: " + item);
				item = ItemDeserializer.INSTANCE.deserializeYamlToItemStack(yaml);
				String json = ItemSerializer.INSTANCE.serializeItemStackToJson(item);
				System.out.println("json: " + json);
				System.out.println("yaml: " + yaml);
				item = ItemDeserializer.INSTANCE.deserializeJsonToItemStack(json);
				System.out.println("serde: " + item);
				player.getInventory().addItem(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (command.getName().equals("deserialize")) {
			String data = String.join(" ", args);
			ItemStack item = null;
			try {
				item = ItemDeserializer.INSTANCE.deserializeJsonToItemStack(data);
				for (Player p : Bukkit.getOnlinePlayers())
					p.getInventory().addItem(item);
				System.out.println("yaml: " + ItemSerializer.INSTANCE.serializeItemStackToYaml(item));
				System.out.println("json: " + ItemSerializer.INSTANCE.serializeItemStackToJson(item));
			} catch (Exception e) {
				try {
					item = ItemDeserializer.INSTANCE.deserializeYamlToItemStack(data);
					for (Player p : Bukkit.getOnlinePlayers())
						p.getInventory().addItem(item);
					System.out.println("yaml: " + ItemSerializer.INSTANCE.serializeItemStackToYaml(item));
					System.out.println("json: " + ItemSerializer.INSTANCE.serializeItemStackToJson(item));
				} catch (Exception e2) {
					sender.sendMessage(e.getMessage());
					sender.sendMessage(e2.getMessage());
					return false;
				}
			}

			return true;
		}
		return true;
	}

	public static NBTNexus getInstance() {
		return singleton;
	}
}
