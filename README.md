# ShopWarps
Warp to a player's temporary shop

Modification from the original TeleportShop plugin (https://github.com/boxhock/ShopTeleport). Code has been cleaned to maximize performance along with added commands and features that I personally wanted.

CONFIG:
shop-time: Amount in seconds you want a player's shop to stay up. This prevents users who just play once and leave.
msg-length: How long a message can be before it's cut off

PERMS:
shopteleport.shop - Use shop command
shopteleport.delshop - Delete your own shop
shopteleport.delshop-others - Delete others' shops

COMMANDS:
"/shop" -- Goes to your shop if it exists; says default error message in config if not.
"/shop help" || "/shop ?" -- Help menu
"/shop msg <&6Colorblahblahblah>" -- Change the message of your shop if it exists
"/shop playername" -- Goes to playername's shop  if it exists; says default error message in config if not.
"/shop list <#>" -- List out all the shops currently up; # specifies the page number, only shows 6 shops at a time.
"/delshop <player>" -- Deletes your shop or a player's shop if given the permission or OP
"/shop reload" -- Reloads the config - NOT SHOPS.YML
