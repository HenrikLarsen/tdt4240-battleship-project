package com.battleship.model.treasures.deprecated
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.battleship.model.treasures.Treasure

@Deprecated("Only gold coin, gold key and chest in use. Will be removed soon!")
class GoldBar(position: Vector2, rotate: Boolean) : Treasure(position) {
    override var dimension: Vector2 = Vector2(1f, 2f)
    override var name: String = "Shiny bar of gold"
    override var health: Int = 2
    override var sound: Sound = Gdx.audio.newSound(Gdx.files.internal("audio/goldbar_sound.mp3"))
    override var sprite: Sprite = Sprite(Texture("images/treasures/key.png"))
    override var type: TreasureType =
        TreasureType.GOLDKEY

    init {
        if (rotate) rotateTreasure()
    }
}
