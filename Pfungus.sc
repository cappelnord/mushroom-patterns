Pfungus : Pattern {

	classvar <fungi;


	var <>key, <>x, <>y, <>repeats;


	*new {|key="Clathrus archeri", x=0.5, y=0.5, repeats=inf|
		fungi.isNil.if {
			fungi = Dictionary();
		};

		^super.newCopyArgs(key, x, y, repeats);
	}



	embedInStream {|inval|

		var keyStream = key.asStream;
		var xStream = x.asStream;
		var yStream = y.asStream;

		repeats.do {
			var keyValue = keyStream.next(inval);
			var xValue = xStream.next(inval);
			var yValue = yStream.next(inval);
			Pfungus.sampleFungus(keyValue, xValue, yValue).yield;
		}
	}

	*sampleFungus {|key, x, y|
		var fungus = fungi[key];
		fungus.isNil.not.if({
			var width = fungus[\width];
			var height = fungus[\height];
			var array = fungus[\array];

			var ret;
			var idx;
			y = (1.0 - y);
			x = (x * width).floor.max(0).min(width-1).asInteger;
			y = (y * height).floor.max(0).min(height-1).asInteger;
			idx = (y * width + x).asInteger;
			ret = array[idx];
			^ret;
		}, {
			("Could not find fungus: " ++ key).postln;
			^0.0;
		});
	}

	*loadImage {|file|
		var image = Image.open(file);

		var pixels = image.pixels;
		var array = Array.newClear(pixels.size);

		// uses bitshifting to extract color channels from integers representing the color
		// calculates the average of the 3 color channels and stores it as a normalized float

		pixels.do {|c, i|
			var blue = c & 0x000000FF;
			var green = c & 0x0000FF00 >> 8;
			var red = c & 0x00FF0000 >> 16;
			// var value = (red + green + blue) / (3.0 * 256.0);
			var value = blue / 256.0;
			array[i] = value;
		};

		^(array: array, width: image.width, height: image.height);
	}

	*loadFungus {|file|
		var key = file.fileNameWithoutExtension;
		fungi[key] = Pfungus.loadImage(file.absolutePath);
		("\"" ++ key ++ "\"").postln;
	}

	*loadFungi {|path|
		var files;
		path.isNil.if {
			var extensionPath = PathName(Pfungus.class.filenameSymbol.asString).pathOnly;
			path = extensionPath ++ "fungi" +/+ "";
		};

		files = PathName(path).files;
		fungi = Dictionary();
		files.do {|file|
			Pfungus.loadFungus(file);
		};
	}
}
