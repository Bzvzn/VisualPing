# 📍 VisualPing

A modern, highly performant, and database-free ping system for Minecraft Paper servers (1.26+). Inspired by modern AAA shooters, VisualPing allows players to visually mark locations and objects in the world for their teammates.

## ✨ Features

* **True Raytracing:** Pings stick exactly to the targeted wall or floor without clipping into blocks.
* **Customizable Colors:** Every player can set their own personal ping color using a Hex Code.
* **Database-Free (PDC):** All player data is stored extremely efficiently directly in the world's vanilla `PersistentDataContainer`. No SQLite/MySQL database required!
* **Modern TextDisplays:** Uses the latest Minecraft text entities with JOML scaling, drop shadows, and "X-Ray" vision (pings are visible through walls).
* **Anti-Spam:** Built-in cooldown system to prevent ping spamming (bypass available for admins).
* **Fully Configurable:** Duration, range, item, text size, and colors can all be adjusted in the config.

---

## 🛠️ Installation

1. Download the latest `VisualPing-1.0.jar` from the Releases page.
2. Drop the file into the `plugins/` folder of your Paper server.
3. Restart the server or use a plugin manager to load it.
4. (Optional) Adjust the generated `config.yml` to your liking.

---

## 🎮 Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/pingcolor <#HEX>` | Sets a custom particle and text color (e.g., `#FF5555`). | *None (open to all)* |
| `/pingcolor reset` | Resets the color to the server default. | *None (open to all)* |
| *(Place Ping)* | Sneak + Right-Click with the configured item (Default: Stick). | *None (open to all)* |
| *(Bypass Cooldown)* | Completely ignores the ping cooldown. | `visualping.bypass` |

---

## ⚙️ Configuration (`config.yml`)

```yaml
# ========================================== #
#           VisualPing Configuration         #
# ========================================== #

ping:
  # Which item must be held and right-clicked to ping?
  pointer-item: "STICK"
  
  # Maximum range of the ping in blocks
  max-distance: 50
  
  # How long should the ping be visible? (in seconds)
  duration-seconds: 5
  
  # How many seconds must a player wait before pinging again?
  cooldown-seconds: 3
  
  # The default color as a Hex Code (Fallback if a player hasn't set one)
  default-color: "#FFAA00" 
  
  # How many blocks above the target should the name hover?
  text-height-offset: 0.8
  
  # The size of the text (1.0 = Vanilla, 2.0 = Large)
  text-scale: 2.0
```

🛠️ For Developers (Compiling)
This project uses Gradle. To compile the plugin yourself:

1. Clone the repository.
2. Run ./gradlew build in the root directory.
3. The compiled .jar will be located in build/libs/.