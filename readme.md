# AnimatorStands 4 MythicMobs

## [DOWNLOAD](http://mc.hackerzlair.org:8080/job/MythicAnimatorStands/) [![Build Status](http://mc.hackerzlair.org:8080/job/MythicAnimatorStands/badge/icon)] <br>

**06.02.2019 update 0.471: moved project to gradle.
**17.10.2018 update 0.470a: added 1.13.x support.
#####
** 21.6.2018 update 0.468b: added aitarget targeter. See aitarget for details.
#####
** 16.6.2018 update 0.468a: fixed issue with aschange mechanic on unix based system.
#####
** 02.2.2018 update 0.467a: added 1.12.2 support.
#####
** 25.8.2017 update 0.466a: added 1.12.1 support.
#####
** 24.7.2017 update 0.465a: fixed bug where animatorstands are not interactable. Added aimobtargetdistance{d=RangedValue;a=true/false} condition
#####
** 19.6.2017 update 0.462a: added support for 1.12. Dropped support for 1.8
#####
** 6.6.2017 update 0.461a: dropped ProtocolLib support gone back to use invisibility for the ai mobs because of the poor damage handling for armorstands.
#####
** 29.5.2017 update 0.46a: some more internal improvements. fixed a DamageEntityEvent NPE if there is no ai for the animatorstand.
#####
** 28.5.2017 update 0.451a: fixed bug in loading animation.
#####
** 12.5.2017 update 0.44a: some more improvements. added noai animatorstands again.
#####
** 11.5.2017 update 0.42a: matured the code. dropped asanim and none aiMob. added animspeed to asinit mechanic. 
#####
*** 8.5.2017 update 0.41a: now using ProtocolLib. 
#####
*** 7.5.2017 update 0.40a: some major changes & bugfixes. Added depend to ProtocolLib for PacketEvent
#####
*** 5.5.2017 update 0.38a: fixed some minor bugs. Improved direction rotation if in movement & armorstand now looks at target if not moving.
#####
*** 4.5.2017 update 0.37a: added movement signals. Use "~onSignal:MOVESTOPP" & "~onSignal:MOVESTART" to control the idle or moving animation.
#####
*** 4.5.2017 update: fixed a server crash exception. some optimization. redid the ASANIMATE mechanic. Now only repeat and delay is needed.
#####
*** 3.5.2017 update: added some sort of fake ai. use a mythicmob config to fake the ai and change the armorstands behaviors into it.
#####
*** 3.5.2017 update: added skill "aschange" to change the animation file. see examples
#####
*** 1.4.2017 update: added "executeSkill" option to anim file. If this option is set to a frame, the animatorstand will execute any metaskill if the frame is played. All mm targeters are useable
#####
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
- asInit{anim=Example.anim;plate=false;mobtype=mythicmob} @self ~onSpawn 1
```
This skill will setup the AnimateStand and assign the animation "Example.anim" to it. The option plate is optional. If false the armorstand will have no plate. The animation will only be loaded if it wasnt already cached befor. Now the mob is ready to be animated.
#### anim, or a = filename. The animfile have to be in the folder MythicMobs/Anims/Filename.anim
#### plate or base: true if the armorstand have a baseplate or false if not.
#### mobtype: the mythicmob used for the ai. (required)
#### animspeed: the delay in ticks
######
###Change animation for the AnimatorStand:
```
  - aschange{anim=flip.anim;animspeed=xx} @self
```
This skill allows to change the animation. Useful for different animations on different events like damage attack and that stuff.
###### anim or a: filename of the new animation.
###### animspeed: the dealy in ticks
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
### Conditions: - AnimateStandPaused
```
 - AnimateStandPaused{a=true/false}
```
Meet the condition if the animation is paused or not if not.
######
### Conditions: - isAnimateStand
```
 - isAnimateStand
```
Meet the condition if the MythicMob is an AnimateStand or false if not.
######
### Conditions: - aimobtargetdistance
```
 - aimobtargetdistance{d=RangedValue;a=true/false}
```
Meet the condition if the animatorstand have a aimob and that aimob have a target where the distance rangedvalue meet or not.
######
### Targeter: - aitarget
```
@aitarget
```
If the animatorstand has an aimob use this targeter to target the aimobs target inside the animatorstand skills.
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
```
Example for MOVESTOPP & MOVESTART:

moonwalker:
  Type: armor_stand
  Options:
    Small: true
    HasArms: true
    ItemBody: COS_GreenChest
    ItemFeet: COS_BlueFeet
    ItemHand: COS_IronSword
    ItemHead: BlackbeardHead
    ItemLegs: COS_YellowLegs
  Skills:
  - asinit{anim=Example1.anim;mobtype=aimob} @self ~onSpawn
  - asanimate{r=20;d=0} @self ~onTimer:20
  - aspause @self ~onSignal:MOVESTOPP
  - asrun @self ~onSignal:MOVESTART
  - skill{s=makeAttack} @trigger ~onAttack
  - skill{s=playDamage} @trigger ~onDamaged 
  - message{msg="You killed me!"} @trigger ~onSignal:DEATH
  
aimob:
  Type: pigzombie
  Options:
    Silent: true
    PreventOtherDrops: true
	
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

```
moonwalker:
  Type: armor_stand
  Options:
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
  Type: pigzombie
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
```
Example animfile:

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
