package gg.rsmod.game.message.impl

import gg.rsmod.game.message.Message

/**
 * @author Tom <rspsmods@gmail.com>
 */
data class ItemActionOneMessage(val item: Int, val slot: Int, val interfaceHash: Int) : Message