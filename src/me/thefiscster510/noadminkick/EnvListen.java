package me.thefiscster510.noadminkick;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class EnvListen implements Listener{
	public static HashMap<Player, Vector> first = new HashMap<Player, Vector>();
	public static HashMap<Player, Vector> second = new HashMap<Player, Vector>();
	
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		
			File admins = new File(Main.plugin.getDataFolder() + File.separator + "Admins.yml");
			if(admins.exists()){
				if(isadmin(event.getPlayer())){
					Main.plugin.AdminOn = true;
					brod(event.getPlayer().getServer(), Main.plugin.getConfig().getString("Strings.AdminJoined"));
				}else{
					if(Main.plugin.AdminOn == true){
						//There is an admin on
					}else{
						if(event.getPlayer().hasPermission("noadminkicker.exempt")){
						
						}else{
							if(Main.plugin.getConfig().isSet("S-Area") && Main.plugin.getConfig().getString("S-Area").equalsIgnoreCase("true")){
								Location s = event.getPlayer().getLocation();
								 if(Main.plugin.getConfig().isSet("S-Spawn.X") && Main.plugin.getConfig().isSet("S-Spawn.Y") && Main.plugin.getConfig().isSet("S-Spawn.Z") && Main.plugin.getConfig().isSet("S-Spawn.YAW")){
									 s.setX(Main.plugin.getConfig().getDouble("S-Spawn.X"));
									 s.setY(Main.plugin.getConfig().getDouble("S-Spawn.Y"));
									 s.setZ(Main.plugin.getConfig().getDouble("S-Spawn.Z"));
									 s.setYaw(Float.parseFloat(Main.plugin.getConfig().getString("S-Spawn.YAW")));
									 event.getPlayer().teleport(s);
									 pmessage(event.getPlayer(), "&4 This area is designated for players when an admin is not on the game. While there are no admins on, you can player here.");
								 }else{
									 pmessage(event.getPlayer(), "&4S-Spawn not set.");
								 }
								 
							}else{
								kick(event.getPlayer(), Main.plugin.getConfig().getString("Strings.NoAdmin"));
							}
						}
					}
				}
			}else{
				kick(event.getPlayer(), Main.plugin.getConfig().getString("Strings.NoAdmin"));
			}
		}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event){

				boolean on = false;
				for(Player p : event.getPlayer().getServer().getOnlinePlayers()){
					if(isadmin(p)){
						if(!p.getName().equalsIgnoreCase(event.getPlayer().getName())){
							on = true;
						}
					}
				}
				if(on == true){
					//There is an admin on
				}else{
					Main.plugin.AdminOn = false;
					brod(event.getPlayer().getServer(), Main.plugin.getConfig().getString("Strings.AllGone"));
				}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer().hasPermission("noadminkicker.admintool")){
			
			if(event.getPlayer().getItemInHand().getType() == Material.BONE){
				if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
				   event.setCancelled(true);
				   int x =  event.getPlayer().getTargetBlock(null, 100).getX();
				   int y =  event.getPlayer().getTargetBlock(null, 100).getY();
				   int z =  event.getPlayer().getTargetBlock(null, 100).getZ();
				   Vector vec = new Vector(x, y ,z);
				   second.put(event.getPlayer(), vec);
				   pmessage(event.getPlayer(), "&2Second Position Set to X: " + x + ", Y: " + y + "Z: " + z);
				}else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
					event.setCancelled(true);
					int x =  event.getPlayer().getTargetBlock(null, 100).getX();
					int y =  event.getPlayer().getTargetBlock(null, 100).getY();
					int z =  event.getPlayer().getTargetBlock(null, 100).getZ();
					Vector vec = new Vector(x, y ,z);
					first.put(event.getPlayer(), vec);
					pmessage(event.getPlayer(), "&2First Position Set to X: " + x + ", Y: " + y + "Z: " + z);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		if(Main.plugin.getConfig().isSet("S-Area") && Main.plugin.getConfig().getString("S-Area").equalsIgnoreCase("true")){
			if(Main.plugin.AdminOn == true){
			
			}else{
			double x = event.getPlayer().getLocation().getX();
			double y = event.getPlayer().getLocation().getY();
			double z = event.getPlayer().getLocation().getZ();
			
			Vector max = gmax(Main.plugin.getConfig().getVector("Area.F"), Main.plugin.getConfig().getVector("Area.S"));
			Vector min = gmin(Main.plugin.getConfig().getVector("Area.F"), Main.plugin.getConfig().getVector("Area.S"));
			
			if(x >= min.getBlockX() && x <= max.getBlockX() && y >= min.getBlockY() && y <= max.getBlockY() && z >= min.getBlockZ() && z <= max.getBlockZ()){
				//They're in the zone
			}else{
				 Location s = event.getPlayer().getLocation();
				 s.setX(Main.plugin.getConfig().getDouble("S-Spawn.X"));
				 s.setY(Main.plugin.getConfig().getDouble("S-Spawn.Y"));
				 s.setZ(Main.plugin.getConfig().getDouble("S-Spawn.Z"));
				 s.setYaw(Float.parseFloat(Main.plugin.getConfig().getString("S-Spawn.YAW")));
				 event.getPlayer().teleport(s);
				 pmessage(event.getPlayer(), "&4You must stay inside the designated zone while an admin is not on the server.");
			}
			
		}
	}
		
		
	}
	
	public Vector gmax(Vector pos1, Vector pos2){
		return new Vector(Math.max(pos1.getX(), pos2.getX()),Math.max(pos1.getY(), pos2.getY()),Math.max(pos1.getZ(), pos2.getZ()));
	}
	
	public Vector gmin(Vector pos1, Vector pos2){
		return new Vector(Math.min(pos1.getX(), pos2.getX()),Math.min(pos1.getY(), pos2.getY()),Math.min(pos1.getZ(), pos2.getZ()));
	}
	
	public void kick(Player p, String message){
		p.kickPlayer(ChatColor.translateAlternateColorCodes('&', message));
	}
	public void pmessage(Player p, String message){
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	public void brod(Server s, String message){
		s.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	public boolean isadmin(Player p){
		File admins = new File(Main.plugin.getDataFolder() + File.separator + "Admins.yml");
		FileConfiguration admin = YamlConfiguration.loadConfiguration(admins);
		List<String> list = admin.getStringList("Admins");
		if(p.hasPermission("noadminkicker.isadmin") || list.contains(p.getName())){
			return true;
		}else{
			return false;
		}
	}
}
