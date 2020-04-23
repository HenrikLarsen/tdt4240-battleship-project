package com.battleship.model.treasures

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.battleship.utility.SoundEffects
import com.battleship.utility.TextureLibrary

/**
 * Inherits behavior from [Treasure]
 *
 * @constructor inherited from [Treasure]
 * @property position: Vector2 - position of treasure on board grid
 * @property rotate: Boolean - describes if treasure is rotated, default: false
 */
class TreasureChest(position: Vector2, rotate: Boolean) : Treasure(position, rotate) {
    override var dimension: Vector2 = Vector2(2f, 2f)
    override var name: String = "Treasure chest"
    override var health: Int = 4
    override var sound: Sound = SoundEffects.TREASURECHEST
    override var sprite: Sprite = Sprite(TextureLibrary.TREASURECHEST)
    override var type: TreasureType = TreasureType.TREASURECHEST

    /**
     * If rotate is true init block triggers rotateTreasure after constructor setup
     */
    init { if (rotate) rotateTreasure() }
}
