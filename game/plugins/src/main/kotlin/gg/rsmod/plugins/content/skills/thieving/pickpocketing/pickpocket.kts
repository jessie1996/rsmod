package gg.rsmod.plugins.content.skills.thieving.pickpocketing

import gg.rsmod.plugins.content.combat.isAttacking
import gg.rsmod.plugins.content.combat.isBeingAttacked

PickpocketInfo.values().forEach { npcClass ->
    npcClass.ids.forEach { npcID ->
        if (!world.definitions.get(NpcDef::class.java, npcID).options.contains("Pickpocket")) {
            return@forEach
        }
        on_npc_option(npc = npcID, option = "pickpocket") {

            player.queue {
                val thievLvl: Int = player.getSkills().getCurrentLevel(Skills.THIEVING)
                if (thievLvl < npcClass.lvl) {
                    player.message("You need level ${npcClass.lvl} to pickpocket a ${npcClass.npcName}.")
                    return@queue
                }
                if (player.isAttacking() || player.isBeingAttacked()) {
                    player.message("You can't pickpocket while in combat!")
                    return@queue
                }
                if (!player.inventory.hasSpace) {
                    player.message("You don't have enough inventory space to pickpocket!")
                    return@queue
                }

                //pickpocketing animation and starting message
                player.animate(881)
                player.message("You attempt to pickpocket the ${npcClass.npcName}...")

                //wait 3 game cycles
                player.lock = LockState.FULL_WITH_ITEM_INTERACTION
                wait(3)
                player.lock = LockState.NONE

                //determine if the pickpocket was successful or not by "if random number is within success chances"
                val cap = if (player.hasEquipped(EquipmentType.GLOVES, Items.GLOVES_OF_SILENCE)) 5 else 0
                if (thievLvl.interpolate(55, 95, npcClass.lvl, 99, 100 - cap)) {

                    player.message("...and you succeed!")
                    val reward = npcClass.rewards.getRandom()
                    player.inventory.add(reward)
                    player.addXp(Skills.THIEVING, npcClass.exp)

                } else {
                    //if failed, sends relevant messages
                    player.message("...and you have failed.")

                    //damages player for a value in the npc's damage range
                    player.hit(npcClass.damage.random(), HitType.HIT)

                    //stuns the player then waits til the stun is done to continue
                    player.stun(npcClass.stunTicks)
                }
            }
        }
    }
}


