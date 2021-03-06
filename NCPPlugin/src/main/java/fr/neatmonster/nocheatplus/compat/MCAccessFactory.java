package fr.neatmonster.nocheatplus.compat;

import java.util.ArrayList;
import java.util.List;

import fr.neatmonster.nocheatplus.compat.bukkit.MCAccessBukkit;
import fr.neatmonster.nocheatplus.compat.cb2511.MCAccessCB2511;
import fr.neatmonster.nocheatplus.compat.cb2512.MCAccessCB2512;
import fr.neatmonster.nocheatplus.compat.cb2545.MCAccessCB2545;
import fr.neatmonster.nocheatplus.compat.cbdev.MCAccessCBDev;
import fr.neatmonster.nocheatplus.config.ConfPaths;
import fr.neatmonster.nocheatplus.config.ConfigManager;
import fr.neatmonster.nocheatplus.logging.LogUtil;

/**
 * Factory class to hide potentially dirty stuff.
 * @author mc_dev
 *
 */
public class MCAccessFactory {
	
	private final String[] updateLocs = new String[]{
		"[NoCheatPlus]  Check for updates at BukkitDev: http://dev.bukkit.org/server-mods/nocheatplus/",
		"[NoCheatPlus]  Development builds: http://nocheatplus.org:8080/job/NoCheatPlus/",
	};
	
	/**
	 * @throws RuntimeException if no access can be set.
	 * @return
	 */
	public MCAccess getMCAccess(){
		final List<Throwable> throwables = new ArrayList<Throwable>();
		final boolean bukkitOnly = ConfigManager.getConfigFile().getBoolean(ConfPaths.COMPATIBILITY_BUKKITONLY);
		
		// Try to set up native access.
		if (!bukkitOnly){
			// TEST //
			try{
				return new MCAccessCBDev();
			}
			catch(Throwable t){
				throwables.add(t);
			};
			// TEST END //
			
			// TODO: add 1.4.7 classes explicitly.
			
			try{
				return new MCAccessCB2545();
			}
			catch(Throwable t){
				throwables.add(t);
			};
			
			try{
				return new MCAccessCB2512();
			}
			catch(Throwable t){
				throwables.add(t);
			};
			
			try{
				return new MCAccessCB2511();
			}
			catch(Throwable t){
				throwables.add(t);
			};
		}
		
		// Try to set up api-only access.
		try{
			final String msg;
			if (bukkitOnly){
				msg = "[NoCheatPlus] The plugin is configured for Bukkit-API-only access.";
			}
			else{
				msg = "[NoCheatPlus] Could not set up native access for your specific Minecraft + server-mod version.";
			}
			LogUtil.logWarning(msg);
			LogUtil.logWarning("[NoCheatPlus] Some features will likely not function properly, performance might suffer.");
			for (String uMsg : updateLocs){
				LogUtil.logWarning(uMsg);
			}
			return new MCAccessBukkit();
		}
		catch(Throwable t){
			throwables.add(t);
		};
		
		// All went wrong.
		LogUtil.logSevere("[NoCheatPlus] Your version of NoCheatPlus does not seem to provide support for either your Minecraft version or your specific server-mod.");
		for (String msg : updateLocs){
			LogUtil.logSevere(msg);
		}
		LogUtil.logSevere("[NoCheatPlus] Could not set up MC version specific access.");
		for (Throwable t : throwables ){
			LogUtil.logSevere(t);
		}
		throw new RuntimeException("Could not set up access to Minecraft API.");
	}
}
