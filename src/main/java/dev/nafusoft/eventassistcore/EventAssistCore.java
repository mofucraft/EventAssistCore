/*
 * Copyright 2022 NAFU_at
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nafusoft.eventassistcore;

import dev.nafusoft.eventassistcore.automation.EventAutomationManager;
import dev.nafusoft.eventassistcore.automation.actions.*;
import dev.nafusoft.eventassistcore.database.DatabaseConnector;
import dev.nafusoft.eventassistcore.gameevent.GameEventManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class EventAssistCore extends JavaPlugin {
    private static EventAssistCore instance;
    private BukkitAudiences adventure;
    private ConfigLoader configLoader;
    private EventAssistConfig config;
    private DatabaseConnector connector;

    private GameEventManager eventManager;
    private EventAutomationManager automationManager;


    public static EventAssistCore getInstance() {
        if (instance == null)
            instance = (EventAssistCore) Bukkit.getServer().getPluginManager().getPlugin("EventAssistCore");
        return instance;
    }

    public EventAssistConfig getEventAssistConfig() {
        if (config == null)
            config = configLoader.getConfig();
        return config;
    }


    @Override
    public void onEnable() {
        // Load configuration
        saveDefaultConfig();
        configLoader = new ConfigLoader();
        configLoader.reloadConfig();

        // SQL Initialization
        connector = new DatabaseConnector(getEventAssistConfig().databaseType(),
                getEventAssistConfig().address() + ":" + getEventAssistConfig().port(),
                getEventAssistConfig().database(),
                getEventAssistConfig().username(),
                getEventAssistConfig().password());

        try {
            eventManager = new GameEventManager(config.tablePrefix(), connector);
            automationManager = new EventAutomationManager(config.tablePrefix(), connector);
        } catch (SQLException e) {
            connector.close();

            throw new IllegalStateException("""
                    The database storing event data could not be accessed.
                    Please check your configuration file to make sure your credentials are correct.
                    """, e);
        }

        // Initialize actions
        automationManager.getActionRegistry().registerAction(CommandExecuteAction.class, CommandExecuteActionOptions.class);
        automationManager.getActionRegistry().registerAction(TitleShowAction.class, TitleShowActionOptions.class);
        automationManager.getActionRegistry().registerAction(MessageSendAction.class, MessageSendActionOptions.class);
        automationManager.getActionRegistry().registerAction(SoundPlayAction.class, SoundPlayActionOptions.class);
        automationManager.getActionRegistry().registerAction(ItemGiveAction.class, ItemGiveActionOptions.class);
        automationManager.getActionRegistry().registerAction(TeleportAction.class, EmptyActionOptions.class);
        automationManager.getActionRegistry().registerAction(CountDownAction.class, EmptyActionOptions.class);

        // Initialize an audiences instance for the plugin
        this.adventure = BukkitAudiences.create(this);

        // イベント開始・終了の定期確認
        Bukkit.getServer().getScheduler().runTaskTimer(this, new EventTimer(getEventManager()), 0L, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getServer().getScheduler().cancelTasks(this);

        if (connector != null) {
            connector.close();
            connector = null;
        }

        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public BukkitAudiences getAdventure() {
        return adventure;
    }

    public GameEventManager getEventManager() {
        return eventManager;
    }

    public EventAutomationManager getAutomationManager() {
        return automationManager;
    }
}
