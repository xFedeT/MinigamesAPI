package it.fedet.minigames.game;

import it.fedet.minigames.events.PlayerGameJoinEvent;
import it.fedet.minigames.events.PlayerGameQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.*;

public class GameListener implements Listener {
    
    private final GameService gameService;

    public GameListener(GameService gameService) {
        this.gameService = gameService;
    }

    /* -------------------------
       Block events (concrete)
       ------------------------- */
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockBreak(BlockBreakEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockPlace(BlockPlaceEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockDamage(BlockDamageEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockIgnite(BlockIgniteEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockFade(BlockFadeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockForm(BlockFormEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockFromTo(BlockFromToEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onLeavesDecay(LeavesDecayEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockGrow(BlockGrowEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockRedstoneChange(BlockRedstoneEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onSignChange(SignChangeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockBurn(BlockBurnEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockCanBuild(BlockCanBuildEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockDispense(BlockDispenseEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockExp(BlockExpEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockMultiPlace(BlockMultiPlaceEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockPhysics(BlockPhysicsEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockPistonExtend(BlockPistonExtendEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockPistonRetract(BlockPistonRetractEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBlockSpread(BlockSpreadEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityBlockForm(EntityBlockFormEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onNotePlay(NotePlayEvent event) { gameService.dispatchToGame(event); }

    /* -------------------------
       Entity events
       ------------------------- */
    @EventHandler(priority = EventPriority.MONITOR) public void onCreatureSpawn(CreatureSpawnEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityDamage(EntityDamageEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityDamageByEntity(EntityDamageByEntityEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityDeath(EntityDeathEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityTarget(EntityTargetEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityExplode(EntityExplodeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onProjectileLaunch(ProjectileLaunchEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onProjectileHit(ProjectileHitEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onFoodLevelChange(FoodLevelChangeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityCombust(EntityCombustEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityTame(EntityTameEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityChangeBlock(EntityChangeBlockEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityCombustByBlock(EntityCombustByBlockEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityCombustByEntity(EntityCombustByEntityEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityCreatePortal(EntityCreatePortalEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityDamageByBlock(EntityDamageByBlockEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityPortal(EntityPortalEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityPortalEnter(EntityPortalEnterEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityRegainHealth(EntityRegainHealthEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityShootBow(EntityShootBowEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityTeleport(EntityTeleportEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEntityUnleash(EntityUnleashEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onExpBottle(ExpBottleEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onExplosionPrime(ExplosionPrimeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onFireworkExplode(FireworkExplodeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onItemDespawn(ItemDespawnEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onItemMerge(ItemMergeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onItemSpawn(ItemSpawnEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPigZap(PigZapEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerDeath(PlayerDeathEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerLeash(PlayerLeashEntityEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPotionSplash(PotionSplashEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onSlimeSplit(SlimeSplitEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onSpawnerSpawn(SpawnerSpawnEvent event) { gameService.dispatchToGame(event); }

    /* -------------------------
       Player events
       ------------------------- */
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerJoin(PlayerJoinEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerQuit(PlayerQuitEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerKick(PlayerKickEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerMove(PlayerMoveEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerTeleport(PlayerTeleportEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerRespawn(PlayerRespawnEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerInteract(PlayerInteractEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerInteractEntity(PlayerInteractEntityEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerToggleSneak(PlayerToggleSneakEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerToggleSprint(PlayerToggleSprintEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onAsyncPlayerChat(AsyncPlayerChatEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerDropItem(PlayerDropItemEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerPickupItem(PlayerPickupItemEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerShearEntity(PlayerShearEntityEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerBucketFill(PlayerBucketFillEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerPortal(PlayerPortalEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerAnimation(PlayerAnimationEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerLevelChange(PlayerLevelChangeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerExpChange(PlayerExpChangeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerItemConsume(PlayerItemConsumeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerItemBreak(PlayerItemBreakEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerEditBook(PlayerEditBookEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerBedEnter(PlayerBedEnterEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerBedLeave(PlayerBedLeaveEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerChangedWorld(PlayerChangedWorldEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerChannel(PlayerChannelEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerEggThrow(PlayerEggThrowEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerFish(PlayerFishEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerItemHeld(PlayerItemHeldEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerLogin(PlayerLoginEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerPreLogin(PlayerPreLoginEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerToggleFlight(PlayerToggleFlightEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerVelocity(PlayerVelocityEvent event) { gameService.dispatchToGame(event); }

    /* -------------------------
       Inventory / Enchantment
       ------------------------- */
    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryClick(InventoryClickEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryClose(InventoryCloseEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryOpen(InventoryOpenEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryDrag(InventoryDragEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPrepareItemCraft(PrepareItemCraftEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onEnchantItem(EnchantItemEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPrepareItemEnchant(PrepareItemEnchantEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onBrew(BrewEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onCraftItem(CraftItemEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onFurnaceBurn(FurnaceBurnEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onFurnaceExtract(FurnaceExtractEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onFurnaceSmelt(FurnaceSmeltEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryCreative(InventoryCreativeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryMoveItem(InventoryMoveItemEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryPickupItem(InventoryPickupItemEvent event) { gameService.dispatchToGame(event); }

    /* -------------------------
       Vehicle / Hanging / Painting
       ------------------------- */
    @EventHandler(priority = EventPriority.MONITOR) public void onVehicleEnter(VehicleEnterEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onVehicleExit(VehicleExitEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onVehicleDamage(VehicleDamageEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onHangingPlace(HangingPlaceEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onHangingBreak(HangingBreakEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPaintingBreak(PaintingBreakEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPaintingPlace(PaintingPlaceEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onVehicleCreate(VehicleCreateEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onVehicleDestroy(VehicleDestroyEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onVehicleMove(VehicleMoveEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onVehicleUpdate(VehicleUpdateEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onHangingBreakByEntity(HangingBreakByEntityEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPaintingBreakByEntity(PaintingBreakByEntityEvent event) { gameService.dispatchToGame(event); }

    /* -------------------------
       World / Server / Weather
       ------------------------- */
    @EventHandler(priority = EventPriority.MONITOR) public void onChunkLoad(ChunkLoadEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onChunkUnload(ChunkUnloadEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onWorldSave(WorldSaveEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onWorldLoad(WorldLoadEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onWorldUnload(WorldUnloadEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onWeatherChange(WeatherChangeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onServerCommand(ServerCommandEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPluginDisable(PluginDisableEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPluginEnable(PluginEnableEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onLightningStrike(LightningStrikeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onThunderChange(ThunderChangeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onWorldInit(WorldInitEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPortalCreate(PortalCreateEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onSpawnChange(SpawnChangeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onStructureGrow(StructureGrowEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onMapInitialize(MapInitializeEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onRemoteServerCommand(RemoteServerCommandEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onServerListPing(ServerListPingEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onServiceRegister(ServiceRegisterEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onServiceUnregister(ServiceUnregisterEvent event) { gameService.dispatchToGame(event); }

    /* -------------------------
       Custom game events
       ------------------------- */
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerGameJoin(PlayerGameJoinEvent event) { gameService.dispatchToGame(event); }
    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerGameQuit(PlayerGameQuitEvent event) { gameService.dispatchToGame(event); }
    
    
}
