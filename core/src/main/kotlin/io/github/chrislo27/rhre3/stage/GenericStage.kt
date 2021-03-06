package io.github.chrislo27.rhre3.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Align
import io.github.chrislo27.rhre3.RHRE3
import io.github.chrislo27.toolboks.ToolboksScreen
import io.github.chrislo27.toolboks.registry.AssetRegistry
import io.github.chrislo27.toolboks.ui.*
import io.github.chrislo27.toolboks.util.MathHelper
import io.github.chrislo27.toolboks.util.gdxutils.drawQuad
import io.github.chrislo27.toolboks.util.gdxutils.fillRect
import kotlin.math.roundToInt


open class GenericStage<S : ToolboksScreen<*, *>>(override var palette: UIPalette, parent: UIElement<S>?,
                                                  camera: OrthographicCamera) : Stage<S>(parent, camera), Palettable {

    companion object {
        const val SCREEN_WIDTH = 0.85f
        const val SCREEN_HEIGHT = 0.9f
        val AREA = (RHRE3.WIDTH * SCREEN_WIDTH) to (RHRE3.HEIGHT * SCREEN_HEIGHT)

        const val PADDING: Float = 32f
        const val ICON_SIZE: Float = 96f
        const val FONT_SCALE: Float = (ICON_SIZE / 128f)
        const val BOTTOM_SIZE: Float = 96f

        val PADDING_RATIO = (PADDING / AREA.first) to (PADDING / AREA.second)
        val ICON_SIZE_RATIO = (ICON_SIZE / AREA.first) to (ICON_SIZE / AREA.second)
        val BOTTOM_RATIO = (BOTTOM_SIZE / AREA.first) to (BOTTOM_SIZE / AREA.second)

        var backgroundImpl: BackgroundImpl = BackgroundImpl.KARATE_MAN
    }

    enum class BackgroundImpl {

        TILED {
            override fun render(camera: OrthographicCamera, batch: SpriteBatch, shapeRenderer: ShapeRenderer) {
                batch.setColor(1f, 1f, 1f, 1f)
                val tex: Texture = AssetRegistry["ui_bg"]
                val start: Float = MathHelper.getSawtoothWave(5f) - 1f
                val ratioX = camera.viewportWidth / RHRE3.WIDTH
                val ratioY = camera.viewportHeight / RHRE3.HEIGHT
                for (x in (start * tex.width).toInt()..RHRE3.WIDTH step tex.width) {
                    for (y in (start * tex.height).toInt()..RHRE3.HEIGHT step tex.height) {
                        batch.draw(tex, x.toFloat() * ratioX, y.toFloat() * ratioY, tex.width * ratioX,
                                   tex.height * ratioY)
                    }
                }
            }
        },
        TENGOKU {
            private inner class Square(x: Float, y: Float,
                                       size: Float = MathUtils.random(20f, 80f),
                                       speedX: Float = MathUtils.random(0.075f, 0.2f),
                                       speedY: Float = -MathUtils.random(0.075f, 0.2f),
                                       rotSpeed: Float = MathUtils.random(90f, 200f) * MathUtils.randomSign(),
                                       rotation: Float = MathUtils.random(360f))
                : Particle(x, y, size, size, speedX, speedY, rotSpeed, rotation, "menu_bg_square")

            init {
                maxParticles = 40
            }

            private val hsv: FloatArray = FloatArray(3)

            val top: Color = Color.valueOf("4048e0")
            val bottom: Color = Color.valueOf("d020a0")
            var cycleSpeed: Float = 1f / 20

            override fun createParticle(initial: Boolean): Particle {
                return if (!initial) {
                    Square(-0.5f, 1f + MathUtils.random(1f))
                } else {
                    Square(MathUtils.random(1f), MathUtils.random(1f))
                }
            }

            override fun render(camera: OrthographicCamera, batch: SpriteBatch, shapeRenderer: ShapeRenderer) {
                val width = camera.viewportWidth
                val height = camera.viewportHeight
                val ratioX = width / RHRE3.WIDTH
                val ratioY = height / RHRE3.HEIGHT

                if (cycleSpeed > 0f) {
                    top.toHsv(hsv)
                    hsv[0] = (hsv[0] - Gdx.graphics.deltaTime * cycleSpeed * 360f) % 360f
                    top.fromHsv(hsv)
                    bottom.toHsv(hsv)
                    hsv[0] = (hsv[0] - Gdx.graphics.deltaTime * cycleSpeed * 360f) % 360f
                    bottom.fromHsv(hsv)
                }

                batch.drawQuad(0f, 0f, bottom, width, 0f, bottom,
                               width, height, top, 0f, height, top)

                super.render(camera, batch, shapeRenderer)

                // Remove OoB squares
                particles.removeIf {
                    it.x > 1f + (ratioX * it.sizeX) / width || it.y < -(ratioY * it.sizeY) / height
                }
            }
        },
        KARATE_MAN {
            private inner class Snowflake(x: Float, y: Float,
                                          size: Float = 54f,
                                          speedX: Float = -MathUtils.random(0.075f, 0.25f),
                                          speedY: Float = -MathUtils.random(0.05f, 0.1f))
                : Particle(x, y, size, size, speedX, speedY, 0f, 0f, "menu_snowflake")

            init {
                maxParticles = 32
            }

            val orangeTop: Color = Color.valueOf("CD3907")
            val orangeBottom: Color = Color.valueOf("FF9333")
            val blueTop: Color = Color.valueOf("27649A")
            val blueBottom: Color = Color.valueOf("72AED5")

            val top = Color()
            val bottom = Color()

            var cycleSpeed: Float = 1f / 10

            override fun createParticle(initial: Boolean): Particle {
                return if (!initial) {
                    Snowflake(1.25f, 0.25f + MathUtils.random(1f))
                } else {
                    Snowflake(MathUtils.random(1f), MathUtils.random(1f))
                }
            }

            override fun render(camera: OrthographicCamera, batch: SpriteBatch, shapeRenderer: ShapeRenderer) {
                val width = camera.viewportWidth
                val height = camera.viewportHeight
                val ratioX = width / RHRE3.WIDTH
                val ratioY = height / RHRE3.HEIGHT

                if (cycleSpeed > 0f) {
                    val percentage = MathHelper.getBaseCosineWave(1f / cycleSpeed)
                    top.set(blueTop)
                    bottom.set(blueBottom)

                    top.lerp(orangeTop, percentage)
                    bottom.lerp(orangeBottom, percentage)
                }

                batch.drawQuad(0f, 0f, bottom, width, 0f, bottom,
                               width, height, top, 0f, height, top)

                super.render(camera, batch, shapeRenderer)

                // Remove OoB particles
                particles.removeIf {
                    it.x < -(ratioX * it.sizeX) / width || it.y < -(ratioY * it.sizeY) / height
                }
            }
        };

        companion object {
            val VALUES: List<BackgroundImpl> = values().toList()
        }

        protected open class Particle(var x: Float, var y: Float,
                                      var sizeX: Float, var sizeY: Float,
                                      var speedX: Float, var speedY: Float,
                                      var rotSpeed: Float, var rotation: Float,
                                      var tex: String)

        protected var maxParticles: Int = 0
        protected val particles: MutableList<Particle> = mutableListOf()

        protected open fun createParticle(initial: Boolean): Particle? = null

        open fun render(camera: OrthographicCamera, batch: SpriteBatch, shapeRenderer: ShapeRenderer) {
            val width = camera.viewportWidth
            val height = camera.viewportHeight
            val ratioX = width / RHRE3.WIDTH
            val ratioY = height / RHRE3.HEIGHT

            if (maxParticles > 0 && particles.size < maxParticles) {
                val initial = particles.size == 0 && maxParticles > 1
                while (particles.size < (if (!initial) maxParticles else (maxParticles * 0.8f).roundToInt().coerceAtLeast(1))) {
                    val particle = createParticle(initial) ?: break
                    particles += particle
                }
            }

            // Render particles
            batch.setColor(1f, 1f, 1f, 0.65f)
            particles.forEach {
                it.x += it.speedX * Gdx.graphics.deltaTime
                it.y += it.speedY * Gdx.graphics.deltaTime
                it.rotation += it.rotSpeed * Gdx.graphics.deltaTime

                val tex = AssetRegistry.get<Texture>(it.tex)

                batch.draw(tex, it.x * width, it.y * width,
                           it.sizeX / 2, it.sizeY / 2, it.sizeX, it.sizeY, ratioX, ratioY, it.rotation,
                           0, 0, tex.width, tex.height, false, false)
            }
            batch.setColor(1f, 1f, 1f, 1f)
        }
    }

    var titleIcon: ImageLabel<S> = ImageLabel(palette, this, this).apply {
        this.alignment = Align.topLeft
        this.location.set(0f, 0f,
                          ICON_SIZE_RATIO.first, ICON_SIZE_RATIO.second,
                          0f, 0f, 0f, 0f)
        this.location.screenY += this.location.screenHeight
    }
    var titleLabel: TextLabel<S> = object : TextLabel<S>(palette, this, this) {
        override fun getFont(): BitmapFont {
            return palette.titleFont
        }
    }.apply {
        this.alignment = Align.topLeft
        this.location.set(PADDING_RATIO.first * 0.5f + ICON_SIZE_RATIO.first,
                          ICON_SIZE_RATIO.second)
        this.location.set(screenWidth = 1f - this.location.screenX, screenHeight = ICON_SIZE_RATIO.second)

        this.textAlign = Align.left + Align.center
        this.textWrapping = false
        this.background = false
        this.fontScaleMultiplier = FONT_SCALE
    }

    var centreStage: Stage<S> = Stage(this, camera).apply {
        this.alignment = Align.bottomLeft
        this.location.set(0f, BOTTOM_RATIO.second + PADDING_RATIO.second / 2f, 1f,
                          1f - (PADDING_RATIO.second / 2f + ICON_SIZE_RATIO.second) - (PADDING_RATIO.second / 2f + BOTTOM_RATIO.second))
    }

    var bottomStage: Stage<S> = Stage(this, camera).apply {
        this.alignment = Align.bottomLeft
        this.location.set(0f, 0f, 1f, BOTTOM_RATIO.second)
    }

    var onBackButtonClick: () -> Unit = {}
    val backButton: Button<S> = object : Button<S>(palette, bottomStage, bottomStage) {
        override fun onLeftClick(xPercent: Float, yPercent: Float) {
            super.onLeftClick(xPercent, yPercent)
            onBackButtonClick()
        }
    }.apply {
        this.visible = false
        this.stage.updatePositions()
        this.location.set(screenHeight = 1f)
        this.location.set(screenWidth = this.stage.percentageOfWidth(this.stage.location.realHeight))
        this.addLabel(ImageLabel(palette, this, this.stage).apply {
            this.image = TextureRegion(AssetRegistry.get<Texture>("ui_icon_back"))
        })
        bottomStage.elements += this
    }

    var drawBackground: Boolean = true

    init {
        this.location.screenWidth = SCREEN_WIDTH - PADDING_RATIO.first * 2
        this.location.screenHeight = SCREEN_HEIGHT - PADDING_RATIO.second * 2
        this.location.screenX = 0.5f - this.location.screenWidth / 2f
        this.location.screenY = 0.5f - this.location.screenHeight / 2f

        elements.apply {
            add(bottomStage)
            add(centreStage)
            add(titleIcon)
            add(titleLabel)
        }

        this.updatePositions()
    }

    override fun render(screen: S, batch: SpriteBatch,
                        shapeRenderer: ShapeRenderer) {
        val oldColor: Float = batch.packedColor
        if (drawBackground) {
            backgroundImpl.render(camera, batch, shapeRenderer)
            batch.setColor(1f, 1f, 1f, 1f)
        }
        batch.setColor(0f, 0f, 0f, 0.65f)
        batch.fillRect(location.realX - PADDING_RATIO.first * camera.viewportWidth,
                       location.realY - PADDING_RATIO.second * camera.viewportHeight,
                       location.realWidth + PADDING_RATIO.first * camera.viewportWidth * 2,
                       location.realHeight + PADDING_RATIO.second * camera.viewportHeight * 2)
        batch.setColor(1f, 1f, 1f, 1f)
        val height = (8f / RHRE3.WIDTH) * camera.viewportHeight
        batch.fillRect(location.realX + height,
                       centreStage.location.realY - PADDING_RATIO.second * camera.viewportHeight / 4f,
                       location.realWidth - height * 2,
                       height)

        batch.fillRect(location.realX + height,
                       titleLabel.location.realY - PADDING_RATIO.second * camera.viewportHeight / 4f,
                       location.realWidth - height * 2,
                       height)
        batch.setColor(oldColor)
        super.render(screen, batch, shapeRenderer)
    }
}