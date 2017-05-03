# AnimateStands 4 MythicMobs
build up on Bram Stout's ArmorStandAnimator Class, thx alot to him. Requires MythicMobs 4.0.0 or higher

### note this is heavy experimental!

*** 3.5.2017 update: added some sort of fake ai. use a mythicmob config to fake the ai and change the armorstands behaviors into it.
*** 3.5.2017 update: added skill "aschange" to change the animation file. see examples

```

mobfile:

moonwalker:
  Type: armor_stand
  Options:
    Invincible: true
    HasArms: true
    ItemBody: COS_GreenChest
    ItemFeet: COS_BlueFeet
    ItemHand: COS_IronSword
    ItemHead: BlackbeardHead
    ItemLegs: COS_YellowLegs
  Skills:
  - asinit{anim=Example1.anim;mobtype=aimob} @self ~onSpawn
  - asanimate{r=20;d=0} @self ~onTimer:20
  - skill{s=makeAttack} @trigger ~onAttack
  - skill{s=playDamage} @trigger ~onDamaged 
  - message{msg="You killed me!"} @trigger ~onSignal:DEATH
  
aimob:
  Type: zombie
  Options:
    Silent: true
    PreventOtherDrops: true
  AIGoalSelectors:
    - 0 clear
    - 1 meleeattack
    - 2 randomstroll
    - 3 float
  AITargetSelectors:
    - 0 clear
    - 1 players


skillfile:

playDamage:
  Cooldown: 1
  Skills:
  - aschange{anim=flip.anim} @self
  - delay 15
  - aschange{anim=Example1.anim} @self

makeAttack:
  Cooldown: 1
  Skills:
  - aschange{anim=winke.anim} @self
  - delay 8
  - damage{a=0.5} @trigger
  - delay 8
  - aschange{anim=Example1.anim} @self

```


*** 1.4.2017 update: added "executeSkill" option to anim file. If this option is set to a frame, the animatorstand will execute any metaskill if the frame is played. All mm targeters are useable
Example animfile:
```

Anim file:

interpolate
length 16
frame 0
Armorstand_Position 0.0 0.0 0.0 0.0
Armorstand_Middle -1.7735 -1.9591 0
Armorstand_Right_Leg 0 4.7462 12.0494
Armorstand_Left_Leg 0 -5.2681 -6.6472
Armorstand_Left_Arm 6.1476 0 -42.2486
Armorstand_Right_Arm 0 -3.1448 41.9762
Armorstand_Head 0 -24.967 -0.4532
frame 7
executeSkill particlestest @PIR{r=5}
Armorstand_Position 0.0 0.0 0.0 0.0
Armorstand_Middle 0 0 -2.4842
Armorstand_Right_Leg 0 0 13.5406
Armorstand_Left_Leg 0 -6.5557 -15.3523
Armorstand_Left_Arm -8.7445 0 -151.9446
Armorstand_Right_Arm 10.178 0 154.3655
Armorstand_Head 9.236 2.7374 -9.5706
frame 15
Armorstand_Position 0.0 0.0 0.0 0.0
Armorstand_Middle -1.7735 -1.9591 0
Armorstand_Right_Leg 0 4.7462 12.0494
Armorstand_Left_Leg 0 -5.2681 -6.6472
Armorstand_Left_Arm 6.1476 0 -42.2486
Armorstand_Right_Arm 0 -3.1448 41.9762
Armorstand_Head 0 -24.967 -0.4532


Skill file:
particlestest:
  Skills:
  - particles{particle=heart;amount=8;vSpread=0.5;hSpread=0.5;Spped=0.01;yoffset=1}
```


*** 10.3.2017 update: added "Animate_Negate" option for anim files. If this is set all positions of the armorstand (expect the World Position itself) will be negated. For compatibility with some animation programs.

### How to install?
Stop your server and copy the mmArmorStandAnimator.jar file into your plugins folder. After that restart the server.

### Usage?
1. You need an MythicMobs with the type of armor_stand. How to setup an armor_stand MythicMobs look here: http://www.mythicmobs.net/manual/doku.php/databases/mobs/options#armor_stands
2. You need atleast one animation that you can apply to your mob. You find one example in the folder "MythicMobs/Anims/" This is btw the folder where all your animations should be stored.
3. Setup the skills to make the MythicMobs an AnimateStand:

###Init the MythicMob:
The first thing to do is, initialize the MythicMobs as an AnimateStand. This can be done with the skill 
```
- asInit{anim=Example.anim;plate=false} @self ~onSpawn 1
```
This skill will setup the AnimateStand and assign the animation "Example.anim" to it. The option plate is optional. If false the armorstand will have no plate. The animation will only be loaded if it wasnt already cached befor. Now the mob is ready to be animated.
#### anim, or a = filename. The animfile have to be in the folder MythicMobs/Anims/Filename.anim
#### plate or base: true if the armorstand have a baseplate or false if not.
######
###Animate the MythicMob:
```
  - asAnimate{anim=Example.anim;r=20;d=0;plate=false} @self ~onTimer:20 1
```
This skill will now play the animation Example.anim for (r=20) 20 times with a delay of (d=0) 0 ticks per frame. There is an ~onTimer:20 1 trigger which will restart the skill again. The AnimateStand timer should not last longer than the MythicMobs timer. It was nessercary for an buildin Timer because MythicMobs clocks only ticks every 4 ticks. This might be a bit to slow for the animations.
###### anim or a: again the name of the animation.
###### r or repeat: how many times should the animation repeat.
###### d or delay: how fast the animation should be played. where 0 is every tick.
######
### Pause the AnimateStand:
With this skill you can pause the animation.
```
 - asPause @self
```
Simple as that. There are no other options.
######
### Restart the Animation again:
```
- asRun @self
```
Also pretty simple. No other options here neither.
######
### TargetConditions: - AnimateStandPaused
```
 - AnimateStandPaused
```
Meet the condition if the animation is paused or not if not.
######
### TargetConditions: - isAnimateStand
```
 - isAnimateStand
```
Meet the condition if the MythicMob is an AnimateStand or false if not.
#
#
Examples:
```
animator:
  Type: armor_stand
  Options:
    HasArms: true
    ItemBody: COS_GreenChest
    ItemFeet: COS_BlueFeet
    ItemHand: COS_IronSword
    ItemHead: BlackbeardHead
    ItemLegs: COS_YellowLegs
  Riding: Rudolf
  Skills:
  - asinit{anim=Example.anim} @self ~onSpawn 1
  - asanimate{anim=Example.anim;r=20;d=0} @self ~onTimer:20 1
  
Rudolf:
  Mobtype: rabbit
  Options:
    MovementSpeed: 0.1
    PreventRenaming: true
  Skills:
  - potion{type=INVISIBILITY;duration=4000;level=1} @self ~onSpawn 1
```
