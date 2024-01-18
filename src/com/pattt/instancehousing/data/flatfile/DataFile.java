package com.pattt.instancehousing.data.flatfile;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;

@Getter
public class DataFile
{

	@Getter @Setter private File file;
	@Getter @Setter private FileConfiguration config;

	/*
	 * Constructor method to use specified variables and load in the file and the file configuration. 
	 */
	public DataFile(JavaPlugin javaPlugin, String name)
	{
		setFile(new File(javaPlugin.getDataFolder(), name));
		loadConfiguration();
	}

	/*
	 * Loads YamlConfiguration from the specified file variable above. 
	 */
	private void loadConfiguration()
	{
		setConfig(YamlConfiguration.loadConfiguration(getFile()));
	}

	/*
	 * Uses a try catch method to save the config and catches IOExceptiion.
	 */
	public void save()
	{
		try
		{
			getConfig().save(getFile());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}