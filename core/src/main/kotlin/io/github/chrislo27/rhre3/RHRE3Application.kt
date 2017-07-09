package io.github.chrislo27.rhre3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import io.github.chrislo27.toolboks.Toolboks
import io.github.chrislo27.toolboks.ToolboksGame
import io.github.chrislo27.toolboks.font.FreeTypeFont
import io.github.chrislo27.toolboks.logging.Logger


class RHRE3Application(logger: Logger, logToFile: Boolean)
    : ToolboksGame(logger, logToFile, RHRE3.VERSION, Pair(1280, 720), false) {

    private val fontFileHandle: FileHandle by lazy { Gdx.files.internal("fonts/rodin.otf") }
    private val fontAfterLoadFunction: FreeTypeFont.() -> Unit = {
        this.font!!.setFixedWidthGlyphs("1234567890/\\")
        this.font!!.data.setLineHeight(this.font!!.lineHeight * 0.6f)
    }

    override fun getTitle(): String =
            "Rhythm Heaven Remix Editor $version"

    override fun create() {
        super.create()
        Toolboks.LOGGER.info("RHRE3 $version is starting...")
    }

    override fun postRender() {
        super.postRender()

        batch.begin()
        defaultFont.draw(batch, "RHRE2 first pass", 50f, 50f)
        batch.end()
    }

    private fun createDefaultTTFParameter(): FreeTypeFontGenerator.FreeTypeFontParameter {
        return FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            magFilter = Texture.TextureFilter.Nearest
            minFilter = Texture.TextureFilter.Linear
            genMipMaps = false
            incremental = true
            size = 24
            characters = ""
        }
    }

    override fun createDefaultFont(): FreeTypeFont {
        return FreeTypeFont(fontFileHandle, emulatedSize, createDefaultTTFParameter())
                .setAfterLoad(fontAfterLoadFunction)
    }

    override fun createDefaultBorderedFont(): FreeTypeFont {
        return FreeTypeFont(fontFileHandle, emulatedSize, createDefaultTTFParameter()
                .apply {
                    borderWidth = 1.5f
                })
                .setAfterLoad(fontAfterLoadFunction)
    }
}