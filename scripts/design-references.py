#!/usr/bin/env python3
"""Turn the designer's canvas export into the reference PNGs viddikDesignParity reads.

The canvas arrives as a directory with `canvas.json` and `png/<Artboard>.png` rendered at 2x.
A reference has to be the size of its fixture (`w` x `h` in canvas.json), so each PNG is reduced
by an exact integer factor with a box filter: every reference pixel is the mean of the source
pixels it covers, which is what a 1x render of the same artboard approximates. Nothing is
cropped or padded - an export whose size is not an integer multiple of its frame is refused.

    python3 scripts/design-references.py <canvas export dir> [<out dir>]

Needs Pillow. The default out dir is skin-plakat/src/desktopTest/snapshots/design.
"""
import json
import pathlib
import sys

from PIL import Image

DEFAULT_OUT = pathlib.Path("skin-plakat/src/desktopTest/snapshots/design")


def main(argv):
    if len(argv) not in (2, 3):
        sys.exit(__doc__)
    src = pathlib.Path(argv[1])
    out = pathlib.Path(argv[2]) if len(argv) == 3 else DEFAULT_OUT
    canvas = json.loads((src / "canvas.json").read_text(encoding="utf-8"))
    frames = [f for page in canvas["pages"] for f in page["frames"]]
    out.mkdir(parents=True, exist_ok=True)
    for frame in frames:
        image = Image.open(src / frame["png"])
        w, h = frame["w"], frame["h"]
        fx, fy = image.width / w, image.height / h
        if fx != fy or fx != int(fx) or fx < 1:
            sys.exit("{0}: {1}x{2} is not an integer multiple of the frame {3}x{4}".format(
                frame["name"], image.width, image.height, w, h))
        factor = int(fx)
        if image.mode not in ("RGB", "RGBA"):
            image = image.convert("RGBA")
        reference = image.reduce(factor) if factor > 1 else image
        reference.save(out / (frame["name"] + ".png"), optimize=True)
        print("{0:34} {1}x{2} -> {3}x{4}".format(frame["name"], image.width, image.height, w, h))
    print("{0} references written to {1}".format(len(frames), out))


if __name__ == "__main__":
    main(sys.argv)
