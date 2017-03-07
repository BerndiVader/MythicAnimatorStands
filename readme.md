## Experimental animated ArmorStands
build up on Bram Stout's ArmorStandAnimator Class, thx alot to him.

Look into the MythicMobs folder for the examples.

1. Copy the MythicMobs folder into your plugins folder for the example mob and example anim file.

2. Copy the jar into your plugins folder.

3. Open the MythicMobs/Mobs/armorstand.yml and take a look how it works:

```
animator:
  Type: armor_stand
  Options:
    HasArms: true
  Skills:
  - asinit{anim=Example.anim;plate=false} @self ~onSpawn 1
  - asanimate{anim=Example.anim;r=20;d=0} @self ~onTimer:20 1
```

## the asinit mechanic:

#### This mechanic initialize the Armorstand to be an animated ArmorStand. Best to use it at spawn.
#### anim, or a = filename. The animfile have to be in the folder MythicMobs/Anims/Filename.anim
#### plate or base: true if the armorstand have a baseplate or false if not.

## the asanimate mechanic:

#### This mechanic animates the armorstand. use it with a timer. Althought it has its own buildin timer you should use the ~ontimer skill anyway. Try out what fit best for u.

#### anim or a: again the name of the animation.
#### r or repeat: how many times should the animation repeat.
#### d or delay: how fast the animation should be played. where 0 is every tick.


Again this is very experimental and only for use on test servers!




