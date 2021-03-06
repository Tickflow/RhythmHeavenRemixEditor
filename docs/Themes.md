# Themes

You may have noticed that RHRE3 comes with some themes built-in.
Have you ever wanted to tweak it or make an entirely new one?

This is very simple to do!

Every time you start the editor, a folder `<user>/.rhre3/themes` will be made,
and inside of that folder will be one more folder called `examples`. Inside
the `examples` folder, every built-in theme will be put into a
`example_#.json` file, where the `#` is just a number. You can open
json files with a text editor
(like [Notepad++](https://notepad-plus-plus.org/), please don't use Notepad).

Below is a sample json file:<br>
```json
{
  "name" : "(Example) theme.light",
  "background" : "#EBEBEB",
  "trackLine" : "#191919",
  "waveform" : "#191919",
  "texture" : "<insert optional Base64 encoded RGBA8888 PNG here>",
  "trackers" : {
    "playback" : "#00FF00",
    "musicStart" : "#FF0000",
    "musicVolume" : "#FF6600",
    "tempoChange" : "#6666E5"
  },
  "entities" : {
    "selectionTint" : "#00BFBF",
    "nameColor" : "#000000",
    "cue" : "#D8D8D8",
    "pattern" : "#D8D8FF",
    "special" : "#FFD4BA",
    "equidistant" : "#FFB2BF",
    "keepTheBeat" : "#FFE27C"
  },
  "selection" : {
    "selectionFill" : "#19BFBF54",
    "selectionBorder" : "#19D8D8"
  }
}
```

> Note: the `subtitle` field name in entities was changed to `special` starting in v3.4.0.
Using `subtitle` still works, but `special` is now the correct field name.


> Note: as of `v3.6.0`, the `timeSignature` field inside `trackers` has been removed. The colour now uses the track line colour.

You'll notice that most of these are just RGB hex values. The `name`
is self-explanatory. Most of the colours are grouped into sections,
like `trackers` or `entities`.

> Note: if you see a longer hex colour like `#19BFBF54`, the last *two*
digits are alpha/transparency values. If left out, these default to `FF` (full opaque).

The **optional** `texture` field accepts a Base64 encoded PNG image. The `RGBA8888` simply
means each pixel is 32-bits, and has transparency. You can use a website
like [this one](https://www.browserling.com/tools/image-to-base64) to
convert your images for you. If this field is omitted, blank, or the
text is something like `<text here>` in angle brackets, no texture will
be loaded.
