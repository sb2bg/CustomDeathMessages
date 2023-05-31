# CustomDeathMessages

<b>CustomDeathMessages</b> is a highly customizable, cross version plugin that allows you to edit any death message in the game. It comes with default messages, which you can modify, delete, or add to. Install the plugin, and everything is all set to go! Plugin works on all versions 1.8+.

### Features
Edit every death message in the game!
Toggleable and configurable global death messages.
Toggleable hover event that displays the original message.
Toggleable hover event that displays an item tooltip.
Toggleable lightning effect when a player is killed.
Built-in placeholders.
Customizable chance for a player to drop their head on death. Head name is customizable.
In-game command to change any config values with ease.
Hex colors for all messages by using &#hex
Toggleable and configurable messages sent to the victim and the killer for PvP deaths.
Update checker sends a message when the plugin has an update
Extensive and very customizable config, see the example below.

### Commands and Permissions

* The path argument autocompletes, so no need to go looking in config.
/cdm reload - Reloads the config after making a change.
  * permission: cdm.reload

* /cdm add <path> [new message] - Adds messages to config.
  * permission: cdm.modify

* /cdm list <path> - Lists the messages for a death message cause, and lists their index which allows you to delete specific messages.
  * permission: cdm.list

* /cdm remove <path> <index> - Deletes messages from config. The index is the number of the message you want to delete, obtained by '/cdm list <path>'.
  * permission: cdm.modify

* /cdm set flag <path> <true|false> - Toggles features in config.
  * permission: cdm.modify

* /cdm set message <path> <message> - Sets string messages in config.
  * permission: cdm.modify

* /cdm set number <path> <number> -Sets number values in config.
  * permission: cdm.modify

### Update Checker
* permission: cdm.updates
* allows players with this permission to receive a message when a plugin update is available.

### Config
```yaml

# Enable Update Messages: Toggles update messages on or off.
enable-update-messages: true

# To use hex colors: &#hex
# PvP Messages only for when a player is killed by another player, enabled by default
# Placeholders for pvp messages: %victim%, %victim-nick%, %killer%, %killer-nick%, %killer-x%, %killer-y%, %killer-z%, %victim-x%, %victim-y%, %victim-z%
enable-pvp-messages: true
killer-message: '&e[&cDeathMessages&e] &eYou killed &c%victim%'
victim-message: '&e[&cDeathMessages&e] &eYou were killed by &c%killer%'

# Toggleable features: set true to enable, false to disable. All features except lightning-effect are false by default.
# Drop Head Percentage: self-explanatory, chance of a player head to drop
# Do Lightning: creates a lightning effect that will not harm or damage anything when the victim is killed. True by default.
# Head Name: Sets the name of a head whenever dropped from a player.
head-name: "&c%victim%'s Head"
do-lightning: true

# Number between 0 and 1 (0 = 0%, 0.5 = 50%, 1 = 100%)
drop-head-percentage: 0.5

# Enable Global Messages: enables global death messages
enable-global-messages: true

# Original Hover Message: Show the original death message when hovering over the custom message.
original-hover-message: false

# You can add messages or remove any of these messages. Toggleable by boolean above.
# Placeholders for melee messages: %kill-weapon%, %victim%, %victim-nick%, %killer%, %killer-nick%, %killer-x%, %killer-y%, %killer-z%, %victim-x%, %victim-y%, %victim-z%
# Enable Item Hover: When hovering over the kill-weapons name, it will display the items enchantments, etc.
enable-item-hover: false
global-pvp-death-messages:
- "&c%victim% &ehas been killed by &c%killer%"
- "&c%victim% &ewas slain by &c%killer% &eusing &7[&b%kill-weapon%&7]"
- "&c%victim% &ehas been put down by &c%killer%"
- "&c%victim% &ewas slaughtered by &c%killer% &eusing &7[&b%kill-weapon%&7]"
- "&c%killer% &epulverized &c%victim% &eusing &7[&b%kill-weapon%&7]"
- "&c%victim% &ewas destroyed by &c%killer%&e"
- "&c%killer% &egave &c%victim% &ethe L"
- "&c%victim% &ewas EZ clapped by &c%killer% &eusing &7[&b%kill-weapon%&7]"

# Placeholders for melee messages: %victim%, %victim-nick%, %killer%, %killer-nick%, %killer-x%, %killer-y%, %killer-z%, %victim-x%, %victim-y%, %victim-z%
melee-death-messages:
- "&c%victim% &ewas slapped by &c%killer%"
- "&c%victim% &ewas KO'd &c%killer%"
- "&c%victim% &egot rocked by &c%killer%"

# Messages for entities with custom names. Will override their mob group message and display these instead, if enabled.
# Placeholders: %victim%, %victim-nick%, %entity-name%, %victim-x%, %victim-y%, %victim-z%
enable-custom-name-entity-messages: true
custom-name-entity-messages:
- "&c%victim% &egot rocked by %entity-name%"
- "&c%victim% &ewas put to sleep by %entity-name%"
- "%entity-name% &eate &c%victim%'s &esoul"
- "&c%victim% &ewishes they didn't fight %entity-name%"

# Placeholders for ALL below messages: %victim%, %victim-nick%, %entity-name%, %victim-x%, %victim-y%, %victim-z%
fall-damage-messages:
- "&c%victim% &emade themself a pancake on the ground"
- "&c%victim% &efell from a high spot"

drowning-messages:
- "&c%victim% &efound out the hard way they don't have gills"
- "&c%victim% &ethought they could make it to oxygen in time"

suffocation-messages:
- "&c%victim% &ecouldn't catch their breath while in a block"
- "&c%victim% &ecouldn't breathe while inside of a block"

cramming-messages:
- "&c%victim% &egot crushed and couldn't breathe"
- "&ePoor &c%victim%&e, they thought they could live in a 1x1 space"

thorns-messages:
- "&c%victim% &egot karma'd by &c%killer%'s &ethorns"
- "&c%victim% &egot the tables turned on them by &c%killer%'s &ethorns"

lava-messages:
- "&c%victim% &eburned up in a pit of lava"
- "&c%victim% &efell into a lava pool... say goodbye to his items"

magma-block-messages:
- "&c%victim% &ewas removed by a hot &6&lmagma block... &ejust crouch?"
- "&c%victim%'s &efeet were made into bacon by a &6magma block"

elytra-messages:
- "&c%victim% &eflew into a wall and died...?"
- "&c%victim% &egot slammed into a wall by his elytra"

wither-messages:
- "&c%victim% &egot the wither disease"
- "&c%victim% &ewithered away to the 4th dimension"

suicide-messages:
- "&c%victim% &ekilled themself"
- "&c%victim% &ehad a mental breakdown and died"

fire-messages:
- "&c%victim% &estood in the fireplace"
- "&c%victim% &eplayed with fire"

fire-tick-messages:
- "&c%victim% &eforgot to stop, drop, and roll"

starvation-messages:
- "&c%victim% &eforgot to eat"
- "&c%victim%'s &emom said we have food at home"

cactus-messages:
- "&c%victim% &ewas pricked to death"
- "&c%victim% &etried acupuncture, but died"
- "&c%victim% &e"

potion-messages:
- "&c%victim% &edied of black magic"
- "&c%victim% &edied of coronavirus"

void-messages:
- "&c%victim% &ethought it would be fun to take a trip in the void"

lightning-messages:
- "&c%victim% &ewas smitten by the gods"
- "&c%victim% &ewas struck down by the gods"

falling-block-messages:
- "&c%victim% &ewas crushed by a falling block"

freeze-messages:
- "&c%victim% &efroze to death. &b&lBrrrrr!"
- "&c%victim% &eforgot his winter jacket"

slime-messages:
- "&c%victim% &ewas crushed by a &a&lGIANT SLIME"
- "&c%victim% &ewas slimed to death while collecting slime balls"

zombie-messages:
- "&c%victim% &ewas eaten alive by a &a&lZombie"
- "&c%victim% &ethought a &a&lZombie &ewas friendly"

pigman-messages:
- "&c%victim% &eaggroed the &d&lPigmen &eand got eaten alive"

skeleton-messages:
- "&c%victim% &egot sniped by a bony &f&lSkeleton"
- "&c%victim% &egot shot by a &f&lskeleton"

magmacube-messages:
- "&c%victim% &egot crushed by a &c&lGIANT MAGMA CUBE"

husk-messages:
- "&c%victim% &egot eaten by a dessert dweller AKA a Husk"

spider-messages:
- "&c%victim% &etried his luck against his worst fear... &c&lA SPIDER!"

cavespider-messages:
- "&c%victim% &egot bitten to death by a &c&lCave Spider"

witherskeleton-messages:
- "&c%victim% &egot destroyed by a &f&lWither Skeleton &ewith a stone sword"

witherboss-messages:
- "&c%victim% &egot his head blown off by the Wither"
- "&c%victim% &egot a Wither skull to the face"


dragon-messages:
- "&c%victim% &egot his guts ripped out by the &d&lEnder Dragon"
- "&c%victim% &egot his head blown off by the &d&lEnder Dragon"
- "&cc%victim% &efound a respawn screen, not the end portal"

dragon-breath-messages:
- "&c%victim% &etook a bath in the &d&lEnder Dragon's &ebreath"
- "&c%victim% &etook a deep breath of the &d&lEnder Dragon's &e(nasty)"

elderguardian-messages:
- "&c%victim% &etried to clear a ocean monument but the &7&lElder Guardian &esaid no"

tnt-messages:
- "&c%victim% &egot his head blown off by &c&lTNT"
- "&c%victim% &egot blown up by &c&lTNT"
- "&e&lBOOM! &c%victim% &efound out about explosives the hard way"

end-crystal-messages:
- "&c%victim% &egot pieced by an &d&lEnd Crystal"
- "&c%victim% &egot blown up by an &d&lEnd Crystal"

creeper-messages:
- "&c%victim% &egot blown up by a &a&lCreeper Ahhhh Man"

ghast-messages:
- "&c%victim% &egot fireballed by a &f&lGhast"

enderman-messages:
- "&c%victim% &elooked an &d&lEnderman &ein the eyes"

silverfish-messages:
- "&c%victim% &egot eaten by a &7&lSilverfish"

witch-messages:
- "&c%victim% &edied to a &d&lWitch named Scarlett"

shulker-messages:
- "&c%victim% &edied trying to get some &d&lshulker &eshells"
- "&c%victim% &egot shot by a &d&lShulker"
- "&eLeviooosa! &c%victim% &egot ruined by a &d&lShulker"

guardian-messages:
- "&c%victim% &egot killed by a &7&lGuardian... &esomehow?"

golem-messages:
- "&c%victim% &emade an &7&lIron Golem &emad"

zombievillager-messages:
- "&c%victim% &ewas made dinner of by a &a&lZombie Villager"

endermite-messages:
- "&c%victim% &egot eaten by the &d&lEndermans &ehater, an &d&lEndermite"

phantom-messages:
- "&c%victim% &ewas kidnapped by a &3&lPhantom"

drowned-messages:
- "&c%victim% &ewent for a late night swim and got eaten by a &b&lDrowned Zombie"

pillager-messages:
- "&c%victim% &egot shot by a &7&lPillager"

ravager-messages:
- "&c%victim% &egot rammed by a &7&lRavager"

illusioner-messages:
- "&c%victim% &ewas sent to the illusion world by an Illusioner"
- "&c%victim% &ewas tricked by an &7&lIllusioner"
- "&c%victim% &ewas killed by an &7&lIllusioner"

bee-messages:
- "&c%victim% &ewas stung by a &lBEE"

wolf-messages:
- "&c%victim% &ewas ripped apart by a &f&lWolf"
- "&c%victim% &eprovoked a &f&lWolf"

llama-messages:
- "&c%victim% &ewas spit to death by a Llama"
- "&c%victim% &ecould not handle being spat on by a Llama"

blaze-messages:
- "&c%victim% &ewas charred by a &6&lBlaze"
- "&c%victim% &ewas turned into charcoal by a &6&lBlaze"

stray-messages:
- "&c%victim% &ewas domed by a Stray"

vex-messages:
- "&c%victim% &ewas eaten by a tiny flying (and annoying) &f&lVex"
- "&c%victim% &egot vexed by a &f&lVex"

vindicator-messages:
- "&c%victim% &ewas hunted down by a &f&lVindicator"
- "&c%victim% &efound Johnny... a &f&lVindicator?"

evoker-messages:
- "&c%victim% &ewas summoned to the dead by an &f&lEvoker"
- "&c%victim% &etried the wrath of an &f&lEvoker"
- "&c%victim% &ewas evoked by an &f&lEvoker"

pufferfish-messages:
- "&c%victim% &edied to a Pufferfish..."

polar-bear-messages:
- "&c%victim% &ewas murdered by a nice Polar Bear... &ewhich means they hit it! (someone kill them)"
- "&c%victim% &egot turned to snow by a &f&lPolar Bear"

dolphin-messages:
- "&c%victim% &eprovoked a Dolphin... and lost?"
- "&c%victim% &ehit a Dolphin and they retaliated"

panda-messages:
- "&c%victim% &ewas made into a Pandas dinner"
- "&c%victim% &etried stealing a Pandas bamboo"

piglin-messages:
- "&c%victim% &emet his maker against the new Piglins"
- "&c%victim% &emade the Piglins mad. I mean, we warned you, don't steal from them..."
- "&c%victim% &emisses Zombie Pigmen, so they took their anger out on a Piglin... and lost"

hoglin-messages:
- "&c%victim% &ewas charged by a Hoglin, kind of rude?"
- "&c%victim% &edidn't read the patch notes about Hoglins"

zoglin-messages:
- "&c%victim% &eate a Zoglins horn for dinner"
- "&c%victim% &ewas rammed by a Zoglin"
-
goat-messages:
- "&c%victim% &ewas headbutted by a Goat"
- "&c%victim% &ewas impaled by a Goat horn"

zombified-piglin-messages:
- "&c%victim% &etook a Piglin to the overworld and was murdered by their new, not so friendly pet"
- "&c%victim% &edied to a Piglin... I mean, Pigman... I mean, Zombified Piglin"

fox-messages:
- "&c%victim% &efoxed around and found out"
- "&c%victim% &ewas chewed up by a Fox"

warden-messages:
- "&c%victim% &ewas killed by a Warden... I mean, what did you expect?"
- "&c%victim% &egot sonic boomed by a Warden"
- "&eA Warden protected the Deep Dark from &c%victim%"

# This is for any messages that haven't been added, so if you die to a cause which doesn't have any messages,
# one of these messages will display instead. (Please report any missing messages in my spigot discussion)
unknown-messages:
- "&c%victim% &edied to unknown causes"
- "&c%victim% &edied"
- "&c%victim% &ewas killed"

fireball-messages:
- "&c%victim% &ewas fireballed until they burnt to death"

arrow-messages:
- "&c%victim% &elooked like a porcupine after getting shot to death"

# Only here to help me debug my plugin, not suggested you enable.
developer-mode: false
 ```