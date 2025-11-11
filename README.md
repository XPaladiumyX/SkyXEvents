[![Discord](https://badgen.net/badge/icon/discord?icon=discord&label)](https://discord.gg/pTErYjTh5h)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-red.svg)](https://bitbucket.org/lbesson/ansi-colors)
[![Open Source? Yes!](https://badgen.net/badge/Open%20Source%20%3F/Yes%21/blue?icon=github)](https://github.com/Naereen/badges/)
[![Website](https://img.shields.io/website-up-down-green-red/http/shields.io.svg)](https://skyxnetwork.net)  

# 🎄 SkyXEvents, Random Chest / Gift Event Plugin (1.21+)

SkyXEvents adds **random event chests** to your Survival worlds! Perfect for Christmas, Easter, or any seasonal treasure hunt.

When the event triggers, a chest spawns at a random location. Players must search for it and loot its rewards.  
Each chest file (`chest1.yml`, `chest2.yml`, …) has **its own loot table**, allowing different rewards per event.

---

## ✨ Features

✅ Random chest spawning in configured worlds  
✅ Multiple chest presets (`chests/chest1.yml`, `chest2.yml`, etc.)  
✅ Fully configurable loot (commands, items, money, currencies...)  
✅ Broadcast messages & actionbar notifications  
✅ Sounds & particle effects when spawning or opening  
✅ Optional hologram support (HolographicDisplays / DecentHolograms if installed)  
✅ Protection system: prevents breaking unopened chests  
✅ Reload command (no restart required)  
✅ Very lightweight — no lag, async operations  
✅ Designed for seasonal events (Christmas gift hunt, treasure events, etc.)

---

## 📁 Plugin Structure
```/plugins/SkyXEvents/  
│ config.yml <-- global settings (timer, worlds allowed, etc.)  
│  
└── chests/  
│ chest1.yml <-- loot config for chest type #1  
│ chest2.yml <-- loot config for chest type #2  
└ …
```

Each chest.yml contains:

- **Rewards**
- **Message**
- **Sound**
- **Particle**
- Commands executed when opened (for example CoinsEngine, ItemsAdder items, etc.)

---

## 🛠 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/skyxevents reload` | Reloads all configuration files | `skyxevents.admin` |
| `/skyxevents force`  | Forces an event chest to spawn instantly | `skyxevents.admin` |

---

## ⚙️ Permissions

```yaml
skyxevents.admin
```

## 🧩 Dependencies (optional)

| Plugin | Purpose |
| --- | --- |
| ✅ DecentHolograms or HolographicDisplays | shows holograms above chests (optional) |

No dependencies are required. The plugin runs standalone.

---

## 🚀 Installation

1.  Download the plugin `.jar`
    
2.  Drop it into your `plugins/` folder
    
3.  Start the server
    
4.  Edit `config.yml` and chest files in `chests/`
    
5.  Use `/skyxevents reload` to apply changes

## 🧪 Example chest config

```yaml
chest:
  rewards:
    commands:
      - "eco give %player% 100000"
  message: "&aYou found a rare Christmas gift!"
  sound: "ENTITY_PLAYER_LEVELUP"
  particles: "VILLAGER_HAPPY"
```
---
## 🌎 Use Cases

🎁 Christmas Gift Hunt  
🐣 Easter Egg Hunt  
🗺 Treasure Hunting Events  
⚔ Special event loots during server updates

Perfect for seasonal hype events on your server.

---

## 🧑‍💻 Developer

Made with ❤️ by XPaladiumyX for SkyXNetwork

---

If you like the plugin, ⭐ star the repo on GitHub!

